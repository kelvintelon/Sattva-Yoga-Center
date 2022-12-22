package com.sattvayoga.model;

import java.math.BigDecimal;
import java.sql.Date;

public class PackageDetails {

    private int package_id;
    private String description;
    private BigDecimal package_cost;
    private Date activation_date;
    private Date expiration_date;
    private int classes_remaining;


    public PackageDetails(int package_id, String description, BigDecimal package_cost, Date activation_date, Date expiration_date, int classes_remaining) {
        this.package_id = package_id;
        this.description = description;
        this.package_cost = package_cost;
        this.activation_date = activation_date;
        this.expiration_date = expiration_date;
        this.classes_remaining = classes_remaining;
    }

    public int getPackage_id() {
        return package_id;
    }

    public void setPackage_id(int package_id) {
        this.package_id = package_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPackage_cost() {
        return package_cost;
    }

    public void setPackage_cost(BigDecimal package_cost) {
        this.package_cost = package_cost;
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

    public int getClasses_remaining() {
        return classes_remaining;
    }

    public void setClasses_remaining(int classes_remaining) {
        this.classes_remaining = classes_remaining;
    }
}
