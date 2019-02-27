package com.example.stavroula.uber.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TripRequest {

    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("pickUpPoint")
    @Expose
    private String pickUpPoint;
    @SerializedName("destination")
    @Expose
    private String destination;
    @SerializedName("rideDistance")
    @Expose
    private Double rideDistance;
    @SerializedName("stops")
    @Expose
    private List<Object> stops = null;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("trip")
    @Expose
    private Trip trip;
    @SerializedName("rider")
    @Expose
    private Rider rider;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPickUpPoint() {
        return pickUpPoint;
    }

    public void setPickUpPoint(String pickUpPoint) {
        this.pickUpPoint = pickUpPoint;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Double getRideDistance() {
        return rideDistance;
    }

    public void setRideDistance(Double rideDistance) {
        this.rideDistance = rideDistance;
    }

    public List<Object> getStops() {
        return stops;
    }

    public void setStops(List<Object> stops) {
        this.stops = stops;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Rider getRider() {
        return rider;
    }
    public void setRider(Rider rider) {
        this.rider = rider;
    }

}