package com.novaagritech.agriclinic.comments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by evolutyz on 21/02/18.
 */

public  class FriendlyMessage implements Serializable {

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("response")
    @Expose
    private List<CommentDetails> response = new ArrayList<CommentDetails>();



    @SerializedName("message")
    @Expose
    private String message;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<CommentDetails> getResponse() {
        return response;
    }

    public void setResponse(List<CommentDetails> response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class CommentDetails {

        @SerializedName("comment_id")
        @Expose
        private String comment_id;


        @SerializedName("user_id")
        @Expose
        private String user_id;


        @SerializedName("article_id")
        @Expose
        private String article_id;


        @SerializedName("comment")
        @Expose
        private String comment;

        @SerializedName("date")
        @Expose
        private String date;


        @SerializedName("time")
        @Expose
        private String time;


        @SerializedName("user_name")
        @Expose
        private String user_name;


        public CommentDetails(String comment_id, String user_id, String article_id, String comment, String date, String time, String user_name) {
            this.comment_id = comment_id;
            this.user_id = user_id;
            this.article_id = article_id;
            this.comment = comment;
            this.date = date;
            this.time = time;
            this.user_name = user_name;
        }


        public String getComment_id() {
            return comment_id;
        }

        public void setComment_id(String comment_id) {
            this.comment_id = comment_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getArticle_id() {
            return article_id;
        }

        public void setArticle_id(String article_id) {
            this.article_id = article_id;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }
    }
}