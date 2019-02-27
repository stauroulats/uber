package com.example.stavroula.uber.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

    public class Car {

        @SerializedName("manufacturer")
        @Expose
        private String manufacturer;
        @SerializedName("model")
        @Expose
        private String model;
        @SerializedName("year")
        @Expose
        private Integer year;
        @SerializedName("registrationPlate")
        @Expose
        private String registrationPlate;
        @SerializedName("color")
        @Expose
        private String color;

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public String getRegistrationPlate() {
            return registrationPlate;
        }

        public void setRegistrationPlate(String registrationPlate) {
            this.registrationPlate = registrationPlate;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

    }

