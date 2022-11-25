package com.example.stavroula.uber.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trip {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("endTime")
    @Expose
    private Object endTime;
    @SerializedName("rideDistance")
    @Expose
    private Integer rideDistance;
    @SerializedName("fare")
    @Expose
    private Integer fare;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("paymentMethod")
    @Expose
    private Object paymentMethod;
    @SerializedName("tripRequest")
    @Expose
    private TripRequest tripRequest;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Object getEndTime() {
        return endTime;
    }

    public void setEndTime(Object endTime) {
        this.endTime = endTime;
    }

    public Integer getRideDistance() {
        return rideDistance;
    }

    public void setRideDistance(Integer rideDistance) {
        this.rideDistance = rideDistance;
    }

    public Integer getFare() {
        return fare;
    }

    public void setFare(Integer fare) {
        this.fare = fare;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Object paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public TripRequest getTripRequest() {
        return tripRequest;
    }

    public void setTripRequest(TripRequest tripRequest) {
        this.tripRequest = tripRequest;
    }

}