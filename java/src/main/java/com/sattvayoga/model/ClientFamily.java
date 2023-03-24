package com.sattvayoga.model;

public class ClientFamily {
    private int client_id;
    private int family_id;
    //helper
    private String family_name;

    public ClientFamily(){};

    public ClientFamily(int client_id, int family_id) {
        this.client_id = client_id;
        this.family_id = family_id;
    }

    public ClientFamily(int client_id, String family_name) {
        this.client_id = client_id;
        this.family_name = family_name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getFamily_id() {
        return family_id;
    }

    public void setFamily_id(int family_id) {
        this.family_id = family_id;
    }
}
