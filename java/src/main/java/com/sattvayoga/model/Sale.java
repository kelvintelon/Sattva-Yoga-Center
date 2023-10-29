package com.sattvayoga.model;

public class Sale {

    private int sale_id;
    private int[] packages_purchased_array;
    private int batch_number;

    public Sale() {
    }

    public int getSale_id() {
        return sale_id;
    }

    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
    }

    public int[] getPackages_purchased_array() {
        return packages_purchased_array;
    }

    public void setPackages_purchased_array(int[] packages_purchased_array) {
        this.packages_purchased_array = packages_purchased_array;
    }

    public int getBatch_number() {
        return batch_number;
    }

    public void setBatch_number(int batch_number) {
        this.batch_number = batch_number;
    }
}
