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


        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, Register.class);
                startActivity(registerIntent);
            }
        });
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

    public void initializeResult(Boolean result){
        requirSatisfied = result;
    }

    public void checkTable(Context context, Activity activity, String txtUsername) {
        DbManager.checkTable checkTable = new DbManager.checkTable(this, txtUsername,this);

        //If none/wrong input
        if (txtUsername.equals("") || txtUsername.toString().equals("Enter a Username")) {
            Toast.makeText(context, "Please enter a valid username", Toast.LENGTH_LONG).show();
        } else { //Else If the user entered something in the text box
            checkTable.execute();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            checkTable(this,this,txtUsername);
            if(requirSatisfied){
                Toast.makeText(this, "Please enter your username or register a new account!"
                        , Toast.LENGTH_SHORT).show();

            }else{
                File file = new File(fragPhotoFilePath);
                String source = txtUsername + "_" + file.getName();

                //Toast.makeText(this, "Upload to s3 started", Toast.LENGTH_SHORT).show();
                S3Upload upload = new S3Upload(this, fragPhotoFilePath, source);
                upload.execute();
                try {
                    upload.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                String target = txtUsername + "_prime.jpg";
                ComparePictures j = new ComparePictures(this, source, target, this);
                j.execute();
                try {
                    j.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    void compareFinish(Float result){
        if(result>80){
            Toast.makeText(this,"You made a match of " + result.toString() +"!"
                    ,Toast.LENGTH_LONG).show();
            Intent registerIntent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(registerIntent);
        }else{
            Toast.makeText(this,"Picture doesn't match Prime", Toast.LENGTH_LONG).show();
        }
    }

}


