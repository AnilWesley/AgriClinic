package com.novaagritech.agriclinic.modals;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SchemesData implements Serializable {
    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("response")
    @Expose
    private List<InfoData> response = new ArrayList<InfoData>();

    @SerializedName("message")
    @Expose
    private String message;


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public List<InfoData> getResponse() {
        return response;
    }

    public void setResponse(List<InfoData> response) {
        this.response = response;
    }
}
