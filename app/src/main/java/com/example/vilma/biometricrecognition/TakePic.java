package com.example.vilma.biometricrecognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by jclew on 1/29/2018.
 */

public class TakePic extends AsyncTask<String, Void, String > {

    Context context;
    String filePath;
    String handler;
    TakePicFragment mObj;
    boolean mExternalStorageWriteable = false;

    public TakePic(Context context, String handler){
        this.context = context;
        this.handler = handler;
    }

    public void dispatchTakePictureIntent() {

        if (context instanceof LoginActivity) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                // Create the File where the photo should go
                //initializes a File object
                File photoFile = null;
                try {
                    //creates an empty file in the enviroments photo directory.
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Toast.makeText(context, "dispatch pic error", Toast.LENGTH_SHORT).show();
                }// Continue only if the File was successfully created
                if (photoFile != null) {
                    //creates a Uri(a string of characters used to identify the file
                    Uri photoURI = FileProvider.getUriForFile(context,
                            "com.example.vilma.biometricrecognition.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivity(context, takePictureIntent, null);
                }
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        mExternalStorageWriteable = isExternalStorageMounted();

        if(mExternalStorageWriteable) {
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            //Creates an empty file in the default temporary-file directory, using the given prefix and
            //suffix to generate its name
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            filePath = image.getAbsolutePath();
            return image;
        }else{
            Toast.makeText(context, "External memory isn't writeable", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public boolean isExternalStorageMounted() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    protected String doInBackground(String... strings) {
        dispatchTakePictureIntent();
        return filePath;
    }

    @Override
    protected void onPostExecute(String result) {
    }

}
