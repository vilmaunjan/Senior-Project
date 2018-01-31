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
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TakePicFragment<T> extends Fragment{

    //I dont think we need this
    //creates a PictureTakerListener variable used to make the fragment
    PictureTakerListener picCommander;
    //makes sure there is a user name
    boolean requirSatisfied;
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
        fragPhotoFilePath = Util.dispatchTakePictureIntent(context);
        picCommander.picClick(fragPhotoFilePath, userName);
        /*checkTable(context, getActivity(), userName);
        if(requirSatisfied){
            fragPhotoFilePath = Util.dispatchTakePictureIntent(context);
            picCommander.picClick(fragPhotoFilePath, userName);
        }else{
            Toast.makeText(context, "Please enter a unique username", Toast.LENGTH_LONG).show();
        }
        */
    }

    public void initializeResult(Boolean result){
        requirSatisfied = result;
    }


    /** This method receives the confidence value from ComparePictures class. The loginIntent has
     * to be done in this class because the confidence("result") cannot be passed to another class.
     */


    public void uploadAndCompare(String uploadFile) {

        picButton.setText("Different Picture?");
        fragPhotoFilePath = uploadFile;
        String source = userName + "_prime.jpg";
        if (context instanceof LoginActivity){
            //Store in S3
            File file = new File(fragPhotoFilePath);
            String target = userName + "_" + file.getName();
            S3Upload uploadL = new S3Upload(context, fragPhotoFilePath, target);
            uploadL.execute();

            //Compares pictures and also calls onBackgroundRekogTaskCompleted() automatically
            ComparePictures j = new ComparePictures(context, source, target, this);
            j.execute();

    }else{
            //Store in s3
            S3Upload uploadR = new S3Upload(context, fragPhotoFilePath, source);
            uploadR.execute();
            Toast.makeText(getActivity(), "REGISTRATION SUCCEED, NOW TRY TO LOGIN", Toast.LENGTH_LONG).show();

            //After taking register pic and storing it in s3, go to Login activity
            Intent intent = new Intent(context, LoginActivity.class);
            this.startActivity(intent);

        }
    }



}
