package com.example.vilma.biometricrecognition;

/**
 * Created by Vilma on 1/21/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static android.support.v4.content.ContextCompat.startActivity;

/*
 * Handles basic helper functions used throughout the app.
 */
public class Util {

    // We only need one instance of the clients and credentials provider
    private static AmazonS3Client sS3Client;
    private static AmazonRekognitionClient rekClient;
    private static CognitoCachingCredentialsProvider sCredProvider;
    private static TransferUtility sTransferUtility;

    /**
     * Gets an instance of CognitoCachingCredentialsProvider which is
     * constructed using the given Context.
     *
     * @param context An Context instance.
     * @return A default credential provider.
     */
    private static CognitoCachingCredentialsProvider getCredProvider(Context context) {
        if (sCredProvider == null) {
            sCredProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(),
                    Constants.COGNITO_POOL_ID,
                    Regions.fromName(Constants.COGNITO_POOL_REGION));
        }
        return sCredProvider;
    }

    /**
     * Gets an instance of a S3 client which is constructed using the given
     * Context.
     *
     * @param context An Context instance.
     * @return A default S3 client.
     */
    public static AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {
            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()));
            sS3Client.setRegion(Region.getRegion(Regions.fromName(Constants.BUCKET_REGION)));
        }
        return sS3Client;
    }

    /**
     * Gets an instance of a rekognition client which is constructed using the given
     * Context.
     *
     * @param context An Context instance.
     * @return A default S3 client.
     */
    public static AmazonRekognitionClient getRekognitionClient(Context context) {
        if (rekClient == null) {
            rekClient = new AmazonRekognitionClient(getCredProvider(context.getApplicationContext()));
        }
        return rekClient;
    }


    /**
     * Gets an instance of the TransferUtility which is constructed using the
     * given Context
     *
     * @param context
     * @return a TransferUtility instance
     */
    public static TransferUtility getTransferUtility(Context context) {
        if (sTransferUtility == null) {
            sTransferUtility = new TransferUtility(getS3Client(context.getApplicationContext()),
                    context.getApplicationContext());
        }

        return sTransferUtility;
    }

    /**
     * Converts number of bytes into proper scale.
     *
     * @param bytes number of bytes to be converted.
     * @return A string that represents the bytes in a proper scale.
     */
    public static String getBytesString(long bytes) {
        String[] quantifiers = new String[] {
                "KB", "MB", "GB", "TB"
        };
        double speedNum = bytes;
        for (int i = 0;; i++) {
            if (i >= quantifiers.length) {
                return "";
            }
            speedNum /= 1024;
            if (speedNum < 512) {
                return String.format("%.2f", speedNum) + " " + quantifiers[i];
            }
        }
    }

    /**
     * Copies the data from the passed in Uri, to a new file for use with the
     * Transfer Service
     *
     * @param context
     * @param uri
     * @return
     * @throws IOException
     */
    public static File copyContentUriToFile(Context context, Uri uri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(uri);
        File copiedData = new File(context.getDir("SampleImagesDir", Context.MODE_PRIVATE), UUID
                .randomUUID().toString());
        copiedData.createNewFile();

        FileOutputStream fos = new FileOutputStream(copiedData);
        byte[] buf = new byte[2046];
        int read = -1;
        while ((read = is.read(buf)) != -1) {
            fos.write(buf, 0, read);
        }

        fos.flush();
        fos.close();

        return copiedData;
    }

    /*
     * Fills in the map with information in the observer so that it can be used
     * with a SimpleAdapter to populate the UI
     */
    public static void fillMap(Map<String, Object> map, TransferObserver observer, boolean isChecked) {
        int progress = (int) ((double) observer.getBytesTransferred() * 100 / observer
                .getBytesTotal());
        map.put("id", observer.getId());
        map.put("checked", isChecked);
        map.put("fileName", observer.getAbsoluteFilePath());
        map.put("progress", progress);
        map.put("bytes",
                getBytesString(observer.getBytesTransferred()) + "/"
                        + getBytesString(observer.getBytesTotal()));
        map.put("state", observer.getState());
        map.put("percentage", progress + "%");
    }

    public static String dispatchTakePictureIntent(Context context, Activity activity) {
        String fragPhotoFile;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            //initializes a File object
            File photoFile = null;
            try {
                //creates an empty file in the enviroments photo directory.
                photoFile = createImageFile(context);
            } catch (IOException ex) {
                Toast.makeText(context, "dispatch pic error", Toast.LENGTH_SHORT).show();
            }// Continue only if the File was successfully created
            if (photoFile != null) {
                fragPhotoFile = photoFile.getAbsolutePath();
                //creates a Uri(a string of characters used to identify the file
                Uri photoURI = FileProvider.getUriForFile(context,
                       "com.example.vilma.biometricrecognition.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                return fragPhotoFile;
            }
        }
        return null;
    }

    private static File createImageFile(Context context) throws IOException {
        // Create an image file name
        //String fragPhotoFilePath;
        boolean mExternalStorageWriteable = false;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        mExternalStorageWriteable = isExternalStorageMounted();

        if(mExternalStorageWriteable) {
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            //Creates an empty file in the default temporary-file directory, using the given prefix and
            //suffix to generate its name
            File image = File.createTempFile(
                    imageFileName,   //prefix
                    ".jpg",          //suffix
                    storageDir       //directory
            );

            // Save a file: path for use with ACTION_VIEW intents
            //fragPhotoFilePath = image.getAbsolutePath();
            return image;
        }else{
            Toast.makeText(context, "External memory isn't writeable", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public static boolean isExternalStorageMounted() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


}
