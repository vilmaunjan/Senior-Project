package com.example.vilma.biometricrecognition;

/**
 * Created by Vilma on 1/20/2018.
 */

/*
 * Copyright 2015-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

public class Constants {

    /*
     * You should replace these values with your own. See the README for details
     * on what to fill in.
     */
    public static final String COGNITO_POOL_ID = "us-east-1:f91fe70a-8228-4626-bde6-16d470fb6358";
    //public static final String COGNITO_POOL_ID = "us-east-1:5721923b-5e19-4ba7-a4df-9f83b5f1582d";

    /*
     * Region of your Cognito identity pool ID.
     */
    public static final String COGNITO_POOL_REGION = "us-east-1";

    /*
     * Note, you must first create a bucket using the S3 console before running
     * the sample (https://console.aws.amazon.com/s3/). After creating a bucket,
     * put it's name in the field below.
     */
    public static final String BUCKET_NAME = "jeffstestingbucket";

    /*
     * Region of your bucket.
     */
    public static final String BUCKET_REGION = "us-east-1";

    /*
     * S3 user access id
     */
    public static final String ACCESS_KEY_ID = "AKIAIHONOSAKPXFMHDOA";

    /*
     * s3 user SECRET access key
     */
    public static final String SECRET_ACCESS_KEY_= "JSCkcnFJTGZelrbsUXX+GYphbzNH0Nc228O7LcET";

}
