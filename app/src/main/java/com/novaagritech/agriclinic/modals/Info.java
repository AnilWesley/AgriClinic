package com.novaagritech.agriclinic.modals;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Info implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("category_id")
    @Expose
    private String category_id;

    @SerializedName("sub_category_id")
    @Expose
    private String sub_category_id;

    @SerializedName("crop_id")
    @Expose
    private String crop_id;

    @SerializedName("language_id")
    @Expose
    private String language_id;

    @SerializedName("image_path")
    @Expose
    private String image_path;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("description2")
    @Expose
    private String description2;

    @SerializedName("description3")
    @Expose
    private String description3;

    @SerializedName("tags")
    @Expose
    private String tags;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("created_on")
    @Expose
    private String created_on;

    @SerializedName("author_image")
    @Expose
    private String author_image;

    @SerializedName("author_info")
    @Expose
    private String author_info;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("image_url")
    @Expose
    private String image_url;

    @SerializedName("start_date")
    @Expose
    private String start_date;

    @SerializedName("end_date")
    @Expose
    private String end_date;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("share_url")
    @Expose
    private String share_url;


    @SerializedName("source_logo")
    @Expose
    private String source_logo;


    @SerializedName("source")
    @Expose
    private String source;

    @SerializedName("likes_count")
    @Expose
    private int likes_count;

    @SerializedName("is_liked")
    @Expose
    private int is_liked;

    @SerializedName("views_count")
    @Expose
    private int views_count;


    @SerializedName("comment_count")
    @Expose
    private int comment_count;


    @SerializedName("terms_conditions")
    @Expose
    private String terms_conditions;

    @SerializedName("privacy_policy")
    @Expose
    private String privacy_policy;

    @SerializedName("remarks")
    @Expose
    private String remarks;

    @SerializedName("reply")
    @Expose
    private String reply;


    @SerializedName("crop_name")
    @Expose
    private String crop_name;



    @SerializedName("user_type")
    @Expose
    private String user_type;

    @SerializedName("role_user_id")
    @Expose
    private String role_user_id;

    @SerializedName("language_type")
    @Expose
    private String language_type;


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

    public String getCrop_name() {
        return crop_name;
    }

    public void setCrop_name(String crop_name) {
        this.crop_name = crop_name;
    }

    public String getRemarks() {
        return remarks;
    }


    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }


    public String getTerms_conditions() {
        return terms_conditions;
    }

    public void setTerms_conditions(String terms_conditions) {
        this.terms_conditions = terms_conditions;
    }

    public String getPrivacy_policy() {
        return privacy_policy;
    }

    public void setPrivacy_policy(String privacy_policy) {
        this.privacy_policy = privacy_policy;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public String getDescription3() {
        return description3;
    }

    public void setDescription3(String description3) {
        this.description3 = description3;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public int getViews_count() {
        return views_count;
    }

    public void setViews_count(int views_count) {
        this.views_count = views_count;
    }

    public int getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(int is_liked) {
        this.is_liked = is_liked;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSub_category_id() {
        return sub_category_id;
    }

    public void setSub_category_id(String sub_category_id) {
        this.sub_category_id = sub_category_id;
    }

    public String getCrop_id() {
        return crop_id;
    }

    public void setCrop_id(String crop_id) {
        this.crop_id = crop_id;
    }

    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public String getAuthor_image() {
        return author_image;
    }

    public void setAuthor_image(String author_image) {
        this.author_image = author_image;
    }

    public String getAuthor_info() {
        return author_info;
    }

    public void setAuthor_info(String author_info) {
        this.author_info = author_info;
    }

    public String getSource_logo() {
        return source_logo;
    }

    public void setSource_logo(String source_logo) {
        this.source_logo = source_logo;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @NonNull
    @Override
    public String toString() {
        return "Info{" +
                "id='" + id + '\'' +
                ", category_id='" + category_id + '\'' +
                ", sub_category_id='" + sub_category_id + '\'' +
                ", crop_id='" + crop_id + '\'' +
                ", language_id='" + language_id + '\'' +
                ", image_path='" + image_path + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", description2='" + description2 + '\'' +
                ", description3='" + description3 + '\'' +
                ", tags='" + tags + '\'' +
                ", status='" + status + '\'' +
                ", created_on='" + created_on + '\'' +
                ", author_image='" + author_image + '\'' +
                ", author_info='" + author_info + '\'' +
                ", image='" + image + '\'' +
                ", image_url='" + image_url + '\'' +
                ", start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                ", location='" + location + '\'' +
                ", share_url='" + share_url + '\'' +
                ", source_logo='" + source_logo + '\'' +
                ", source='" + source + '\'' +
                ", likes_count=" + likes_count +
                ", is_liked=" + is_liked +
                ", views_count=" + views_count +
                ", comment_count=" + comment_count +
                '}';
    }
}
