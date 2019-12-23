package com.novaagritech.agriclinic.modals;

import java.io.Serializable;
import java.util.List;

public class Stories1 implements Serializable {


    private String story_id;
    private String user_id;
    private String image_url;
    private String vedio_url;
    private boolean viewed_status;
    private int is_vedio;

    public Stories1(String story_id, String user_id, String image_url, String vedio_url, boolean viewed_status, int is_vedio) {
        this.story_id = story_id;
        this.user_id = user_id;
        this.image_url = image_url;
        this.vedio_url = vedio_url;
        this.viewed_status = viewed_status;
        this.is_vedio = is_vedio;
    }

    public int getIs_vedio() {
        return is_vedio;
    }

    public void setIs_vedio(int is_vedio) {
        this.is_vedio = is_vedio;
    }

    public boolean isViewed_status() {
        return viewed_status;
    }

    public void setViewed_status(boolean viewed_status) {
        this.viewed_status = viewed_status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getVedio_url() {
        return vedio_url;
    }

    public void setVedio_url(String vedio_url) {
        this.vedio_url = vedio_url;
    }

    public String getStory_id() {
        return story_id;
    }

    public void setStory_id(String story_id) {
        this.story_id = story_id;
    }
}
