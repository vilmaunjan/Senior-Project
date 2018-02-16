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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;



//All classes should extend BaseActivity to be able to see the toolbar
public class Register extends BaseActivity implements TakePicFragment.PictureTakerListener {

    ImageView mImageView;
    EditText txtUsernameBox;
    String txtUsername;
    EditText txtFirstnameBox;
    EditText txtLastnameBox;
    Button btnRegister;

    String[] dataUser= new String[5];

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String fragPhotoFilePath = null;
    boolean requirSatisfied;


    @Override  //calls this method immediately when this activity is called
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //so it doesnt show any text in the toolbar

        initUI();
    }

    //is called on create to set up the view
    private void initUI() {
        mImageView = (ImageView) findViewById(R.id.image_view);
        txtUsernameBox = (EditText) findViewById(R.id.txtUsername);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        txtFirstnameBox = (EditText) findViewById(R.id.txtFirstname);
        txtLastnameBox = (EditText) findViewById(R.id.txtLastname);


        btnRegister.setOnClickListener(new View.OnClickListener(){

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
            dataUser[0] = txtUsernameBox.getText().toString();
            dataUser[1] = txtFirstnameBox.getText().toString();
            dataUser[2] = txtLastnameBox.getText().toString();
            if (txtUsername.equals("") || txtUsername.toString().equals("Enter a Username")
                    ||dataUser[1].equals("") || dataUser[1].toString().equals("Enter a Username")
                    ||dataUser[2].equals("") || dataUser[2].toString().equals("Enter a Username")) {
                Toast.makeText(this, "Please enter every field", Toast.LENGTH_LONG).show();
            }else{
                DbManager.checkTable checkTable = new DbManager.checkTable(this, txtUsername,this);
                checkTable.execute();
                try {
                    checkTable.get();
                } catch (InterruptedException e) {
                    Toast.makeText(this, "Interrupted Exception", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    Toast.makeText(this, "Execution Exception", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

        }else{
            Toast.makeText(this, "Please take a picture first and enter a username!", Toast.LENGTH_LONG).show();
        }
    }

    //method below allows TakePicFragment to grab the username the user typed in
    @Override
    public String getTxt(){
        return txtUsername= txtUsernameBox.getText().toString();
    }
    //grabs the photopath from the TakePicFragment and sets pic
    @Override
    public void picClick(String fragPhotoFilePath, String txtUsername) {
        this.txtUsername = txtUsername;
        this.fragPhotoFilePath = fragPhotoFilePath;
        //Toast.makeText(this, "we did it", Toast.LENGTH_SHORT).show();
    }


    //handles what happens when we need to create a new user into the database
    public void initializeResult(Boolean result){
        requirSatisfied = result;
        if(requirSatisfied){
            Toast.makeText(this, "Please enter a unique username", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Welcome to Hertz!", Toast.LENGTH_SHORT).show();
            DbManager.createItem createItem = new DbManager.createItem(this, dataUser);
            createItem.execute();

            try {
                createItem.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            String source = txtUsername + "_prime.jpg";
            S3Upload upload = new S3Upload(this, fragPhotoFilePath, source);
            upload.execute();

            Intent registerIntent = new Intent(Register.this, LoginActivity.class);
            startActivity(registerIntent);
            //Toast.makeText(this, "Check everything bud i think you did it", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Nice Pic!", Toast.LENGTH_LONG).show();
            setFullImageFromFilePath(fragPhotoFilePath,mImageView);
        }
    }

    private void setFullImageFromFilePath(String imagePath, ImageView imageView) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        mImageView.setImageBitmap(bitmap);
        //rotateImage(bitmap);
    }

    private void rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface = null;
        try{
            exifInterface = new ExifInterface(fragPhotoFilePath);
        }catch (IOException e){
            e.printStackTrace();
        }

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();

        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
            default:
        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
        mImageView.setImageBitmap(rotatedBitmap);
    }
}
