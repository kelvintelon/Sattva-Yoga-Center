package com.sattvayoga.model;

public class ClientDetailsDTO {
    private int client_id;
    private String last_name;
    private String first_name;

    public ClientDetailsDTO(int client_id, String last_name, String first_name) {
        this.client_id = client_id;
        this.last_name = last_name;
        this.first_name = first_name;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }
}
