package com.example.stavroula.uber.entity;

import android.graphics.Bitmap;

public class Photo {
    private String name;
    private long size;

    public Photo(String name, long size) {
        this.name = name;
        this.size = size;
    }

    public Photo(Bitmap bitmap) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
