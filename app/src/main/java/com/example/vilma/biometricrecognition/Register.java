package com.example.vilma.biometricrecognition;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.*;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

/**
 * Created by vilma on 1/20/2018.
 */

/*
    All classes should extend BaseActivity to be able to see the toolbar
 */
public class Register extends BaseActivity {


    Button btnTakePic;
    Button btnRegister;
    ImageView mImageView;
    EditText txtUsername;


    private String mCurrentPhotoPath = null;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    // TAG for logging;
    private static final String TAG = "UploadActivity";

    //Below is taken from https://docs.aws.amazon.com/sdkfornet1/latest/apidocs/html
    //                             /T_Amazon_S3_Transfer_TransferUtility.htm
    // The TransferUtility is the primary class for managing transfer to S3
    //TransferUtility provides a simple API for uploading content to and downloading content from
    // Amazon S3. It makes extensive use of Amazon S3 multipart uploads to achieve enhanced
    // throughput, performance, and reliability. When uploading large files by specifying file
    // paths instead of a stream, TransferUtility uses multiple threads to upload multiple parts
    // of a single upload at once. When dealing with large content sizes and high bandwidth, this
    // can increase throughput significantly.
    private TransferUtility transferUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        transferUtility = Util.getTransferUtility(this);
        initUI();
    }

    private void initUI() {
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnTakePic = (Button) findViewById(R.id.btnProfilePic);
        mImageView = (ImageView) findViewById(R.id.image_view);
        txtUsername = (EditText) findViewById(R.id.txtUsername);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean registerRequir = checkrequirements();

                DbManager.createItem createItem = new DbManager.createItem(view.getContext(),(Activity)view.getContext());
                createItem.execute();

                if (registerRequir) {
                    BeginUpload upload = new BeginUpload(getApplicationContext(), mCurrentPhotoPath);
                    upload.execute();
                }
            }
        });


        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

    }

    private boolean checkrequirements() {
        if (txtUsername.getText().toString().equals("") ||
                txtUsername.getText().toString().equals("Enter a Username")) {
            Toast.makeText(this, "Please enter a unique username", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = txtUsername.getText().toString()+"_prime";


        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            //initializes a File object
            File photoFile = null;
            try {
                //creates an empty file in the enviroments photo directory.
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "dispatch pic error", Toast.LENGTH_SHORT).show();
            }// Continue only if the File was successfully created
            if (photoFile != null) {
                //creates a Uri(a string of characters used to identify the file
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.vilma.biometricrecognition.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //checks to see if the activity is "all good"

            case (REQUEST_IMAGE_CAPTURE): {

                if (resultCode == RESULT_OK) {
                    setPic();
                    btnTakePic.setText("Different Picture?");
                }

            }
            case (2): {
                    /*if (resultCode == Activity.RESULT_OK) {

                        //grabs the info from the intents data and set the uri to that data
                        Uri uri = data.getData();

                        //this tries to begin the upload to the s3 bucket using the path of the picture selected
                        try {
                            //grabs path of picture
                            String path = getPath(uri);
                            //calls the begin method, and sends the path of the picture we are uploading.
                            beginUpload(path);
                        } catch (URISyntaxException e) {
                            Toast.makeText(this,
                                    "Unable to get the file from the given URI.  See error log for details",
                                    Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Unable to upload file from the given uri", e);
                        }
                    }*/
                Toast.makeText(this, "hey", Toast.LENGTH_LONG);
            }
        }
    }

    private class BeginUpload extends AsyncTask<String, Void, String> {

        private Context mContext;
        private String filePath;

        public BeginUpload(Context context, String string) {
            mContext = context;
            filePath = string;
        }

        @Override
        protected String doInBackground(String... params) {
            File file = new File(filePath);
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(Constants.ACCESS_KEY_ID,
                    Constants.SECRET_ACCESS_KEY_);

            AmazonS3Client s3Client = new AmazonS3Client(awsCreds);

            //File file = new File(S3JavaSDKExample.class.getResource(fileName).toURI());

            PutObjectRequest putRequest1 = new PutObjectRequest(Constants.BUCKET_NAME, txtUsername.getText().toString(), file);
            PutObjectResult response1 = s3Client.putObject(putRequest1);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast toast = Toast.makeText(mContext, "Upload to S3 complete", Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
        ///////////////////////////////////////FORTH ATTEMPT////////////////////////////////////////
        //the code below is an example
        /*BasicAWSCredentials awsCreds = new BasicAWSCredentials(Constants.ACCESS_KEY_ID,
                Constants.SECRET_ACCESS_KEY_);

        AmazonS3Client s3Client = new AmazonS3Client(awsCreds);

        //File file = new File(S3JavaSDKExample.class.getResource(fileName).toURI());

        PutObjectRequest putRequest1 = new PutObjectRequest(Constants.BUCKET_NAME,txtUsername.getText().toString(),filePath);
        PutObjectResult response1 = s3Client.putObject(putRequest1);
        */

    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        bitmap = RotateBitmap(bitmap, 270);
        mImageView.setImageBitmap(bitmap);
    }

    @SuppressLint("NewApi")
    private String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }






}
