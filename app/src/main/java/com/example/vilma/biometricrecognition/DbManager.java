package com.example.vilma.biometricrecognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;

import java.util.concurrent.ExecutionException;

/**
 * Created by root on 1/25/18.
 */

/*  This class has the functions to interact the database
*   Since the dynamoDB calls have to be asynchronous
*   They are dealing with threads
*
*   Calling Convection: */


public class DbManager {

    public static String prime_pic;
    static Context currentContext;
//    static Activity currentActivity;

    public static class createItem extends AsyncTask<String, Void, Boolean> {
        String txtUsername;
        String txtFirstname;
        String txtLastname;

        // Save the context received via constructor in a local variable
        // it also saves the activity of the current view
        public createItem(Context context, String[] Username) {
            currentContext = context;
            txtUsername = Username[0];
            txtFirstname = Username[1];
            txtLastname = Username[2];

        }

        @Override
        protected Boolean doInBackground(String... strings) {
//          Looper.prepare();

            ManagerClass managerClass = new ManagerClass();
            CognitoCachingCredentialsProvider credentialsProvider = managerClass.getCredentials(currentContext);
            DynamoDBMapper dynamoDBMapper = managerClass.initDynamoClient(credentialsProvider);

            AccountsDO accountsDo = new AccountsDO();
//            accountsDo.setUserId(txtUsername.getText().toString());
            accountsDo.setUserId(txtUsername); //Set the name of the  object
            // It queries the database for the current set Username
            DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
                    .withHashKeyValues(accountsDo);

            PaginatedList<AccountsDO> result = dynamoDBMapper.query(AccountsDO.class, queryExpression);
            accountsDo.setFirstName(txtFirstname);
            accountsDo.setLastName(txtLastname);
            accountsDo.setPic1(txtUsername+"_prime.jpg");
            // it creates new item in DynamoDB
            dynamoDBMapper.save(accountsDo);
            return null;
        }

        protected void onPostExecute(Boolean result) {
        }
    }


    public static class checkTable extends AsyncTask<String, Void, Boolean> {
        String txtUsername;
        String[] dataUser = new String[5];

        public static boolean loginFlag;
        LoginActivity lObj = null; //Need to have this to pass the loginFlag to LoginActivity
        Register rObj = null;
        //Constructor
        public checkTable(Context context, String Username, Register obj) {
            currentContext = context;
            txtUsername = Username;
            rObj = obj;
        }

        public checkTable(Context context, String Username, LoginActivity obj) {
            currentContext = context;
            txtUsername = Username;
            lObj = obj;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
//          Looper.prepare();

            //Get credentials from aws DynamoDB
            ManagerClass managerClass = new ManagerClass();
            CognitoCachingCredentialsProvider credentialsProvider = managerClass.getCredentials(currentContext);
            DynamoDBMapper dynamoDBMapper = managerClass.initDynamoClient(credentialsProvider);

            AccountsDO accountsDo = new AccountsDO();
            accountsDo.setUserId(txtUsername);

            // It queries the database for the current set Username
            DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
                    .withHashKeyValues(accountsDo);

            PaginatedList<AccountsDO> result = dynamoDBMapper.query(AccountsDO.class, queryExpression);

            // if not in the database its going to ask to prompt again
            if (result.isEmpty()) {//Unsuccessful
                loginFlag = false;
            } else { //Success
               DbManager.prime_pic =result.get(0).getPic1();
               dataUser[0] = result.get(0).getUserId();
               dataUser[1] = result.get(0).getFirstName();
               dataUser[2] = result.get(0).getLastName();

                loginFlag = true;
            }
            return loginFlag;
        }

        protected void onPostExecute(Boolean result) {
            if(rObj != null){
                rObj.initializeResult(result);
            }
            if(lObj != null){
                lObj.getUserData(dataUser);
                lObj.initializeResult(result);
            }
        }
    }
}


