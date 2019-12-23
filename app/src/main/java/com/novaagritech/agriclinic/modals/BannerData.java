package com.novaagritech.agriclinic.modals;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BannerData {

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("response")
    @Expose
    private List<BannerDetails> response = new ArrayList<BannerDetails>();



    @SerializedName("message")
    @Expose
    private String message;


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<BannerDetails> getResponse() {
        return response;
    }

    public void setResponse(List<BannerDetails> response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class BannerDetails{

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("user_type")
        @Expose
        private String user_type;

        @SerializedName("role_user_id")
        @Expose
        private String role_user_id;

        @SerializedName("language_type")
        @Expose
        private String language_type;

        @SerializedName("title")
        @Expose
        private String title;

        @SerializedName("image")
        @Expose
        private String image;

        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("created_on")
        @Expose
        private String created_on;

        @SerializedName("image_url")
        @Expose
        private String image_url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_type() {
            return user_type;
        }

        public void setUser_type(String user_type) {
            this.user_type = user_type;
        }

        public String getRole_user_id() {
            return role_user_id;
        }

        public void setRole_user_id(String role_user_id) {
            this.role_user_id = role_user_id;
        }

        public String getLanguage_type() {
            return language_type;
        }

        public void setLanguage_type(String language_type) {
            this.language_type = language_type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreated_on() {
            return created_on;
        }

        public void setCreated_on(String created_on) {
            this.created_on = created_on;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }
    }
}
