package com.nhom7.den_cafe.model;

import java.io.Serializable;
import java.util.List;

public class OrderDetail implements Serializable {
    private String id;
    private String useruid;
    private String phone;
    private String name;
    private String date;
    private String address;
    private int total;
    private boolean state;
    private List<Cart> carts;

    public OrderDetail(String id, String useruid, String phone, String name, String date, String address, int total, boolean state) {
        this.id = id;
        this.useruid = useruid;
        this.phone = phone;
        this.name = name;
        this.date = date;
        this.address = address;
        this.total = total;
        this.state = state;
    }

    public OrderDetail() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getUseruid() {
        return useruid;
    }

    public void setUseruid(String useruid) {
        this.useruid = useruid;
    }

    public List<Cart> getCarts() {
        return carts;
    }

    public void setCarts(List<Cart> carts) {
        this.carts = carts;
    }
}
