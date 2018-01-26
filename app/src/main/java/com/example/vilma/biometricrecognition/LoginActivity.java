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

/**
 * MAIN CLASS
 * Created by vilma on 1/20/2018.
 */

/*
    All classes should extend BaseActivity to be able to see the toolbar
 */
public class LoginActivity extends BaseActivity implements TakePicFragment.PictureTakerListener {

    String photoPath;
    TextView txtSignup;
    EditText txtUsernameBox;
    String txtUsername = "";
    boolean registerRequir = false;
    TakePicFragment fragment = new TakePicFragment();
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

        txtUsernameBox.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                txtUsername = txtUsernameBox.getText().toString();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerInternt = new Intent(LoginActivity.this, Register.class);
                startActivity(registerInternt);
            }
        });
    }

    @Override
    public String getTxt() {
        return txtUsername;
    }

    //
    @Override
    public void picClick(String photoPath, String txtUsername) {
        this.photoPath = photoPath;
        File file = new File(photoPath);

        //This requirSatisfied is passing the bool logic from the database
        // then the next iteration of if checks if the picture that was taken does meet reqs
        //if fragment.requirSatisfied = true means the user is in the db
        if (fragment.requirSatisfied) {
            source = txtUsername + "_prime.jpg";
            target = txtUsername + file.getName();
            S3Upload upload = new S3Upload(getApplicationContext(), photoPath,
                    txtUsername + "_" + file.getName());
            upload.execute();
            comparePic(LoginActivity.this);
        }
    }

    /*
     *This method call ComparesPicture and if the accuracy is greater or equal than 80 it proceeds
     * to login, otherwise should prompt a toast message that login failed.
     */
    public void comparePic(Context context) {

        ComparePictures j = new ComparePictures(context, source, target);
        j.execute();

        if (j.getConfidence() >= 80) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(context,
                    "Please enter a valid picture", Toast.LENGTH_SHORT).show();
        }
    }
}


