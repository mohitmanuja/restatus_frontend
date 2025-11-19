package com.motivation.quotes.app.model;

import com.google.gson.annotations.SerializedName;

public class LoginModel {
    @SerializedName("is_registered")
    public boolean is_registered;


    @SerializedName("data")
    public UserDetail data;
}
