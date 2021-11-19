package com.nhom7.den_cafe.model;

import java.io.Serializable;

public class Product implements Serializable {
    private String productId;
    private String productName;
    private int productPrice;
    private String productImage;
    private String productType;
    private boolean isWish;
    private float rating;

    public Product(String productId, String productName, int productPrice, String productImage, String productType, boolean isWish, float rating) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productImage = productImage;
        this.productType = productType;
        this.isWish = isWish;
        this.rating = rating;
    }

    public Product() {

    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public boolean isWish() {
        return isWish;
    }

    public void setWish(boolean wish) {
        isWish = wish;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    @Override
    public String toString() {
        return productName;
    }
}
