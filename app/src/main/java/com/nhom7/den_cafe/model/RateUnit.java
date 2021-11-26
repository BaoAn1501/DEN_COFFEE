package com.nhom7.den_cafe.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RateUnit implements Serializable {
    private String uidRating;
    private float valueRating;
    private String dateRating;
    private String comment;
    private String userName;

    public RateUnit(String uidRating, float valueRating, String dateRating, String comment, String userName) {
        this.uidRating = uidRating;
        this.valueRating = valueRating;
        this.dateRating = dateRating;
        this.comment = comment;
        this.userName = userName;
    }

    public RateUnit() {
    }

    public String getUidRating() {
        return uidRating;
    }

    public void setUidRating(String uidRating) {
        this.uidRating = uidRating;
    }

    public float getValueRating() {
        return valueRating;
    }

    public void setValueRating(float valueRating) {
        this.valueRating = valueRating;
    }

    public String getDateRating() {
        return dateRating;
    }

    public void setDateRating(String dateRating) {
        this.dateRating = dateRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
