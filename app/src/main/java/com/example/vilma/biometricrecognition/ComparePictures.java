package com.example.vilma.biometricrecognition;

/**
 * Created by Vilma on 1/21/2018.
 */

import android.content.Context;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Regions;
//import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.s3.model.S3DataSource;

import java.util.List;

public class ComparePictures {

    private AmazonRekognitionClient rekognitionClient;
    private Float similarityThreshold = 80F;
    String photo = "photo.jpg";
    String bucket = "S3bucket";

    private void comparePic(Context context) {
   /*
    AWSCredentials credentials;
    try {
        credentials = new ProfileCredentialsProvider("AdminUser").getCredentials();
    } catch(Exception e) {
        throw new AmazonClientException("Cannot load the credentials from the"
                + "credential profiles file. "
                + "Please make sure that your credentials file is at the correct "
                + "location (/Users/userid/.aws/credentials), and is in a valid format.", e);
    }
    AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder
            .standard()
            .withRegion(Regions.US_WEST_2)
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .build();
   */
    /*
        rekognitionClient = Util.getRekognitionClient(context);

        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(photo).withBucket(bucket)))
                .withMaxLabels(10)
                .withMinConfidence(75F);
        try {
            DetectLabelsResult result = rekognitionClient.detectLabels(request);
            List<Label> labels = result.getLabels();
            System.out.println("Detected labels for " + photo);
            for (Label label : labels) {
                System.out.println(label.getName() + ": " +
                        label.getConfidence().toString());
            }
        } catch (AmazonRekognitionException e) {
            e.printStackTrace();
        }*/
    }

}