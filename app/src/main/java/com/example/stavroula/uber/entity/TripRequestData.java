package com.example.stavroula.uber.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TripRequestData {

        @SerializedName("pickUpPoint")
        @Expose
        private String pickUpPoint;
        @SerializedName("destination")
        @Expose
        private String destination;
        @SerializedName("rideDistance")
        @Expose
        private double rideDistance;
        @SerializedName("stops")
        @Expose
        private List<Stop> stops = null;

    public TripRequestData(String pickUpPoint, String destination, double rideDistance) {
        this.pickUpPoint = pickUpPoint;
        this.destination = destination;
        this.rideDistance = rideDistance;
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

        public double getRideDistance() {
            return rideDistance;
        }

        public void setRideDistance(double rideDistance) {
            this.rideDistance = rideDistance;
        }

        public List<Stop> getStops() {
            return stops;
        }

        public void setStops(List<Stop> stops) {
            this.stops = stops;
        }

    }
