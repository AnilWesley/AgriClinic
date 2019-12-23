package com.novaagritech.agriclinic.modals;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserData implements Serializable {

    @SerializedName("status")
    @Expose
    private boolean status;
    @SerializedName("response")
    @Expose
    private List<UserDeatils> response = new ArrayList<UserDeatils>();

    @SerializedName("message")
    @Expose
    private String message;


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<UserDeatils> getResponse() {
        return response;
    }

    public void setResponse(List<UserDeatils> response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class UserDeatils{

        @SerializedName("otp")
        @Expose
        private String otp;

        @SerializedName("user_id")
        @Expose
        private String user_id;

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("mobile")
        @Expose
        private String mobile;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("latitude")
        @Expose
        private String latitude;

        @SerializedName("longitude")
        @Expose
        private String longitude;

        @SerializedName("pincode")
        @Expose
        private String pincode;

        @SerializedName("check_tc")
        @Expose
        private String check_tc;

        @SerializedName("check_sms_calls")
        @Expose
        private String check_sms_calls;

        @SerializedName("status")
        @Expose
        private String status;




        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public String getCheck_tc() {
            return check_tc;
        }

        public void setCheck_tc(String check_tc) {
            this.check_tc = check_tc;
        }

        public String getCheck_sms_calls() {
            return check_sms_calls;
        }

        public void setCheck_sms_calls(String check_sms_calls) {
            this.check_sms_calls = check_sms_calls;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
