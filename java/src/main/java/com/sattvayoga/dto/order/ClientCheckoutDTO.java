package com.sattvayoga.dto.order;

import com.sattvayoga.model.GiftCard;
import com.sattvayoga.model.PackageDetails;

import java.util.List;

public class ClientCheckoutDTO {

    private int client_id;
    private List<PackageDetails> selectedCheckoutPackages;
    private int balance;
    private int discount;
    private int cash;
    private int check;
    private String emailForGift;
    private String emailForReceipt;
    private boolean saveEmailGiftCardPurchase;
    private boolean saveEmailReceiptPurchase;
    private String renewalDate;
    private String paymentMethodId;
    private boolean saveCard;
    private boolean saveAsRecurringPayment;
    private int iterations;
    private int subscriptionDuration;
    private GiftCard giftCard;
    private boolean compFree;

    public ClientCheckoutDTO(int client_id, List<PackageDetails> selectedCheckoutPackages, int total, String email, int discount, boolean saveEmail, String renewalDate) {
        this.client_id = client_id;
        this.selectedCheckoutPackages = selectedCheckoutPackages;
        this.balance = total;
        this.emailForGift = email;
        this.discount = discount;
        this.saveEmailGiftCardPurchase = saveEmail;
        this.renewalDate = renewalDate;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public boolean isCompFree() {
        return compFree;
    }

    public void setCompFree(boolean compFree) {
        this.compFree = compFree;
    }

    public boolean isSaveEmailReceiptPurchase() {
        return saveEmailReceiptPurchase;
    }

    public void setSaveEmailReceiptPurchase(boolean saveEmailReceiptPurchase) {
        this.saveEmailReceiptPurchase = saveEmailReceiptPurchase;
    }

    public int getSubscriptionDuration() {
        return subscriptionDuration;
    }

    public void setSubscriptionDuration(int subscriptionDuration) {
        this.subscriptionDuration = subscriptionDuration;
    }

    public String getEmailForReceipt() {
        return emailForReceipt;
    }

    public void setEmailForReceipt(String emailForReceipt) {
        this.emailForReceipt = emailForReceipt;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public boolean isSaveAsRecurringPayment() {
        return saveAsRecurringPayment;
    }

    public void setSaveAsRecurringPayment(boolean saveAsRecurringPayment) {
        this.saveAsRecurringPayment = saveAsRecurringPayment;
    }

    public boolean isSaveCard() {
        return saveCard;
    }

    public void setSaveCard(boolean saveCard) {
        this.saveCard = saveCard;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public List<PackageDetails> getListOfPackages() {
        return selectedCheckoutPackages;
    }

    public void setListOfPackages(List<PackageDetails> selectedCheckoutPackages) {
        this.selectedCheckoutPackages = selectedCheckoutPackages;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getEmailForGift() {
        return emailForGift;
    }

    public void setEmailForGift(String emailForGift) {
        this.emailForGift = emailForGift;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public boolean isSaveEmailGiftCardPurchase() {
        return saveEmailGiftCardPurchase;
    }

    public void setSaveEmailGiftCardPurchase(boolean saveEmailGiftCardPurchase) {
        this.saveEmailGiftCardPurchase = saveEmailGiftCardPurchase;
    }

    public String getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(String renewalDate) {
        this.renewalDate = renewalDate;
    }

    public List<PackageDetails> getSelectedCheckoutPackages() {
        return selectedCheckoutPackages;
    }

    public void setSelectedCheckoutPackages(List<PackageDetails> selectedCheckoutPackages) {
        this.selectedCheckoutPackages = selectedCheckoutPackages;
    }

    public GiftCard getGiftCard() {
        return giftCard;
    }

    public void setGiftCard(GiftCard giftCard) {
        this.giftCard = giftCard;
    }
}
