package com.example.dietplan.response;

import com.example.dietplan.item.BmiHistoryList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BmiHistoryRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("DIET_APP")
    private List<BmiHistoryList> bmiHistoryLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<BmiHistoryList> getBmiHistoryLists() {
        return bmiHistoryLists;
    }
}
