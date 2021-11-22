package com.nhom7.den_cafe.model;

import java.io.Serializable;

public class Wish implements Serializable {
    private String idWish;
    private String productName;
    private int productPrice;
    private String productImg;

    public Wish(String idWish, String productName, int productPrice, String productImg) {
        this.idWish = idWish;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImg = productImg;
    }

    public Wish() {

    }

    public String getIdWish() {
        return idWish;
    }

    public void setIdWish(String idWish) {
        this.idWish = idWish;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }
}
