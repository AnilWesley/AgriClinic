package com.novaagritech.agriclinic.modals;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ArticlesList implements Serializable {

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("response")
    @Expose
    private List<InfoData> response = new ArrayList<InfoData>();


    @SerializedName("blist")
    @Expose
    private List<BannerData.BannerDetails> blist = new ArrayList<BannerData.BannerDetails>();

    @SerializedName("message")
    @Expose
    private String message;



    @SerializedName("article_pages")
    @Expose
    private Integer article_pages;

    @SerializedName("current_page")
    @Expose
    private Integer current_page;

    @SerializedName("likes_count")
    @Expose
    private Integer likes_count;


    public List<BannerData.BannerDetails> getBlist() {
        return blist;
    }

    public void setBlist(List<BannerData.BannerDetails> blist) {
        this.blist = blist;
    }

    public Integer getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(Integer likes_count) {
        this.likes_count = likes_count;
    }

    public Integer getArticle_pages() {
        return article_pages;
    }

    public void setArticle_pages(Integer article_pages) {
        this.article_pages = article_pages;
    }

    public Integer getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(Integer current_page) {
        this.current_page = current_page;
    }
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<InfoData> getResponse() {
        return response;
    }

    public void setResponse(List<InfoData> response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}

