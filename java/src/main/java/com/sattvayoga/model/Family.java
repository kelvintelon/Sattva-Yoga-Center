package com.sattvayoga.model;

public class Family {
    private int family_id;
    private String family_name;
    //helper
    private String quick_details;

    public Family(){};

    public Family(int family_id, String family_name) {
        this.family_id = family_id;
        this.family_name = family_name;
    }

    public Family(int family_id, String family_name, String quick_details) {
        this.family_id = family_id;
        this.family_name = family_name;
        this.quick_details = quick_details;
    }

    public int getFamily_id() {
        return family_id;
    }

    public void setFamily_id(int family_id) {
        this.family_id = family_id;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getQuick_details() {
        return quick_details;
    }

    public void setQuick_details(String quick_details) {
        this.quick_details = quick_details;
    }
}
