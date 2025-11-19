package com.motivation.quotes.app.model;

import com.google.gson.annotations.SerializedName;

public class AppInfos {

    @SerializedName("AppInfo")
    public AppInfo appInfo;


    @SerializedName("AdsModel")
    public AdsModel adsModel;


    public AppInfo getAppInfo() {
        return appInfo;
    }


    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public AdsModel getAdsModel() {
        return adsModel;
    }

    public void setAdsModel(AdsModel adsModel) {
        this.adsModel = adsModel;
    }
}
