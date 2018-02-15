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

//This class is the DB GETTER AND SETTER

@DynamoDBTable(tableName = "biometricscanner-mobilehub-1770537756-Accounts")

public class AccountsDO {
    private String _userId;
    private String _firstName;
    private String _lastName;
    private String _pic1;
    private String _pic2;
    private Set<String> _picLog;

    @DynamoDBHashKey(attributeName = "userId")
   // @DynamoDBIndexHashKey(attributeName = "userId", globalSecondaryIndexName = "DateSorted")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBAttribute(attributeName = "First_Name")
    public String getFirstName() {
        return _firstName;
    }

    public void setFirstName(final String _firstName) {
        this._firstName = _firstName;
    }
    @DynamoDBAttribute(attributeName = "Last_Name")
    public String getLastName() {
        return _lastName;
    }

    public void setLastName(final String _lastName) {
        this._lastName = _lastName;
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
