package com.example.vilma.biometricrecognition;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

/**
 * MAIN CLASS
 * Created by vilma on 1/20/2018.
 */

/*
    All classes should extend BaseActivity to be able to see the toolbar
 */



public class LoginActivity extends BaseActivity {

    Button btnLogin;
    TextView txtSignup;
    EditText txtUsername;

    DbManager dBManager = new DbManager();





//    DynamoDBMapper dynamoDBMapper;
//


    /*
    This function hold all database connectivity initialization.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        initUI();

    }


    private void initUI() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtSignup = (TextView) findViewById(R.id.txtSignup);
        txtUsername = (EditText) findViewById(R.id.txtUsername);




        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                DbManager.checkTable checkTable = new DbManager.checkTable(view.getContext(),(Activity) view.getContext());
                //Checking if username is empty or did not enter something valid
                if (txtUsername.getText().toString().equals("") ||
                        txtUsername.getText().toString().equals("Enter a Username")) {
                    Toast.makeText(LoginActivity.this, "Please enter a valid username", Toast.LENGTH_LONG).show();

                }else {
                    checkTable.execute();
                   
                }
                if(dBManager.getLoginFlag()){

                    startActivity(intent);
                }




            }
        });

        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, Register.class);

                startActivity(intent);
            }
        });
    }


}


