package com.motivation.quotes.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserDetail implements Serializable {


    @SerializedName("user_id")
    public String user_id;

    @SerializedName("login_type")
    public String login_type;

    @SerializedName("name")
    public String name;

    @SerializedName("email")
    public String email;

    @SerializedName("mobile")
    public String mobile;

    @SerializedName("logo")
    public String logo;

    @SerializedName("designation")
    public String designation;

    @SerializedName("socialmedia1")
    public String socialmedia1;

    @SerializedName("socialmedia2")
    public String socialmedia2;

    @SerializedName("is_subscribed")
    public boolean is_subscribed = false;

    @SerializedName("subscription_id")
    public String subscription_id;

    @SerializedName("subscription_start_date")
    public String subscription_start_date;

    @SerializedName("subscription_end_date")
    public String subscription_end_date;

    @SerializedName("business_id")
    public String business_id;

    @SerializedName("business_name")
    public String business_name;

    @SerializedName("business_email")
    public String business_email;

    @SerializedName("business_mobile")
    public String business_mobile;

    @SerializedName("business_logo")
    public String business_logo;

    @SerializedName("business_about")
    public String business_about;

    @SerializedName("business_address")
    public String business_address;

    @SerializedName("business_socialmedia_type")
    public String business_socialmedia_type;

    @SerializedName("business_socialmedia_value")
    public String business_socialmedia_value;


}
