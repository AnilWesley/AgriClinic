package com.novaagritech.agriclinic.modals;


import java.io.Serializable;

public class Video implements Serializable {
    private String videoId, title,url,videoTime;

    public Video(String videoId, String title, String url, String videoTime) {
        this.videoId = videoId;
        this.title = title;
        this.url = url;
        this.videoTime = videoTime;
    }

    public Video(String videoId) {
        this.videoId = videoId;
    }

    public Video() {
    }

    public String getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(String videoTime) {
        this.videoTime = videoTime;
    }

    public String getUrl() {
        return url;
    }



    public void setUrl(String url) {
        this.url = url;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    @Override
    public String toString() {
        return "Video{" +
                "videoId='" + videoId + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
