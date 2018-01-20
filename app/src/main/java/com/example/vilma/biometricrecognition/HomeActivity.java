package com.example.vilma.biometricrecognition;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Created by vilma on 1/20/2018.
 */

/*
    All classes should extend BaseActivity to be able to see the toolbar
 */
public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}
