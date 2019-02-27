package com.example.stavroula.uber.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TripStatistics {

    @SerializedName("totalTrips")
    @Expose
    private Integer totalTrips;
    @SerializedName("earnings")
    @Expose
    private Integer earnings;
    @SerializedName("totalKms")
    @Expose
    private Integer totalKms;

    public Integer getTotalTrips() {
        return totalTrips;
    }

    public void setTotalTrips(Integer totalTrips) {
        this.totalTrips = totalTrips;
    }

    public Integer getEarnings() {
        return earnings;
    }

    public void setEarnings(Integer earnings) {
        this.earnings = earnings;
    }

    public Integer getTotalKms() {
        return totalKms;
    }

    public void setTotalKms(Integer totalKms) {
        this.totalKms = totalKms;
    }

}
