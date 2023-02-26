package com.example.dietplan.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SliderList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("slider_type")
    private String slider_type;

    @SerializedName("slider_title")
    private String slider_title;

    @SerializedName("thumbnail_b")
    private String thumbnail_b;

    @SerializedName("external_link")
    private String external_link;

    @SerializedName("video_id")
    private String video_id;

    @SerializedName("video_url")
    private String video_url;

    @SerializedName("video_type")
    private String video_type;

    public String getId() {
        return id;
    }

    public String getSlider_type() {
        return slider_type;
    }

    public String getSlider_title() {
        return slider_title;
    }

    public String getThumbnail_b() {
        return thumbnail_b;
    }

    public String getExternal_link() {
        return external_link;
    }

    public String getVideo_id() {
        return video_id;
    }

    public String getVideo_url() {
        return video_url;
    }

    public String getVideo_type() {
        return video_type;
    }
}
