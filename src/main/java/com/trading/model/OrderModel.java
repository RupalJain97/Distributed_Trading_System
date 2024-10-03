package com.trading.model;

import java.util.Date;

public class OrderModel {
    private String stockSymbol;
    private int quantity;
    private String companyName;
    private Date orderDate;
    private float price;

    public OrderModel() {}

    public OrderModel(String stockSymbol, int quantity, String companyName, Date orderDate, float price) {
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.companyName = companyName;
        this.orderDate = orderDate;
        this.price = price;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getOrderDate(){
        return orderDate;
    }

    public String getCompanyName(){
        return companyName;
    }

    public float getPrice(){
        return price;
    }
}
