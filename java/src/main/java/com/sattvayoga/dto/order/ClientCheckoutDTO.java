package com.sattvayoga.dto.order;

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


    public ClientCheckoutDTO(int client_id, List<PackageDetails> selectedCheckoutPackages, int total, String email, int discount, boolean saveEmail, String renewalDate) {
        this.client_id = client_id;
        this.selectedCheckoutPackages = selectedCheckoutPackages;
        this.total = total;
        this.email = email;
        this.discount = discount;
        this.saveEmail = saveEmail;
        this.renewalDate = renewalDate;
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
}
