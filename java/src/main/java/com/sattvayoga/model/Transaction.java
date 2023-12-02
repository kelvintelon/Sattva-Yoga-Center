package com.sattvayoga.model;

public class Transaction {

    private int transaction_id;
    private int sale_id;
    private int client_id;
    private String payment_type;
    private double payment_amount;
    private String gift_code = "";

    public Transaction() {
    }

    public String getGift_code() {
        return gift_code;
    }

    public void setGift_code(String gift_code) {
        this.gift_code = gift_code;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public int getSale_id() {
        return sale_id;
    }

    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public double getPayment_amount() {
        return payment_amount;
    }

    public void setPayment_amount(double payment_amount) {
        this.payment_amount = payment_amount;
    }
}
