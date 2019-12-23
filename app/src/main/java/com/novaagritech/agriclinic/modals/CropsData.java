package com.novaagritech.agriclinic.modals;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CropsData implements Serializable {

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("response")
    @Expose
    private List<CropDetails> response = new ArrayList<CropDetails>();

    @SerializedName("message")
    @Expose
    private String message;


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<CropDetails> getResponse() {
        return response;
    }

    public void setResponse(List<CropDetails> response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class CropDetails{

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("language_id")
        @Expose
        private String language_id;

        @SerializedName("main_category_id")
        @Expose
        private String main_category_id;

        @SerializedName("sub_category_id")
        @Expose
        private String sub_category_id;

        @SerializedName("title")
        @Expose
        private String title;

        @SerializedName("image")
        @Expose
        private String image;

        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("image_url")
        @Expose
        private String image_url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLanguage_id() {
            return language_id;
        }

        public void setLanguage_id(String language_id) {
            this.language_id = language_id;
        }

        public String getMain_category_id() {
            return main_category_id;
        }

        public void setMain_category_id(String main_category_id) {
            this.main_category_id = main_category_id;
        }

        public String getSub_category_id() {
            return sub_category_id;
        }

        public void setSub_category_id(String sub_category_id) {
            this.sub_category_id = sub_category_id;
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

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }
    }
}
