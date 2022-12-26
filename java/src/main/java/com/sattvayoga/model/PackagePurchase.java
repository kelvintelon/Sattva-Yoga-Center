package com.sattvayoga.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class PackagePurchase {

    private int package_purchase_id;
    private int client_id;
    private Timestamp date_purchased;
    private int package_id;
    private boolean is_expired;
    private int classes_remaining;
    private Date activation_date;
    private Date expiration_date;
    private BigDecimal total_amount_paid;
    private boolean is_monthly_renew;
    private BigDecimal discount;

    public PackagePurchase(int package_purchase_id, int client_id, Timestamp date_purchased, int package_id,
                           boolean is_expired, int classes_remaining, Date activation_date,
                           Date expiration_date, BigDecimal total_amount_paid, boolean is_monthly_renew,
                           BigDecimal discount) {
        this.package_purchase_id = package_purchase_id;
        this.client_id = client_id;
        this.date_purchased = date_purchased;
        this.package_id = package_id;
        this.is_expired = is_expired;
        this.classes_remaining = classes_remaining;
        this.activation_date = activation_date;
        this.expiration_date = expiration_date;
        this.total_amount_paid = total_amount_paid;
        this.is_monthly_renew = is_monthly_renew;
        this.discount = discount;
    }

    public int getPackage_purchase_id() {
        return package_purchase_id;
    }

    public void setPackage_purchase_id(int package_purchase_id) {
        this.package_purchase_id = package_purchase_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public Timestamp getDate_purchased() {
        return date_purchased;
    }

    public void setDate_purchased(Timestamp date_purchased) {
        this.date_purchased = date_purchased;
    }

    public int getPackage_id() {
        return package_id;
    }

    public void setPackage_id(int package_id) {
        this.package_id = package_id;
    }

    public boolean isIs_expired() {
        return is_expired;
    }

    public void setIs_expired(boolean is_expired) {
        this.is_expired = is_expired;
    }

    public int getClasses_remaining() {
        return classes_remaining;
    }

    public void setClasses_remaining(int classes_remaining) {
        this.classes_remaining = classes_remaining;
    }

    public Date getActivation_date() {
        return activation_date;
    }

    public void setActivation_date(Date activation_date) {
        this.activation_date = activation_date;
    }

    public Date getExpiration_date() {
        return expiration_date;
    }

    public void setExpiration_date(Date expiration_date) {
        this.expiration_date = expiration_date;
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

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
}
