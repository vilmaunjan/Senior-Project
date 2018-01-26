package com.example.vilma.biometricrecognition;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "biometricscanner-mobilehub-1770537756-Accounts")

/*This class is the getter and setter for the app*/



public class AccountsDO {
    private String _userId;
    private String _pic1;
    private String _pic2;
    private Set<String> _picLog;

    @DynamoDBHashKey(attributeName = "userId")
//    @DynamoDBIndexHashKey(attributeName = "userId", globalSecondaryIndexName = "DateSorted")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }

    @DynamoDBAttribute(attributeName = "Pic1")
    public String getPic1() {
        return _pic1;
    }

    public void setPic1(final String _pic1) {
        this._pic1 = _pic1;
    }

    @DynamoDBAttribute(attributeName = "Pic2")
    public String getPic2() {
        return _pic2;
    }

    public void setPic2(final String _pic2) {
        this._pic2 = _pic2;
    }

    @DynamoDBAttribute(attributeName = "Pic_Log")
    public Set<String> getPicLog() {
        return _picLog;
    }

    public void setPicLog(final Set<String> _picLog) {
        this._picLog = _picLog;
    }

}
