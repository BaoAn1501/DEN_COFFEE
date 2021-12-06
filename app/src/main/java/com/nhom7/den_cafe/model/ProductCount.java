package com.nhom7.den_cafe.model;

public class ProductCount {
    private String id;
    private int count;

    public ProductCount(String id, int count) {
        this.id = id;
        this.count = count;
    }

    public ProductCount() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
