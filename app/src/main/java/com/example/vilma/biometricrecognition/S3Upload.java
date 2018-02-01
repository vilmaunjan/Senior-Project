package com.example.vilma.biometricrecognition;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

/**
 * Created by Vilma on 1/20/2018.
 */

public class S3Upload extends AsyncTask<String,Void,String>{

    private Context mContext;
    private String filePath;
    private String upFileName;
    private static String bucket = Constants.BUCKET_NAME;

    public S3Upload(Context context, String file, String fileName){
            mContext = context;
            filePath = file;
            upFileName = fileName;
    }

    @Override
    protected String doInBackground(String... params) {
        File file = new File(filePath);
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(Constants.ACCESS_KEY_ID,
                Constants.SECRET_ACCESS_KEY_);

        AmazonS3Client s3Client = new AmazonS3Client(awsCreds);

        PutObjectRequest putRequest1 = new PutObjectRequest(bucket,upFileName,file);
        PutObjectResult response1 = s3Client.putObject(putRequest1);
        return null;
    }

    @Override
    protected void onPostExecute(String result) {

    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected void onProgressUpdate(Void... values) {}

}


