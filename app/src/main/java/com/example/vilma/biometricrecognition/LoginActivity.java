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

    String photoPath;
    TextView txtSignup;
    EditText txtUsernameBox;
    String txtUsername;
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
    public void picClick(String photoPath, String txtUname) {


        //This requirSatisfied is passing the bool logic from the database
        // then the next iteration of if checks if the picture that was taken does meet reqs
        //if fragment.requirSatisfied = true means the user is in the db
        //if (fragment.requirSatisfied) { //If user is in the db
        //    this.photoPath = photoPath;
        //    File file = new File(photoPath);
        //    source = txtUname + "_prime.jpg";
        //   target = txtUname +"_" +file.getName();
        //    S3Upload upload = new S3Upload(getApplicationContext(), photoPath, target);
        //    upload.execute();
            //comparePic(LoginActivity.this);
        //}

    }

    /*
     *This method call ComparesPicture and if the accuracy is greater or equal than 80 it proceeds
     * to login, otherwise should prompt a toast message that login failed.
     */
    /*public void comparePic(Context context) {

        ComparePictures j = new ComparePictures(context, "My_Face.jpg", "My_Face2.jpg",this);
        j.execute();
    }
*/
    /*
    public void onBackgroundRekogTaskCompleted(Float result){
        final Float THRESHOLD = 80F;

         //*For some reason, it doesnt let me assign "result" to the confidence variable inside this
         //* method. So the only way to use the result value passed from ComparePictures is inside of
         //* this method. That is why I have to do the confidence check here.
         //*
        if (result >= THRESHOLD) {
            //flag = "Greater than 80!";
            Intent intent = new Intent(this, HomeActivity.class);
            this.startActivity(intent);
            Toast.makeText(this,
                    "lOGING IN!!!", Toast.LENGTH_SHORT).show();
        } else {
            //flag = "Less than 80!";
            Toast.makeText(this,
                    "CANNOT LOGIN!!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackgroundDBTaskCompleted(Boolean result){
        if (result == true){ //User exists in database
            comparePic(LoginActivity.this);
        }else{ //when result==false, user does not exist in database

        }
    }

    */
}


