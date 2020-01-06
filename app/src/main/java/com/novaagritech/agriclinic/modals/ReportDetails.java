package com.novaagritech.agriclinic.modals;

public class ReportDetails {

    private String id;
    private String user_name;
    private String mobile;
    private String image;
    private String remarks;
    private String reply;
    private String created_on;
    private String modified_on;


    public ReportDetails() {
    }


    public ReportDetails(String id, String user_name, String mobile, String image, String remarks, String reply, String created_on, String modified_on) {
        this.id = id;
        this.user_name = user_name;
        this.mobile = mobile;
        this.image = image;
        this.remarks = remarks;
        this.reply = reply;
        this.created_on = created_on;
        this.modified_on = modified_on;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getModified_on() {
        return modified_on;
    }

    public void setModified_on(String modified_on) {
        this.modified_on = modified_on;
    }
}
