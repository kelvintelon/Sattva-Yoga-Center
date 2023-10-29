package com.sattvayoga.dao;

import com.sattvayoga.dto.order.CheckoutItemDTO;
import com.sattvayoga.model.ClassEvent;
import com.sattvayoga.model.GiftCard;
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

    PackagePurchase filterPackageList(List<PackagePurchase> packagePurchaseList, ClassEvent classEvent);

    List<PackagePurchase> getAllSharedActiveQuantityPackages(int client_id);

    PaginatedListOfPurchasedPackages getAllUserPaginatedPackagePurchases(int userId, int page, int limit, String sortBy, boolean sortDesc);

    PaginatedListOfPurchasedPackages getAllActiveUserPaginatedPackagePurchases(int userId, int page, int pageSize, String sortBy, boolean sortDesc);

    List<PackagePurchase> getAllActiveUserPackagePurchases(int userId);

    String generateGiftCardCode();

    void createGiftCard(String code, double amount);

    int createStripePackagePurchase(CheckoutItemDTO checkoutItemDTO);

    int createAdminPackagePurchase(PackagePurchase packagePurchase);

    void createOneMonthAutoRenewPurchase(CheckoutItemDTO checkoutItemDTO);

    void createSixMonthAutoRenewPurchase(CheckoutItemDTO checkoutItemDTO);

    int createGiftCardPurchase(CheckoutItemDTO checkoutItemDTO);

    void purchaseLineItems(List<CheckoutItemDTO> itemList);

    GiftCard retrieveGiftCard(String code);

    boolean updateGiftCard(GiftCard originalGiftCard, int clientId, int amountUsed);
}
