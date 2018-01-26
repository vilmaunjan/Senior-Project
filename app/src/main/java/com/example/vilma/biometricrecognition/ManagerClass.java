package com.example.vilma.biometricrecognition;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;


/**
 * Created by root on 1/24/18.
 */

public class ManagerClass {

    // to get credentials from the Cognito pool
    CognitoCachingCredentialsProvider credentialsProvider = null;
    CognitoSyncManager syncManager=null;

    public static AmazonDynamoDBClient dynamoDBClient;
    public static DynamoDBMapper dynamoDBMapper=null;


    public CognitoCachingCredentialsProvider getCredentials(Context context){
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:69c73672-0639-4cfa-9196-52788ff859aa", // Identity pool ID
                Regions.US_EAST_1 // Region

        );

//        syncManager = new CognitoSyncManager(context,Regions.US_EAST_1,credentialsProvider);
//        Dataset dataset = syncManager.openOrCreateDataset("Dynamoset");
//        dataset.put("DynamoKey","DynamoValue");

//        dataset.synchronize(new DefaultSyncCallback());

        return credentialsProvider;
    }

    public DynamoDBMapper initDynamoClient(CognitoCachingCredentialsProvider credentialsProvider){
        if(dynamoDBClient==null){

            dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);
            dynamoDBClient.setRegion(Region.getRegion(Regions.US_EAST_1));
            dynamoDBMapper = new DynamoDBMapper(dynamoDBClient);
        }
        return dynamoDBMapper;
    }

}
