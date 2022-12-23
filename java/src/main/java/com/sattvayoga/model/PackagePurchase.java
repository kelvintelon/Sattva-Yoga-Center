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
    private BigDecimal total_amount_paid;
    private boolean is_monthly_renew;
    private BigDecimal discount;

    public PackagePurchase(int package_purchase_id, int client_id, Timestamp date_purchased, int package_id, boolean is_expired,
                           BigDecimal total_amount_paid, boolean is_monthly_renew, BigDecimal discount) {
        this.package_purchase_id = package_purchase_id;
        this.client_id = client_id;
        this.date_purchased = date_purchased;
        this.package_id = package_id;
        this.is_expired = is_expired;
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
