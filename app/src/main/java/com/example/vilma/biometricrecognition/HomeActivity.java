package com.example.vilma.biometricrecognition;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

/**
 * Created by vilma on 1/20/2018.
 */

/*
    All classes should extend BaseActivity to be able to see the toolbar
 */
public class HomeActivity extends BaseActivity {

    Button btnAccount;
    Button btnCheckout;
    TextView usernameTxtView;
    String txtFirstname;
    String txtLastname;
    String txtUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home);
        //Use frame layout instead of setContentView to include the menu and toolbar
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_home, contentFrameLayout);

        initUI();
    }

    private void initUI() {
        btnAccount = (Button) findViewById(R.id.btnAccount);
        btnCheckout = (Button) findViewById(R.id.btnCheckout);
        usernameTxtView = findViewById(R.id.txtName);

        Intent intentExtras = getIntent();
        Bundle extraBundle = intentExtras.getExtras();
        //txtFirstname = extraBundle.getString("FirstName");
        //txtLastname = extraBundle.getString("LastName");
        //txtUsername = extraBundle.getString("Username");

        //Do this so that the user info can be stored for all activities, and so we dont need to pass from 1 activity to another
        SharedPreferences prefs = this.getSharedPreferences("MyPref",MODE_PRIVATE);
        txtFirstname = prefs.getString("FirstName",null);
        txtLastname = prefs.getString("LastName",null);
        txtUsername = prefs.getString("Username",null);

        usernameTxtView.setText(txtFirstname+" "+txtLastname);
//        usernameTxtView.setText(txtUsername);

        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAccount = new Intent(HomeActivity.this,AccountActivity.class);
                startActivity(intentAccount);
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ComparePictures j = new ComparePictures(HomeActivity.this,"My_Face.jpg", "My_Face2.jpg");
                //j.execute();
                //ComparePictures.comparePic(HomeActivity.this,"My_Face.jpg", "My_Face2.jpg");
            }
        });
    }
}
