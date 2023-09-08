package com.sattvayoga.model;

public class GiftCard {

    private String code;
    private double amount;
    private double client_id;

    public GiftCard() {
    }

    public GiftCard(String code, int amount, int client_id) {
        this.code = code;
        this.amount = amount;
        this.client_id = client_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getClient_id() {
        return client_id;
    }

    public void setClient_id(double client_id) {
        this.client_id = client_id;
    }
}
