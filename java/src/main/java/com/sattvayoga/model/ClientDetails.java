package com.sattvayoga.model;

import java.sql.Timestamp;
import java.util.Objects;

public class ClientDetails {
    private int client_id;
    private String last_name;
    private String first_name;
    private boolean is_client_active;
    private boolean is_new_client;
    private String street_address;
    private String city;
    private String state_abbreviation;
    private String country;
    private String zip_code;
    private String phone_number;
    private boolean is_on_email_list;
    private String email;
    private boolean has_record_of_liability;
    private Timestamp date_of_entry;
    private int user_id;
    private boolean redFlag;
    //helper
    private String full_address;

    //helper
    private String quick_details;

    //helper
    private String family_name;
    private int family_id;

    //HELPER
    private String username;


    private boolean is_allowed_video;

    private String customer_id;


    public ClientDetails(int client_id, String last_name, String first_name, boolean is_client_active, boolean is_new_client, String street_address, String city, String state_abbreviation, String zip_code, String phone_number, boolean is_on_email_list, String email, boolean has_record_of_liability, Timestamp date_of_entry, int user_id) {
        this.client_id = client_id;
        this.last_name = last_name;
        this.first_name = first_name;
        this.is_client_active = is_client_active;
        this.is_new_client = is_new_client;
        this.street_address = street_address;
        this.city = city;
        this.state_abbreviation = state_abbreviation;
        this.zip_code = zip_code;
        this.phone_number = phone_number;
        this.is_on_email_list = is_on_email_list;
        this.email = email;
        this.has_record_of_liability = has_record_of_liability;
        this.date_of_entry = date_of_entry;
        this.user_id = user_id;
    }

    public ClientDetails(int client_id, String last_name, String first_name, boolean is_client_active, boolean is_new_client, String street_address, String city, String state_abbreviation, String zip_code, String phone_number, boolean is_on_email_list, String email, boolean has_record_of_liability, Timestamp date_of_entry, int user_id, boolean redFlag, String full_address, String quick_details, String family_name) {
        this.client_id = client_id;
        this.last_name = last_name;
        this.first_name = first_name;
        this.is_client_active = is_client_active;
        this.is_new_client = is_new_client;
        this.street_address = street_address;
        this.city = city;
        this.state_abbreviation = state_abbreviation;
        this.zip_code = zip_code;
        this.phone_number = phone_number;
        this.is_on_email_list = is_on_email_list;
        this.email = email;
        this.has_record_of_liability = has_record_of_liability;
        this.date_of_entry = date_of_entry;
        this.user_id = user_id;
        this.redFlag = redFlag;
        this.full_address = full_address;
        this.quick_details = quick_details;
        this.family_name = family_name;
    }

    public ClientDetails() {
    }

    public ClientDetails(int client_id, String last_name, String first_name) {
        this.client_id = client_id;
        this.last_name = last_name;
        this.first_name = first_name;
    }

    public int getFamily_id() {
        return family_id;
    }

    public void setFamily_id(int family_id) {
        this.family_id = family_id;
    }

    public boolean isIs_allowed_video() {
        return is_allowed_video;
    }

    public void setIs_allowed_video(boolean is_allowed_video) {
        this.is_allowed_video = is_allowed_video;
    }

    public boolean isRedFlag() {
        return redFlag;
    }

    public void setRedFlag(boolean redFlag) {
        this.redFlag = redFlag;
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

    public boolean isIs_client_active() {
        return is_client_active;
    }

    public void setIs_client_active(boolean is_client_active) {
        this.is_client_active = is_client_active;
    }

    public boolean isIs_new_client() {
        return is_new_client;
    }

    public void setIs_new_client(boolean is_new_client) {
        this.is_new_client = is_new_client;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState_abbreviation() {
        return state_abbreviation;
    }

    public void setState_abbreviation(String state_abbreviation) {
        this.state_abbreviation = state_abbreviation;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public boolean isIs_on_email_list() {
        return is_on_email_list;
    }

    public void setIs_on_email_list(boolean is_on_email_list) {
        this.is_on_email_list = is_on_email_list;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isHas_record_of_liability() {
        return has_record_of_liability;
    }

    public void setHas_record_of_liability(boolean has_record_of_liability) {
        this.has_record_of_liability = has_record_of_liability;
    }

    public Timestamp getDate_of_entry() {
        return date_of_entry;
    }

    public void setDate_of_entry(Timestamp date_of_entry) {
        this.date_of_entry = date_of_entry;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFull_address() {
        return full_address;
    }

    public void setFull_address(String full_address) {
        this.full_address = full_address;
    }

    public String getQuick_details() {
        return quick_details;
    }

    public void setQuick_details(String quick_details) {
        this.quick_details = quick_details;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientDetails that = (ClientDetails) o;
        return client_id == that.client_id && is_client_active == that.is_client_active && is_new_client == that.is_new_client && is_on_email_list == that.is_on_email_list && has_record_of_liability == that.has_record_of_liability && user_id == that.user_id && redFlag == that.redFlag && is_allowed_video == that.is_allowed_video && Objects.equals(last_name, that.last_name) && Objects.equals(first_name, that.first_name) && Objects.equals(street_address, that.street_address) && Objects.equals(city, that.city) && Objects.equals(state_abbreviation, that.state_abbreviation) && Objects.equals(zip_code, that.zip_code) && Objects.equals(phone_number, that.phone_number) && Objects.equals(email, that.email) && Objects.equals(date_of_entry, that.date_of_entry) && Objects.equals(full_address, that.full_address) && Objects.equals(quick_details, that.quick_details) && Objects.equals(family_name, that.family_name) && Objects.equals(username, that.username) && Objects.equals(customer_id, that.customer_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client_id, last_name, first_name, is_client_active, is_new_client, street_address, city, state_abbreviation, zip_code, phone_number, is_on_email_list, email, has_record_of_liability, date_of_entry, user_id, redFlag, full_address, quick_details, family_name, username, is_allowed_video, customer_id);
    }
}


