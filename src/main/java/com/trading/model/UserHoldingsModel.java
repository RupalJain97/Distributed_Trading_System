package com.trading.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class UserHoldingsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userid")
    private String userId;

    @Column(name = "stock_symbol")
    private String stockSymbol;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "current_quantity")
    private int currentQuantity; // Represents how many stocks user holds

    @Column(name = "last_price")
    private double lastPrice; // The last price at which the stock was bought/sold

    @Column(name = "last_order_date")
    private Date lastOrderDate; // Date of the last transaction

    public UserHoldingsModel() {
    }

    public UserHoldingsModel(Long id,
            String userId,
            String stockSymbol,
            String companyName,
            int currentQuantity,
            double lastPrice,
            Date lastOrderDate) {
        this.id = id;
        this.userId = userId;
        this.stockSymbol = stockSymbol;
        this.companyName = companyName;
        this.currentQuantity = currentQuantity;
        this.lastPrice = lastPrice;
        this.lastOrderDate = lastOrderDate;
    }

    // Getter and Setter for id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter and Setter for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter and Setter for stockSymbol
    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    // Getter and Setter for companyName
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    // Getter and Setter for currentQuantity
    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    // Getter and Setter for lastPrice
    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    // Getter and Setter for lastOrderDate
    public Date getLastOrderDate() {
        return lastOrderDate;
    }

    public void setLastOrderDate(Date lastOrderDate) {
        this.lastOrderDate = lastOrderDate;
    }
}
