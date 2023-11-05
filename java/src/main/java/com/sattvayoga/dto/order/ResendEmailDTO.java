package com.sattvayoga.dto.order;

public class ResendEmailDTO {

    private int package_purchase_id;
    private String email;

    public ResendEmailDTO() {
    }

    public int getPackage_purchase_id() {
        return package_purchase_id;
    }

    public void setPackage_purchase_id(int package_purchase_id) {
        this.package_purchase_id = package_purchase_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
