package com.sattvayoga.model;

public class ClientEvent {

    private int event_id;
    private int client_id;
    private int package_purchase_id;

    public ClientEvent(int event_id, int client_id, int package_purchase_id) {
        this.event_id = event_id;
        this.client_id = client_id;
        this.package_purchase_id = package_purchase_id;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getPackage_purchase_id() {
        return package_purchase_id;
    }

    public void setPackage_purchase_id(int package_purchase_id) {
        this.package_purchase_id = package_purchase_id;
    }
}
