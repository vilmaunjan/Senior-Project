package com.example.vilma.biometricrecognition;

/**
 * Created by Vilma on 1/21/2018.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import android.content.Context;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Regions;
//import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.model.S3DataSource;

import java.util.List;

public class ComparePictures extends AsyncTask<Object, String, String> {

    private Context mContext;
    private String mSource;
    private String mTarget;
    private String confidence;
    private static AmazonRekognitionClient rekognitionClient;
    private static Float similarityThreshold = 80F;
    private static String bucket = Constants.BUCKET_NAME;

    public String getConfidence() {
        return confidence;
    }
    //String photo = "photo.jpg";
    //String source = "My_Face.jpg";
    //String source = "My_Face2.jpg";

    public ComparePictures(Context context, String source, String target){
        mContext = context;
        mSource = source;
        mTarget = target;
    }

    @Override
    protected String doInBackground(Object... params) {
        //String source = (String)params[0];
        //String target = (String)params[1];

        rekognitionClient = Util.getRekognitionClient(mContext);

        CompareFacesRequest request = new CompareFacesRequest()
                .withSourceImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(mSource).withBucket(bucket)))
                .withTargetImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(mTarget).withBucket(bucket)))
                .withSimilarityThreshold(similarityThreshold);

        // Call operation
        CompareFacesResult compareFacesResult=rekognitionClient.compareFaces(request);

        // Display results
        List <CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
        for (CompareFacesMatch match: faceDetails){
            ComparedFace face= match.getFace();
            BoundingBox position = face.getBoundingBox();
            confidence = face.getConfidence().toString();
            System.out.println("Face at " + position.getLeft().toString()
                    + " " + position.getTop()
                    + " matches with " + face.getConfidence().toString()
                    + "% confidence.");
            //Toast.makeText(mContext,face.getConfidence().toString()+"% confidence.",Toast.LENGTH_SHORT).show();
        }

        return null;
    }


    protected void onPostExecute(String result) {
        Toast toast = Toast.makeText(this.mContext, confidence, Toast.LENGTH_SHORT);
        toast.show();
    }
/*
    public static void comparePic(Context context, String source, String target) {

        rekognitionClient = Util.getRekognitionClient(context);

        CompareFacesRequest request = new CompareFacesRequest()
                .withSourceImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(source).withBucket(bucket)))
                .withTargetImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(target).withBucket(bucket)))
                .withSimilarityThreshold(similarityThreshold);

        // Call operation
        CompareFacesResult compareFacesResult=rekognitionClient.compareFaces(request);

        // Display results
        List <CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
        for (CompareFacesMatch match: faceDetails){
            ComparedFace face= match.getFace();
            BoundingBox position = face.getBoundingBox();
            System.out.println("Face at " + position.getLeft().toString()
                    + " " + position.getTop()
                    + " matches with " + face.getConfidence().toString()
                    + "% confidence.");
            Toast.makeText(context,face.getConfidence().toString()+"% confidence.",Toast.LENGTH_SHORT).show();
        }
        List<ComparedFace> uncompared = compareFacesResult.getUnmatchedFaces();

        System.out.println("There were " + uncompared.size()
                + " that did not match");
        System.out.println("Source image rotation: " + compareFacesResult.getSourceImageOrientationCorrection());
        System.out.println("target image rotation: " + compareFacesResult.getTargetImageOrientationCorrection());


        //Toast.makeText(context,"hi",Toast.LENGTH_SHORT).show();

    }
*/

}