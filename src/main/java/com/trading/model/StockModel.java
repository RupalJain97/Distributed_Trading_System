package com.trading.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stock_model") // Specify table name
public class StockModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockid; // Auto-generated id

    @Column(name = "symbol", nullable = false, unique = true)
    private String symbol;

    @Column(name = "companyname", nullable = false)
    private String companyName;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "high_price", nullable = false)
    private double highPrice; // All-time high price for the stock

    @Column(name = "low_price", nullable = false)
    private double lowPrice; // All-time low price for the stock

    // No-argument constructor (required by JPA)
    public StockModel() {
    }

    // Constructor, Getters, and Setters
    public StockModel(String symbol, String companyName, int quantity, double price) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.quantity = quantity;
        this.price = price;
        this.highPrice = Math.max(this.highPrice, price);
        this.lowPrice = Math.min(this.lowPrice, price);
    }

    public Long getId() {
        return stockid;
    }

    public void setId(Long id) {
        this.stockid = id;
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

    public void setQuantity(int stock_left) {
        this.quantity = stock_left;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(double price) {
        this.highPrice = Math.max(this.highPrice, price);
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(double price) {
        this.lowPrice = Math.min(this.lowPrice, price);
    }
}
