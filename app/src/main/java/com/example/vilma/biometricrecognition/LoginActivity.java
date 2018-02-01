package com.example.vilma.biometricrecognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.EditText;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.io.File;
import java.util.concurrent.ExecutionException;


/**
 * MAIN CLASS
 * Created by vilma on 1/20/2018.
 */

/*
    All classes should extend BaseActivity to be able to see the toolbar
 */
public class LoginActivity extends BaseActivity implements TakePicFragment.PictureTakerListener {

    String fragPhotoFilePath;
    TextView txtSignup;
    EditText txtUsernameBox;
    Button btnLogin;
    String txtUsername;
    boolean requirSatisfied;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String source;
    String target;
    //This function hold all database connectivity initialization.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
        //Use frame layout instead of setContentView to include the menu and toolbar
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_login, contentFrameLayout);

        initUI();

    }


    private void initUI() {
        txtUsernameBox = (EditText) findViewById(R.id.txtUsername);
        txtSignup = (TextView) findViewById(R.id.txtSignup);
        btnLogin = (Button) findViewById(R.id.button2);

        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, Register.class);
                startActivity(registerIntent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //this method does like pretty much all the work....like seriously-_-
                tryToUpload(v);
            }
        });
    }

    private void tryToUpload(View v) {
        if(fragPhotoFilePath != null){
            txtUsername = txtUsernameBox.getText().toString();
            if (txtUsername.equals("") || txtUsername.toString().equals("Enter a Username")) {
                Toast.makeText(this, "Please enter a valid username", Toast.LENGTH_LONG).show();
            }else{
                DbManager.checkTable checkTable = new DbManager.checkTable(this, txtUsername,this);
                checkTable.execute();
                try {
                    checkTable.get();
                } catch (InterruptedException e) {
                    Toast.makeText(this, "shit we broke it", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    Toast.makeText(this, "shit we broke it", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

        }else{
            Toast.makeText(this, "Please take a picture first and enter a username!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public String getTxt() {
        return txtUsername = txtUsernameBox.getText().toString();
    }

    @Override
    public void picClick(String fragPhotoFilePath, String txtUsername) {
        this.fragPhotoFilePath = fragPhotoFilePath;
        this.txtUsername = txtUsername;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Nice Pic!", Toast.LENGTH_LONG).show();
        }
    }

    public void initializeResult(Boolean result){
        requirSatisfied = result;
        if(requirSatisfied){
            Toast.makeText(this, "check done", Toast.LENGTH_LONG).show();
            File file = new File(fragPhotoFilePath);
            String target = txtUsername + "_" + file.getName();
            String source = txtUsername + "_prime.jpg";

            S3Upload upload = new S3Upload(this, fragPhotoFilePath, target);
            upload.execute();

            try {
                upload.get();
            } catch (InterruptedException e) {
                Toast.makeText(this, "IM SORRY vilma", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (ExecutionException e) {
                Toast.makeText(this, "IM SORRY gabe", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            Intent intentBundle = new Intent(LoginActivity.this,ThisYouActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("PhotoPath", fragPhotoFilePath);
            bundle.putString("Target",target);
            bundle.putString("Source",source);
            bundle.putString("Username", txtUsername);
            intentBundle.putExtras(bundle);
            startActivity(intentBundle);

            /*ComparePictures j = new ComparePictures(this, source, target, this);
            j.execute();
            try {
                j.get();
            } catch (InterruptedException e) {
                Toast.makeText(this, "IM SORRY vilma", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (ExecutionException e) {
                Toast.makeText(this, "IM SORRY gabe", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            */

        }else{
            Toast.makeText(this, "Please enter your username or register a new account!"
                    , Toast.LENGTH_SHORT).show();


        }
    }

}


