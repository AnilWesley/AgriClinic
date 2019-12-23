package com.novaagritech.agriclinic.modals;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.novaagritech.agriclinic.modals.weathermodel.Weather;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Stories implements Serializable {



    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("count")
    @Expose
    private int count;

    @SerializedName("response")
    @Expose
    private List<StoriesDetails> response = new ArrayList<StoriesDetails>();


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<StoriesDetails> getResponse() {
        return response;
    }

    public void setResponse(List<StoriesDetails> response) {
        this.response = response;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public class StoriesDetails{

        @SerializedName("story_id")
        @Expose
        private String story_id;
        @SerializedName("user_id")
        @Expose
        private String user_id;
        @SerializedName("image_url")
        @Expose
        private String image_url;
        @SerializedName("vedio_url")
        @Expose
        private String vedio_url;

        @SerializedName("is_vedio")
        @Expose
        private int is_vedio;

        public StoriesDetails(String story_id, String user_id, String image_url, String vedio_url, int is_vedio) {
            this.story_id = story_id;
            this.user_id = user_id;
            this.image_url = image_url;
            this.vedio_url = vedio_url;
            this.is_vedio = is_vedio;
        }

        public int getIs_vedio() {
            return is_vedio;
        }

        public void setIs_vedio(int is_vedio) {
            this.is_vedio = is_vedio;
        }

        public String getStory_id() {
            return story_id;
        }

        public void setStory_id(String story_id) {
            this.story_id = story_id;
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
    }

}
