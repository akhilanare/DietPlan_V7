package com.example.dietplan.response;

import com.example.dietplan.item.DietPlanList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DietPlanRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("ads_param")
    private String ads_param;

    @SerializedName("DIET_APP")
    private List<DietPlanList> dietPlanLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getAds_param() {
        return ads_param;
    }

    public List<DietPlanList> getDietPlanLists() {
        return dietPlanLists;
    }
}
