package com.sattvayoga.dto.order;

import com.sattvayoga.model.GiftCard;
import com.sattvayoga.model.PackageDetails;

import java.util.List;

public class ClientCheckoutDTO {

    private int client_id;
    private List<PackageDetails> selectedCheckoutPackages;
    private int total;
    private String email;
    private int discount;
    private boolean saveEmail;
    private String renewalDate;
    private String paymentMethodId;
    private boolean saveCard;
    private boolean saveAsRecurringPayment;
    private int iterations;

    private GiftCard giftCard;

    public ClientCheckoutDTO(int client_id, List<PackageDetails> selectedCheckoutPackages, int total, String email, int discount, boolean saveEmail, String renewalDate) {
        this.client_id = client_id;
        this.selectedCheckoutPackages = selectedCheckoutPackages;
        this.total = total;
        this.email = email;
        this.discount = discount;
        this.saveEmail = saveEmail;
        this.renewalDate = renewalDate;
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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public boolean isSaveEmail() {
        return saveEmail;
    }

    public void setSaveEmail(boolean saveEmail) {
        this.saveEmail = saveEmail;
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
