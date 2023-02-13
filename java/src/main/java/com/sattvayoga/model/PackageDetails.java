package com.sattvayoga.model;

import java.math.BigDecimal;

public class PackageDetails {

    private int package_id;
    private String description;
    private BigDecimal package_cost;
    private int classes_amount;
    private int subscription_duration;
    private boolean is_subscription;

    private boolean is_visible_online;

    public PackageDetails(int package_id, String description, BigDecimal package_cost, int classes_amount, int subscription_duration, boolean is_subscription, boolean is_visible_online) {
        this.package_id = package_id;
        this.description = description;
        this.package_cost = package_cost;
        this.classes_amount = classes_amount;
        this.subscription_duration = subscription_duration;
        this.is_subscription = is_subscription;
        this.is_visible_online = is_visible_online;
    }

    public PackageDetails() {
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

    public boolean isIs_subscription() {
        return is_subscription;
    }

    public void setIs_subscription(boolean is_subscription) {
        this.is_subscription = is_subscription;
    }

    public int getSubscription_duration() {
        return subscription_duration;
    }

    public void setSubscription_duration(int subscription_duration) {
        this.subscription_duration = subscription_duration;
    }

    public boolean isIs_visible_online() {
        return is_visible_online;
    }

    public void setIs_visible_online(boolean is_visible_online) {
        this.is_visible_online = is_visible_online;
    }
}
