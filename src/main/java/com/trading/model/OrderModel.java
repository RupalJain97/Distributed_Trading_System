package com.trading.model;

import java.util.Date;

public class OrderModel {
    private String stockSymbol;
    private int quantity;
    private String companyName;
    private float stockPrice;
    private Date orderDate;
    private float orderPrice;
    private String action;

    public OrderModel() {
    }

    public OrderModel(String stockSymbol, int quantity, String companyName, float stockPrice, Date orderDate,
            float orderPrice, String action) {
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.companyName = companyName;
        this.stockPrice = stockPrice;
        this.orderDate = orderDate;
        this.orderPrice = orderPrice;
        this.action = action;
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

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate() {
        this.orderDate = new Date();
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String StockCompanyName) {
        this.companyName = StockCompanyName;
    }

    public float getOrderPrice() {
        return orderPrice;
    }

    public float getStockPrice() {
        return stockPrice;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String orderAction) {
        this.action = orderAction;
    }

    public void printOrder() {
        System.out.println("Order Details:");
        System.out.println("Stock Symbol: " + stockSymbol);
        System.out.println("Company Name: " + companyName);
        System.out.println("Stock Price: $" + stockPrice);
        System.out.println("Quantity: " + quantity);
        System.out.println("Order Price: $" + orderPrice);
        System.out.println("Order Date: " + orderDate);
        System.out.println("Order Type: " + action);
    }

    // For debugging purpose, you can override toString() as well
    @Override
    public String toString() {
        return "OrderModel {" +
                "Stock Symbol='" + stockSymbol + '\'' +
                ", Company Name='" + companyName + '\'' +
                ", Price=$" + stockPrice +
                ", Quantity=" + quantity +
                ", Order Price=$" + orderPrice +
                ", Order Date=" + orderDate +
                ", Order Type=" + action +
                '}';
    }
}
