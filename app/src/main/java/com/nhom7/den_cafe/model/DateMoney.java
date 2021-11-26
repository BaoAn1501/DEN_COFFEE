package com.nhom7.den_cafe.model;

public class DateMoney {
    private String date;;
    private int sum;

    public DateMoney(String date, int sum) {
        this.date = date;
        this.sum = sum;
    }

    public DateMoney() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
