package com.nhom7.den_cafe.model;

import java.io.Serializable;

public class OrderState implements Serializable {
    private String idState;
    private String useruid;
    private String phone;
    private String date;
    private boolean state;

    public OrderState(String idState, String useruid, String phone, String date, boolean state) {
        this.idState = idState;
        this.useruid = useruid;
        this.phone = phone;
        this.date = date;
        this.state = state;
    }

    public OrderState() {
    }

    public String getIdState() {
        return idState;
    }

    public void setIdState(String idState) {
        this.idState = idState;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
}
