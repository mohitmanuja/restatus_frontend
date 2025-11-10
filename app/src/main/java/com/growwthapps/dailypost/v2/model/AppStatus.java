package com.growwthapps.dailypost.v2.model;

import com.google.gson.annotations.SerializedName;

public class AppStatus {


    @SerializedName("code")
    public String code;


    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public SubscriptionModel subscriptionModel;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SubscriptionModel getSubscriptionModel() {
        return subscriptionModel;
    }

    public void setSubscriptionModel(SubscriptionModel subscriptionModel) {
        this.subscriptionModel = subscriptionModel;
    }
}
