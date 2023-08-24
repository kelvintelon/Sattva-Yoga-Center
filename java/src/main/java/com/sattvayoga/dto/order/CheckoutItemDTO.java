package com.sattvayoga.dto.order;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CheckoutItemDTO {
    private String productName;
    private int quantity;
    private double price;
    private int client_id;
    private int package_id;
    private LocalDateTime date_purchased;
    private int classes_remaining;

    public CheckoutItemDTO(){
    }

    public int getClasses_remaining() {
        return classes_remaining;
    }

    public void setClasses_remaining(int classes_remaining) {
        this.classes_remaining = classes_remaining;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getPackage_id() {
        return package_id;
    }

    public void setPackage_id(int package_id) {
        this.package_id = package_id;
    }

    public LocalDateTime getDate_purchased() {
        return date_purchased;
    }

    public void setDate_purchased(LocalDateTime date_purchased) {
        this.date_purchased = date_purchased;
    }
}
