package com.example.dietplan.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VideoList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("video_type")
    private String video_type;

    @SerializedName("video_title")
    private String video_title;

    @SerializedName("video_url")
    private String video_url;

    @SerializedName("video_id")
    private String video_id;

    @SerializedName("video_thumbnail_b")
    private String video_thumbnail_b;

    @SerializedName("video_thumbnail_s")
    private String video_thumbnail_s;

    @SerializedName("is_fav")
    private String is_fav;

    public String getId() {
        return id;
    }

    public String getVideo_type() {
        return video_type;
    }

    public String getVideo_title() {
        return video_title;
    }

    public String getVideo_url() {
        return video_url;
    }

    public String getVideo_id() {
        return video_id;
    }

    public String getVideo_thumbnail_b() {
        return video_thumbnail_b;
    }

    public String getVideo_thumbnail_s() {
        return video_thumbnail_s;
    }

    public String getIs_fav() {
        return is_fav;
    }

    public void setIs_fav(String is_fav) {
        this.is_fav = is_fav;
    }
}
