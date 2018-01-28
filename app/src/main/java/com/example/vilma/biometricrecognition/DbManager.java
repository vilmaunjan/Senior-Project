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


    //THS IS NOT USED AND DOESNT WORK
    public String getPrime_pic(){
        return getPrime_pic();
    }

    public static class createItem extends AsyncTask<String, Void, String> {
        String txtUsername;

        // Save the context received via constructor in a local variable
        // it also saves the activity of the current view
        public createItem(Context context, String Username) {
            currentContext = context;
//            txtUsername = (EditText) activity.findViewById(R.id.txtUsername);
            txtUsername = Username;
        }

        @Override
        protected String doInBackground(String... strings) {
//            Looper.prepare();

            ManagerClass managerClass = new ManagerClass();
            CognitoCachingCredentialsProvider credentialsProvider = managerClass.getCredentials(currentContext);
            DynamoDBMapper dynamoDBMapper = managerClass.initDynamoClient(credentialsProvider);

            AccountsDO accountsDo = new AccountsDO();
//            accountsDo.setUserId(txtUsername.getText().toString());
            accountsDo.setUserId(txtUsername);
            // It queries the database for the current set Username
            DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
                    .withHashKeyValues(accountsDo);

            PaginatedList<AccountsDO> result = dynamoDBMapper.query(AccountsDO.class, queryExpression);

            accountsDo.setPic1(txtUsername+"_prime.jpg");
//            accountsDo.setPic1(txtUsername.getText().toString() + "_prime.jpg");

            // if the set Username does not match the database then it create new item
            if (!accountsDo.getUserId().equals(result.get(0).getUserId())) {
                // it creates new item in DynamoDB
                dynamoDBMapper.save(accountsDo);
                return ("1");
            } else {
                return ("2");
            }
        }




        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            if (string.equals("1")) {
                // Name is valid
                Toast.makeText(currentContext, "Successful", Toast.LENGTH_LONG).show();
            } else if (string.equals("2")) {
                // if not in the database its going to ask to prompt again
                Toast.makeText(currentContext, "Account Name Already Exists", Toast.LENGTH_LONG).show();
            }

        }
    }


    public static class checkTable extends AsyncTask<String, Void, Boolean> {
        String txtUsername;
        public static boolean loginFlag;
        TakePicFragment mObj; //Need to have this to pass the loginFlag to LoginActivity

        //Constructor
        public checkTable(Context context, String Username, TakePicFragment obj) {
            currentContext = context;
//            currentActivity = activity;
//            txtUsername = (EditText) activity.findViewById(R.id.txtUsername);
            txtUsername = Username;
            //mActivity = activity;
            mObj = obj;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
//            Looper.prepare();

            //Get credentials from aws DynamoDB
            ManagerClass managerClass = new ManagerClass();
            CognitoCachingCredentialsProvider credentialsProvider = managerClass.getCredentials(currentContext);
            DynamoDBMapper dynamoDBMapper = managerClass.initDynamoClient(credentialsProvider);

            AccountsDO accountsDo = new AccountsDO();
//            accountsDo.setUserId(txtUsername.getText().toString());
            accountsDo.setUserId(txtUsername);

            // It queries the database for the current set Username
            DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
                    .withHashKeyValues(accountsDo);

            PaginatedList<AccountsDO> result = dynamoDBMapper.query(AccountsDO.class, queryExpression);

            // if not in the database its going to ask to prompt again
            if (result.isEmpty()) {
                loginFlag = false;
                //return "unsuccessful";
            } else {

               DbManager.prime_pic =result.get(0).getPic1();
                loginFlag = true;
                //return "success";
            }
            return loginFlag;

        }

        protected void onPostExecute(Boolean result) {
           mObj.onBackgroundDBTaskCompleted(result);
/*
            // if not in the database its going to ask to prompt again
            if (result.equals("unsuccessful")) {
                Toast.makeText(currentContext, "Please Enter A Valid UserName or Register", Toast.LENGTH_LONG).show();
                //loginFlag = false;
            } else if (result.equals("success")) {

                Toast.makeText(currentContext, "Thank you", Toast.LENGTH_LONG).show();
                //loginFlag = true;

            }
*/
        }
    }
}


