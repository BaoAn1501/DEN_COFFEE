package com.nhom7.den_cafe.model;

import java.io.Serializable;
import java.util.List;

public class ProductRating implements Serializable {
    private String idproduct;
    private float count;
    private float total;
    private float average;
    List<RateUnit> rateUnits;

    public ProductRating(String idproduct, float count, float total, float average, List<RateUnit> rateUnits) {
        this.idproduct = idproduct;
        this.count = count;
        this.total = total;
        this.average = average;
        this.rateUnits = rateUnits;
    }

    public ProductRating(String idproduct, float count, float total, float average) {
        this.idproduct = idproduct;
        this.count = count;
        this.total = total;
        this.average = average;
    }

    public ProductRating() {
    }

    public String getIdproduct() {
        return idproduct;
    }

    public void setIdproduct(String idproduct) {
        this.idproduct = idproduct;
    }

    public float getCount() {
        return count;
    }

    public void setCount(float count) {
        this.count = count;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public List<RateUnit> getRateUnits() {
        return rateUnits;
    }

    public void setRateUnits(List<RateUnit> rateUnits) {
        this.rateUnits = rateUnits;
    }
}
