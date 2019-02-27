package com.example.stavroula.uber.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Rider {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("trips")
    @Expose
    private List<Trip> trips = null;
    @SerializedName("tripRequests")
    @Expose
    private List<TripRequest> tripRequests = null;
    @SerializedName("appointments")
    @Expose
    private List<Object> appointments = null;
    @SerializedName("creditCards")
    @Expose
    private List<CreditCard> creditCards = null;
    @Expose
    private Integer totalReviews;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public List<TripRequest> getTripRequests() {
        return tripRequests;
    }

    public void setTripRequests(List<TripRequest> tripRequests) {
        this.tripRequests = tripRequests;
    }

    public List<Object> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Object> appointments) {
        this.appointments = appointments;
    }

    public List<CreditCard> getCreditCards() {
        return creditCards;
    }

    public void setCreditCards(List<CreditCard> creditCards) {
        this.creditCards = creditCards;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }


}