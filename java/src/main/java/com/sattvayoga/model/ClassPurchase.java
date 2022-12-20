package com.sattvayoga.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class ClassPurchase {

    private int class_purchase_id;
    private int client_id;
    private String classes_purchased;
    private Timestamp date_purchased;
    private Date activation_date;
    private Date expiration_date;
    private boolean expired;
    private int classes_remaining;
    private BigDecimal amount_paid;
    private BigDecimal cost_per_class;
    private boolean is_monthly_renew;
    private Date next_renew_date;
    private String notes;
    private Date date_of_entry;
    private boolean exclude_from_totals;

    public ClassPurchase(int class_purchase_id, int client_id, String classes_purchased, Timestamp date_purchased, Date activation_date, Date expiration_date, boolean expired, int classes_remaining, BigDecimal amount_paid, BigDecimal cost_per_class, boolean is_monthly_renew, Date next_renew_date, String notes, Date date_of_entry, boolean exclude_from_totals) {
        this.class_purchase_id = class_purchase_id;
        this.client_id = client_id;
        this.classes_purchased = classes_purchased;
        this.date_purchased = date_purchased;
        this.activation_date = activation_date;
        this.expiration_date = expiration_date;
        this.expired = expired;
        this.classes_remaining = classes_remaining;
        this.amount_paid = amount_paid;
        this.cost_per_class = cost_per_class;
        this.is_monthly_renew = is_monthly_renew;
        this.next_renew_date = next_renew_date;
        this.notes = notes;
        this.date_of_entry = date_of_entry;
        this.exclude_from_totals = exclude_from_totals;
    }

    public int getClass_purchase_id() {
        return class_purchase_id;
    }

    public void setClass_purchase_id(int class_purchase_id) {
        this.class_purchase_id = class_purchase_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getClasses_purchased() {
        return classes_purchased;
    }

    public void setClasses_purchased(String classes_purchased) {
        this.classes_purchased = classes_purchased;
    }

    public Timestamp getDate_purchased() {
        return date_purchased;
    }

    public void setDate_purchased(Timestamp date_purchased) {
        this.date_purchased = date_purchased;
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

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public int getClasses_remaining() {
        return classes_remaining;
    }

    public void setClasses_remaining(int classes_remaining) {
        this.classes_remaining = classes_remaining;
    }

    public BigDecimal getAmount_paid() {
        return amount_paid;
    }

    public void setAmount_paid(BigDecimal amount_paid) {
        this.amount_paid = amount_paid;
    }

    public BigDecimal getCost_per_class() {
        return cost_per_class;
    }

    public void setCost_per_class(BigDecimal cost_per_class) {
        this.cost_per_class = cost_per_class;
    }

    public boolean isIs_monthly_renew() {
        return is_monthly_renew;
    }

    public void setIs_monthly_renew(boolean is_monthly_renew) {
        this.is_monthly_renew = is_monthly_renew;
    }

    public Date getNext_renew_date() {
        return next_renew_date;
    }

    public void setNext_renew_date(Date next_renew_date) {
        this.next_renew_date = next_renew_date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getDate_of_entry() {
        return date_of_entry;
    }

    public void setDate_of_entry(Date date_of_entry) {
        this.date_of_entry = date_of_entry;
    }

    public boolean isExclude_from_totals() {
        return exclude_from_totals;
    }

    public void setExclude_from_totals(boolean exclude_from_totals) {
        this.exclude_from_totals = exclude_from_totals;
    }
}
