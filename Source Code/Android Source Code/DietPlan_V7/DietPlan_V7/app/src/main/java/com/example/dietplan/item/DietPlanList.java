package com.example.dietplan.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DietPlanList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("diet_title")
    private String diet_title;

    @SerializedName("diet_image")
    private String diet_image;

    @SerializedName("diet_image_thumbnail")
    private String diet_image_thumbnail;

    public String getId() {
        return id;
    }

    public String getDiet_title() {
        return diet_title;
    }

    public String getDiet_image() {
        return diet_image;
    }

    public String getDiet_image_thumbnail() {
        return diet_image_thumbnail;
    }
}