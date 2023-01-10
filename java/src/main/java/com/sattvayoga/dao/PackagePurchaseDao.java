package com.sattvayoga.dao;

import com.sattvayoga.model.PackagePurchase;

import java.util.List;

public interface PackagePurchaseDao {

    void createPackagePurchase(PackagePurchase packagePurchase);

    List<PackagePurchase> getAllUserPackagePurchases(int userId);

    boolean expirePackage(PackagePurchase packagePurchase);
}
