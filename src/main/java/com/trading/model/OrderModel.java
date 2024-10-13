package com.trading.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class OrderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userid")
    private String userId;

    @Column(name = "stock_symbol")
    private String stockSymbol;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "stock_price")
    private float stockPrice;

    @Column(name = "quantity")
    private int quantity; // Represents the quantity for each transorderType

    @Column(name = "order_date")
    private Date orderDate;

    @Column(name = "order_price")
    private float orderPrice;

    @Column(name = "order_type")
    private String orderType;

    public OrderModel() {
    }

    public OrderModel(String userId, String stockSymbol, int quantity, String companyName, float stockPrice,
            Date orderDate,
            float orderPrice, String orderType) {
        this.userId = userId;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.companyName = companyName;
        this.stockPrice = stockPrice;
        this.orderDate = orderDate;
        this.orderPrice = orderPrice;
        this.orderType = orderType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderorderType) {
        this.orderType = orderorderType;
    }

    public void printOrder() {
        System.out.println("Order Details:");
        System.out.println("Stock Symbol: " + stockSymbol);
        System.out.println("Company Name: " + companyName);
        System.out.println("Stock Price: $" + stockPrice);
        System.out.println("Quantity: " + quantity);
        System.out.println("Order Price: $" + orderPrice);
        System.out.println("Order Date: " + orderDate);
        System.out.println("Order Type: " + orderType);
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
                ", Order Type=" + orderType +
                '}';
    }
}
