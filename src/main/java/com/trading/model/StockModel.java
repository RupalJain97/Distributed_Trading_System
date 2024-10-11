package com.trading.model;

import jakarta.persistence.*;

@Entity
public class StockModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockid; // Auto-generated id

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "companyname", nullable = false)
    private String companyName;

    @Column(name = "quantity", nullable = false, unique = true)
    private int quantity;

    @Column(name = "price", nullable = false)
    private double price;

    // No-argument constructor (required by JPA)
    public StockModel() {
    }

    // Constructor, Getters, and Setters
    public StockModel(String symbol, String companyName, int quantity, double price) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.quantity = quantity;
        this.price = price;
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

    public void setCompanyNameBySymbol(String stockSymbol){
        // return companyName;
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
}
