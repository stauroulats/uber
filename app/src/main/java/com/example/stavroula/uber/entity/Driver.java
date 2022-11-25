package com.example.stavroula.uber.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Driver {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("trips")
    private Trip trip;
    @SerializedName("isActive")
    @Expose
    private Boolean isActive;

    public Driver(Integer id, User user) {
        this.id = id;
        this.user = user;
    }

    public Driver (){}

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
