package com.example.demo;

// Discount class
public class Discount {
    private String discountID;
    private String discountType;
    private double value;
    
    public Discount(String discountID, String discountType, double value) {
        this.discountID = discountID;
        this.discountType = discountType;
        this.value = value;
    }
    
    // Getters and Setters
    public String getDiscountID() {
        return discountID;
    }
    
    public void setDiscountID(String discountID) {
        this.discountID = discountID;
    }
    
    public String getDiscountType() {
        return discountType;
    }
    
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }
    
    public double getValue() {
        return value;
    }
    
    public void setValue(double value) {
        this.value = value;
    }
    
    public double applyDiscount(double price) {
        return price - (price * value);
    }
}