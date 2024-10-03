package com.trading.model;

public class StockModel {
    private String symbol;
    private String companyName;
    private int quantity;
    private double price;

    // Constructor, Getters, and Setters
    public StockModel(String symbol, String companyName, int quantity, double price) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setAvailableQuantity(int stock_left) {
        this.quantity = stock_left;
    }
}