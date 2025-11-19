package com.motivation.quotes.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GreetingData {

    @SerializedName("id")
    public final String id;

    @SerializedName("name")
    public final String name;

    @SerializedName("post")
    public List<PostItem> post;

    public GreetingData(String id, String name) {
        this.id = id;
        this.name = name;
    }
}