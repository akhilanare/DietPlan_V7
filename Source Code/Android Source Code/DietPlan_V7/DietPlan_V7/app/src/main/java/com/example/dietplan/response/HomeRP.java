package com.example.dietplan.response;

import com.example.dietplan.item.CategoryList;
import com.example.dietplan.item.DietPlanList;
import com.example.dietplan.item.SliderList;
import com.example.dietplan.item.VideoList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class HomeRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("slider_list")
    private List<SliderList> sliderLists;

    @SerializedName("recent_diet")
    private List<DietPlanList> recentDietPlanLists;

    @SerializedName("recent_video")
    private List<VideoList>  recentVideoLists;

    @SerializedName("cat_list")
    private List<CategoryList> categoryLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<SliderList> getSliderLists() {
        return sliderLists;
    }

    public List<CategoryList> getCategoryLists() {
        return categoryLists;
    }

    public List<DietPlanList> getRecentDietPlanLists() {
        return recentDietPlanLists;
    }

    public List<VideoList> getRecentVideoLists() {
        return recentVideoLists;
    }
}
