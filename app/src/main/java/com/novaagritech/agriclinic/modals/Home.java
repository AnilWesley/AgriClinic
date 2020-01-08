package com.novaagritech.agriclinic.modals;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Home implements Serializable {
    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("response")
    @Expose
    private List<Info> response = new ArrayList<Info>();

    @SerializedName("result")
    @Expose
    private List<Info> result = new ArrayList<Info>();


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


    public List<Info> getResponse() {
        return response;
    }

    public void setResponse(List<Info> response) {
        this.response = response;
    }


    public List<Info> getResult() {
        return result;
    }

    public void setResult(List<Info> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Home{" +
                "status=" + status +
                ", response=" + response +
                ", result=" + result +
                ", message='" + message + '\'' +
                '}';
    }
}
