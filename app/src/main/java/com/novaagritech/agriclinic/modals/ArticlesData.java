package com.novaagritech.agriclinic.modals;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ArticlesData implements Serializable {


    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("response")
    @Expose
    private List<ArticlesDetails> response = new ArrayList<ArticlesDetails>();



    @SerializedName("message")
    @Expose
    private String message;



    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<ArticlesDetails> getResponse() {
        return response;
    }

    public void setResponse(List<ArticlesDetails> response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public class ArticlesDetails {


        @SerializedName("latest")
        @Expose
        private List<InfoData> latest = new ArrayList<InfoData>();

        @SerializedName("recommended")
        @Expose
        private List<InfoData> recommended = new ArrayList<InfoData>();

        @SerializedName("trending")
        @Expose
        private List<InfoData> trending = new ArrayList<InfoData>();


        public List<InfoData> getLatest() {
            return latest;
        }

        public void setLatest(List<InfoData> latest) {
            this.latest = latest;
        }

        public List<InfoData> getRecommended() {
            return recommended;
        }

        public void setRecommended(List<InfoData> recommended) {
            this.recommended = recommended;
        }

        public List<InfoData> getTrending() {
            return trending;
        }

        public void setTrending(List<InfoData> trending) {
            this.trending = trending;
        }



    }
}
