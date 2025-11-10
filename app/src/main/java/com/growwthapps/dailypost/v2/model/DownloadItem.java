package com.growwthapps.dailypost.v2.model;

import android.net.Uri;

public class DownloadItem {

    public String uri;

    public boolean isVideo;

    public DownloadItem(String uri, boolean isVideo) {
        this.uri = uri;
        this.isVideo = isVideo;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }
}
