package com.sattvayoga.model;

import java.math.BigDecimal;

public class PackageDetails {

    private int package_id;
    private String description;
    private BigDecimal package_cost;
    private int classes_amount;
    private int package_duration;
    private boolean unlimited;

    private boolean is_visible_online;
    private boolean is_recurring;

    private boolean active;

    private int package_order;

    private int quantity;

    public PackageDetails(int package_id, String description, BigDecimal package_cost, int classes_amount, int package_duration, boolean unlimited, boolean is_visible_online, boolean is_recurring) {
        this.package_id = package_id;
        this.description = description;
        this.package_cost = package_cost;
        this.classes_amount = classes_amount;
        this.package_duration = package_duration;
        this.unlimited = unlimited;
        this.is_visible_online = is_visible_online;
        this.is_recurring = is_recurring;
    }

    public PackageDetails() {
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPackage_order() {
        return package_order;
    }

    public void setPackage_order(int package_order) {
        this.package_order = package_order;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public int getClasses_amount() {
        return classes_amount;
    }

    public void setClasses_amount(int classes_amount) {
        this.classes_amount = classes_amount;
    }

    public boolean isUnlimited() {
        return unlimited;
    }

    public void setUnlimited(boolean unlimited) {
        this.unlimited = unlimited;
    }

    public int getPackage_duration() {
        return package_duration;
    }

    public void setPackage_duration(int package_duration) {
        this.package_duration = package_duration;
    }

    public boolean isIs_visible_online() {
        return is_visible_online;
    }

    public void setIs_visible_online(boolean is_visible_online) {
        this.is_visible_online = is_visible_online;
    }

    public boolean isIs_recurring() {
        return is_recurring;
    }

    public void setIs_recurring(boolean is_recurring) {
        this.is_recurring = is_recurring;
    }
}
