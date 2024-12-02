package com.example.demo;

import java.util.Date;
import java.util.*;
import java.text.SimpleDateFormat;

public class Transaction {
    private String transactionID;
    private Date date;
    private double total;
    private List<Product> items;
    private Discount discount;  // Aggregation
    private User performer;     // Association with User
    private Customer customer;  // Association with Customer
    
    public Transaction(String transactionID, User performer, Customer customer) {
        if (!performer.isAuthenticated()) {
            throw new SecurityException("User harus login terlebih dahulu untuk membuat transaksi");
        }
        this.transactionID = transactionID;
        this.performer = performer;
        this.customer = customer;
        this.date = new Date();
        this.items = new ArrayList<>();
        
        // Add this transaction to both performer and customer
        performer.addTransaction(this);
        customer.addTransaction(this);
    }
    
    // Getters and Setters
    public String getTransactionID() {
        return transactionID;
    }
    
    public Date getDate() {
        return date;
    }
    
    public double getTotal() {
        return total;
    }
    
    public User getPerformer() {
        return performer;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public List<Product> getItems() {
        return new ArrayList<>(items); // Return a copy to maintain encapsulation
    }
    
    public void setDiscount(Discount discount) {
        if (!performer.isAuthenticated()) {
            throw new SecurityException("User harus login terlebih dahulu untuk mengatur diskon");
        }
        this.discount = discount;
        calculateTotal(); // Recalculate total when discount is set
    }
    
    public void addItem(Product product) {
        if (!performer.isAuthenticated()) {
            throw new SecurityException("User harus login terlebih dahulu untuk menambah item");
        }
        if (product.getStock() > 0) {
            items.add(product);
            product.setStock(product.getStock() - 1);
            calculateTotal(); // Recalculate total when item is added
        } else {
            throw new IllegalStateException("Stok produk " + product.getName() + " habis");
        }
    }
    
    public void calculateTotal() {
        total = 0;
        for (Product item : items) {
            total += item.getEffectivePrice();
        }
        if (discount != null) {
            total = discount.applyDiscount(total);
        }
    }
    
    public void generateInvoice() {
        if (!performer.isAuthenticated()) {
            throw new SecurityException("User harus login terlebih dahulu untuk generate invoice");
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        StringBuilder invoice = new StringBuilder();
        
        // Header
        invoice.append("\n=================================\n");
        invoice.append("            INVOICE              \n");
        invoice.append("=================================\n\n");
        
        // Transaction details
        invoice.append("No. Transaksi: ").append(transactionID).append("\n");
        invoice.append("Tanggal: ").append(dateFormat.format(date)).append("\n");
        invoice.append("Kasir: ").append(performer.getUsername()).append("\n");
        invoice.append("Customer: ").append(customer.getCustomerID()).append("\n\n");
        
        // Items
        invoice.append("Items:\n");
        invoice.append("---------------------------------\n");
        for (Product item : items) {
            invoice.append(String.format("%-20s Rp %,10.2f%n", 
                item.getName(), item.getEffectivePrice()));
        }
        invoice.append("---------------------------------\n");
        
        // Discount if applicable
        if (discount != null) {
            invoice.append(String.format("Subtotal:%25s%,10.2f%n", "Rp ", 
                items.stream().mapToDouble(Product::getPrice).sum()));
            invoice.append(String.format("Discount (%s):%20s%,10.2f%n", 
                discount.getDiscountType(), "Rp ", 
                items.stream().mapToDouble(Product::getPrice).sum() - total));
        }
        
        // Total
        invoice.append(String.format("TOTAL:%28s%,10.2f%n", "Rp ", total));
        invoice.append("\n=================================\n");
        invoice.append("          Terima Kasih           \n");
        invoice.append("=================================\n");
        
        // Print invoice
        System.out.println(invoice.toString());
    }
}