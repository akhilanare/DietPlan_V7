package com.example.dietplan.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CategoryList implements Serializable {

    @SerializedName("cid")
    private String cid;

    @SerializedName("category_name")
    private String category_name;

    @SerializedName("category_image_thumb")
    private String category_image_thumb;

    public String getCid() {
        return cid;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getCategory_image_thumb() {
        return category_image_thumb;
    }
}
