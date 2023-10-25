package com.sattvayoga.dto.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CheckoutItemDTO {
    private String productName;
    private int quantity;
    private double price; // not necessary
    private int client_id;
    private int package_id;
    private LocalDateTime date_purchased;
    private int classes_remaining;
    private BigDecimal total_amount_paid;
    private boolean is_monthly_renew;
    private String paymentId;
    private int discount = 0;
    private String giftCardEmail = "";
    private boolean saveGiftCardEmail = false;
    private int subscriptionDuration;

    public CheckoutItemDTO(){
    }

    public int getSubscriptionDuration() {
        return subscriptionDuration;
    }

    public void setSubscriptionDuration(int subscriptionDuration) {
        this.subscriptionDuration = subscriptionDuration;
    }

    public String getGiftCardEmail() {
        return giftCardEmail;
    }

    public void setGiftCardEmail(String giftCardEmail) {
        this.giftCardEmail = giftCardEmail;
    }

    public boolean isSaveGiftCardEmail() {
        return saveGiftCardEmail;
    }

    public void setSaveGiftCardEmail(boolean saveGiftCardEmail) {
        this.saveGiftCardEmail = saveGiftCardEmail;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
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

    public BigDecimal getTotal_amount_paid() {
        return total_amount_paid;
    }

    public void setTotal_amount_paid(BigDecimal total_amount_paid) {
        this.total_amount_paid = total_amount_paid;
    }

    public boolean isIs_monthly_renew() {
        return is_monthly_renew;
    }

    public void setIs_monthly_renew(boolean is_monthly_renew) {
        this.is_monthly_renew = is_monthly_renew;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
