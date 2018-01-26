package com.example.vilma.biometricrecognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakePicFragment extends Fragment{

    //I dont think we need this
    //creates a PictureTakerListener variable used to make the fragment
    PictureTakerListener picCommander;
    //makes sure there is a user name
    boolean requirSatisfied = false;
    //context
    Context context;
    //needed username
    String userName;
    //the path of the photo file
    String fragPhotoFilePath;
    //the actual button
    Button picButton;
    //variables to check is external storage is available
    boolean mExternalStorageWriteable = false;
    //database stuff
    DbManager dBManager = new DbManager();

    /*public static TakePicFragment newInstance(String userName) {
        TakePicFragment f = new TakePicFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("Username", userName);
        f.setArguments(args);
        return f;
    }*/

    //I dont think I need this
    //makes sure that the activity that this fragment is in checks the requirements before taking a pic
    public interface PictureTakerListener{
        void picClick(String photoPath, String userName);
        String getTxt();
    }

    //Tracks which Activity this fragment is called on
    @Override
    public void onAttach(Context context1) {
        context  = context1;
        super.onAttach(context);
        //tries to initializes the context into a PictureTakerListener Object
        try{
            picCommander = (PictureTakerListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnArticleSelectedListener");
        }
    }

    //same as an oncreate in an activity
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.take_pic_fragment, container, false);
        //Bundle args = getArguments();
        //userName =  args.getString("Username");
        picButton = (Button)view.findViewById(R.id.button);


        picButton.setOnClickListener(
                new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        //this method does like pretty much all the work....like seriously-_-
                        buttonClicked(v);
                    }
                }
        );

        return view;
    }

    //I think you can
    public void buttonClicked(View view){
        userName = picCommander.getTxt();
        checkTable(context, getActivity(), userName);
//        if(requirSatisfied) {
            dispatchTakePictureIntent();
            picCommander.picClick(fragPhotoFilePath, userName);
            picButton.setText("Different Picture?");
//        }else{
//            Toast.makeText(getActivity(), "Please choose a different username", Toast.LENGTH_LONG).show();
//        }
    }

    //this method checks to see if there is available external storage
    public boolean isExternalStorageMounted() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    //this method creates a hollow file with name of the dateformat.jpeg
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
            fragPhotoFilePath = image.getAbsolutePath();
            return image;
        }else{
            Toast.makeText(context, "External memory isn't writeable", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    //blag for available username;
    //th
    public void dispatchTakePictureIntent() {

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
                startActivity(takePictureIntent);
            }
        }
    }

    public void checkTable(Context context, Activity activity, String txtUsername) {
        DbManager.checkTable checkTable = new DbManager.checkTable(context, txtUsername);
        //Checking if username is empty or did not enter something valid
        if (txtUsername.equals("") || txtUsername.toString().equals("Enter a Username")) {
            Toast.makeText(context, "Please enter a valid username", Toast.LENGTH_LONG).show();
        } else {
            checkTable.execute();
        }

        if (dBManager.getLoginFlag()) {
            requirSatisfied = true;

        }
    }

}
