package com.feibo.testmediaplayer.bean;

import android.graphics.Bitmap;

public class MediaInfo {

    private String videoUrl;
    private Bitmap bitmap;
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
