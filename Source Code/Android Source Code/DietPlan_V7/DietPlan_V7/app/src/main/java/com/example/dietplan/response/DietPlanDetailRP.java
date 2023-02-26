package com.example.dietplan.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DietPlanDetailRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("id")
    private String id;

    @SerializedName("diet_title")
    private String diet_title;

    @SerializedName("diet_image")
    private String diet_image;

    @SerializedName("diet_info")
    private String diet_info;

    @SerializedName("is_fav")
    private String is_fav;

    @SerializedName("share_link")
    private String share_link;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public String getId() {
        return id;
    }

    public String getDiet_title() {
        return diet_title;
    }

    public String getDiet_image() {
        return diet_image;
    }

    public String getDiet_info() {
        return diet_info;
    }

    public String getIs_fav() {
        return is_fav;
    }

    public String getShare_link() {
        return share_link;
    }
}
