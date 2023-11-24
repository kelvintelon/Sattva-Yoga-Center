package com.sattvayoga.model;

import java.util.ArrayList;
import java.util.List;

public class Sale {

    private int sale_id;
    private int[] packages_purchased_array;

    private List<Integer> packages_purchased_list = new ArrayList<>();
    private int batch_number;
    private int client_id;

    public Sale() {
    }

    public List<Integer> getPackages_purchased_list() {
        return packages_purchased_list;
    }

    public void setPackages_purchased_list(List<Integer> packages_purchased_list) {
        this.packages_purchased_list = packages_purchased_list;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
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
