package com.sattvayoga.model;

import java.util.ArrayList;
import java.util.List;

public class Family {
    private int family_id;
    private String family_name;

    //helper
    private String quick_details;

    //helper
    private List<ClientDetails> listOfFamilyMembers = new ArrayList<>();

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

    public List<ClientDetails> getListOfFamilyMembers() {
        return listOfFamilyMembers;
    }

    public void setListOfFamilyMembers(List<ClientDetails> listOfFamilyMembers) {
        this.listOfFamilyMembers = listOfFamilyMembers;
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
