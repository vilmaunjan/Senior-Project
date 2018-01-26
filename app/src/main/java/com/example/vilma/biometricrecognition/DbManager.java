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
    static Activity currentActivity;
    static Intent currentIntent;


    public static class createItem extends AsyncTask<String, Void, String> {
        EditText txtUsername;


        // Save the context received via constructor in a local variable
        // it also saves the activity of the current view
        public createItem(Context context, Activity activity) {
            currentContext = context;
            txtUsername = (EditText) activity.findViewById(R.id.txtUsername);
        }

        @Override
        protected String doInBackground(String... strings) {
            Looper.prepare();

            ManagerClass managerClass = new ManagerClass();
            CognitoCachingCredentialsProvider credentialsProvider = managerClass.getCredentials(currentContext);

            AccountsDO accountsDo = new AccountsDO();
            accountsDo.setUserId(txtUsername.getText().toString());
            accountsDo.setPic1(txtUsername.getText().toString() + "_prime.jpg");


            if (credentialsProvider != null && accountsDo != null) {

                DynamoDBMapper dynamoDBMapper = managerClass.initDynamoClient(credentialsProvider);
                dynamoDBMapper.save(accountsDo);


            } else {
                return ("2");

            }
            return ("1");

        }

        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            if (string.equals("1")) {
                Toast.makeText(currentActivity, "Successful", Toast.LENGTH_SHORT).show();
            } else if (string.equals("2")) {
                Toast.makeText(currentActivity, "Unsuccessful", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public static class checkTable extends AsyncTask<String, Void, String> {
        EditText txtUsername;

        // Save the context received via constructor in a local variable
        // it also saves the activity of the current view

        public checkTable(Context context, Activity activity) {
            currentContext = context;
            currentActivity = activity;
            txtUsername = (EditText) activity.findViewById(R.id.txtUsername);
        }

        @Override
        protected String doInBackground(String... strings) {
            Looper.prepare();

            ManagerClass managerClass = new ManagerClass();
            CognitoCachingCredentialsProvider credentialsProvider = managerClass.getCredentials(currentContext);
            DynamoDBMapper dynamoDBMapper = managerClass.initDynamoClient(credentialsProvider);

            AccountsDO accountsDo = new AccountsDO();
            accountsDo.setUserId(txtUsername.getText().toString());

            //Checking if username is empty or did not enter something valid
            if (txtUsername.getText().toString().equals("") ||
                    txtUsername.getText().toString().equals("Enter a Username")) {
                Toast.makeText(currentContext, "Please enter a unique username", Toast.LENGTH_LONG).show();

            }


            // It queries the database for the current set Username
            DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
                    .withHashKeyValues(accountsDo);

            PaginatedList<AccountsDO> result = dynamoDBMapper.query(AccountsDO.class, queryExpression);

            // if not in the database its going to ask to prompt again
            if (result.isEmpty()) {
                Toast.makeText(currentContext, "Please Enter A Valid UserName or Register", Toast.LENGTH_LONG).show();
            } else {
                prime_pic = result.get(0).getPic1().toString() ;

                Toast.makeText(currentContext, "Thank you", Toast.LENGTH_LONG).show();
            }
            return "success";
        }

/*
            try{
                accountsDo.setUserId(txtUsername.getText().toString());




            }catch(Exception e){
                Toast.makeText(currentContext, "something broke", Toast.LENGTH_LONG).show();
            }*/
/*
            if (credentialsProvider != null && accountsDo != null) {

                DynamoDBMapper dynamoDBMapper = managerClass.initDynamoClient(credentialsProvider);
                dynamoDBMapper.save(accountsDo);

            } else {
                return ("2");

            }

            return ("1");
        }*/
/*
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            if (string.equals("1")) {
                Toast.makeText(currentContext, "Welcome", Toast.LENGTH_SHORT).show();
            } else if (string.equals("2")) {
                Toast.makeText(currentContext, "Login unsuccessful", Toast.LENGTH_SHORT).show();
            }*/

    }
}

