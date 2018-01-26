package com.example.vilma.biometricrecognition;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.URISyntaxException;


//All classes should extend BaseActivity to be able to see the toolbar
public class Register extends BaseActivity implements TakePicFragment.PictureTakerListener {

    ImageView mImageView;
    EditText txtUsernameBox;
    String txtUsername;
    private String mCurrentPhotoPath = null;
    boolean registerRequir = false;
    //the fragment
    //checks if the register requirements have been verified


    @Override  //calls this method immediately when this activity is called
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUI();
    }

    //is called on create to set up the view
    private void initUI() {
        mImageView = (ImageView) findViewById(R.id.image_view);
        txtUsernameBox = (EditText) findViewById(R.id.txtUsername);

        txtUsernameBox.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                    txtUsername = txtUsernameBox.getText().toString();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        //Bundle args = new Bundle();
        //args.putString("Username", txtUsername);
        //frag.setArguments(args);
    }

    @Override
    public String getTxt(){
        return txtUsername;
    }
    //grabs the photopath from the TakePicFragment and sets pic
    @Override
    public void picClick(String mCurrentPhotoPath, String txtUsername) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
        S3Upload upload =  new S3Upload(getApplicationContext(), mCurrentPhotoPath,
                txtUsername + "_prime.jpg");
        upload.execute();
        setPic();
    }

    //rotates the pic depending on the float you send it
    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    //sets the picture depending on the mCurrentPhoto
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
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        bitmap = RotateBitmap(bitmap,270);
        mImageView.setImageBitmap(bitmap);
    }
}
