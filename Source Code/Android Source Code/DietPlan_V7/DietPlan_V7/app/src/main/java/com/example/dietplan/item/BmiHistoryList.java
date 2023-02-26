package com.example.dietplan.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BmiHistoryList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("gender")
    private String gender;

    @SerializedName("bmi_score")
    private String bmi_score;

    @SerializedName("bmi_status")
    private String bmi_status;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGender() {
        return gender;
    }

    public String getBmi_score() {
        return bmi_score;
    }

    public String getBmi_status() {
        return bmi_status;
    }
}
