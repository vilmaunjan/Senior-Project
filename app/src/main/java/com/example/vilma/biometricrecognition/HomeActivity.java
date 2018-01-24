package com.example.vilma.biometricrecognition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

/**
 * Created by vilma on 1/20/2018.
 */

/*
    All classes should extend BaseActivity to be able to see the toolbar
 */
public class HomeActivity extends BaseActivity {

    Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
    }

    private void initUI() {
        btnCheckout = (Button) findViewById(R.id.btnCheckout);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComparePictures j = new ComparePictures(HomeActivity.this,"My_Face.jpg", "My_Face2.jpg");
                j.execute();
                //ComparePictures.comparePic(HomeActivity.this,"My_Face.jpg", "My_Face2.jpg");
            }
        });
    }
}
