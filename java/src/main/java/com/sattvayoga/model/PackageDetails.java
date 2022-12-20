package com.sattvayoga.model;

import java.math.BigDecimal;

public class PackageDetails {

    private int package_id;
    private String description;
    private BigDecimal package_cost;
    private BigDecimal cost_per_class;

    public PackageDetails(int package_id, String description, BigDecimal package_cost, BigDecimal cost_per_class) {
        this.package_id = package_id;
        this.description = description;
        this.package_cost = package_cost;
        this.cost_per_class = cost_per_class;
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

    public BigDecimal getCost_per_class() {
        return cost_per_class;
    }

    public void setCost_per_class(BigDecimal cost_per_class) {
        this.cost_per_class = cost_per_class;
    }
}
