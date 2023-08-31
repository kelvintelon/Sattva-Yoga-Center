package com.sattvayoga.dao;

import com.sattvayoga.dto.order.CheckoutItemDTO;
import com.sattvayoga.model.Event;
import com.sattvayoga.model.PackagePurchase;
import com.sattvayoga.model.PaginatedListOfPurchasedPackages;

import java.util.List;

public interface PackagePurchaseDao {

    void createPackagePurchase(PackagePurchase packagePurchase);

    List<PackagePurchase> getAllUserPackagePurchases(int userId);

    boolean expirePackage(PackagePurchase packagePurchase);

    boolean decrementByOne(int packagePurchaseId);

    boolean incrementByOne(int packagePurchaseId);

    boolean updatePackage(PackagePurchase packagePurchase);

    PackagePurchase getPackagePurchaseObjectByPackagePurchaseId(int packagePurchaseId);

    PackagePurchase filterPackageList(List<PackagePurchase> packagePurchaseList, Event event);

    List<PackagePurchase> getAllSharedActiveQuantityPackages(int client_id);

    PaginatedListOfPurchasedPackages getAllUserPaginatedPackagePurchases(int userId, int page, int limit, String sortBy, boolean sortDesc);

    PaginatedListOfPurchasedPackages getAllActiveUserPaginatedPackagePurchases(int userId, int page, int pageSize, String sortBy, boolean sortDesc);

    List<PackagePurchase> getAllActiveUserPackagePurchases(int userId);

    String generateGiftCardCode();

    void createGiftCard(String code, double amount);

    void createStripePackagePurchase(CheckoutItemDTO checkoutItemDTO);

    void createOneMonthAutoRenewPurchase(CheckoutItemDTO checkoutItemDTO);

    void createSixMonthAutoRenewPurchase(CheckoutItemDTO checkoutItemDTO);

    void createGiftCardPurchase(CheckoutItemDTO checkoutItemDTO);
}
