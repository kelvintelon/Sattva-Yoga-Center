package com.sattvayoga.model;

import java.util.List;

public class PaginatedListOfPurchasedPackages {

    private List<PackagePurchase> listOfPurchasedPackages;
    private int totalRows;

    public PaginatedListOfPurchasedPackages() {
    }

    public PaginatedListOfPurchasedPackages(List<PackagePurchase> listOfPurchasedPackages, int totalRows) {
        this.listOfPurchasedPackages = listOfPurchasedPackages;
        this.totalRows = totalRows;
    }

    public List<PackagePurchase> getListOfPurchasedPackages() {
        return listOfPurchasedPackages;
    }

    public void setListOfPurchasedPackages(List<PackagePurchase> listOfPurchasedPackages) {
        this.listOfPurchasedPackages = listOfPurchasedPackages;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }
}
