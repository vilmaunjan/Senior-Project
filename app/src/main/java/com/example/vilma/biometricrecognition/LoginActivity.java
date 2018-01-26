package com.example.vilma.biometricrecognition;

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
                new updateTable().execute();
                startActivity(intent);
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

    private class updateTable extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            ManagerClass managerClass = new ManagerClass();
            CognitoCachingCredentialsProvider credentialsProvider = managerClass.getCredentials(LoginActivity.this);

            AccountsDO accountsDo = new AccountsDO();
            try{
                accountsDo.setUserId(txtUsername.getText().toString());

            }catch(Exception e){
                Toast.makeText(LoginActivity.this, "something broke", Toast.LENGTH_LONG).show();
            }

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
                Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
            } else if (string.equals("2")) {
                Toast.makeText(LoginActivity.this, "Login unsuccessful", Toast.LENGTH_SHORT).show();
            }

        }
    }
}


