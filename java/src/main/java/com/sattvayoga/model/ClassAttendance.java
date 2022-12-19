package com.sattvayoga.model;

public class ClassAttendance {

    private String class_attendance_id;
    private String class_id;
    private String client_id;
    private String package_id;
    private String purchase_id;
    private String is_new_client;
    private String is_drop_in;
    private String drop_in_fee;
    private String mat_use_fee;
    private String is_guest;
    private String attendance_count;
    private String is_private;
    private String is_unlimited;

    public ClassAttendance(String class_attendance_id, String class_id, String client_id, String package_id, String purchase_id, String is_new_client, String is_drop_in, String drop_in_fee, String mat_use_fee, String is_guest, String attendance_count, String is_private, String is_unlimited) {
        this.class_attendance_id = class_attendance_id;
        this.class_id = class_id;
        this.client_id = client_id;
        this.package_id = package_id;
        this.purchase_id = purchase_id;
        this.is_new_client = is_new_client;
        this.is_drop_in = is_drop_in;
        this.drop_in_fee = drop_in_fee;
        this.mat_use_fee = mat_use_fee;
        this.is_guest = is_guest;
        this.attendance_count = attendance_count;
        this.is_private = is_private;
        this.is_unlimited = is_unlimited;
    }


    public String getClass_attendance_id() {
        return class_attendance_id;
    }

    public void setClass_attendance_id(String class_attendance_id) {
        this.class_attendance_id = class_attendance_id;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getPurchase_id() {
        return purchase_id;
    }

    public void setPurchase_id(String purchase_id) {
        this.purchase_id = purchase_id;
    }

    public String getIs_new_client() {
        return is_new_client;
    }

    public void setIs_new_client(String is_new_client) {
        this.is_new_client = is_new_client;
    }

    public String getIs_drop_in() {
        return is_drop_in;
    }

    public void setIs_drop_in(String is_drop_in) {
        this.is_drop_in = is_drop_in;
    }

    public String getDrop_in_fee() {
        return drop_in_fee;
    }

    public void setDrop_in_fee(String drop_in_fee) {
        this.drop_in_fee = drop_in_fee;
    }

    public String getMat_use_fee() {
        return mat_use_fee;
    }

    public void setMat_use_fee(String mat_use_fee) {
        this.mat_use_fee = mat_use_fee;
    }

    public String getIs_guest() {
        return is_guest;
    }

    public void setIs_guest(String is_guest) {
        this.is_guest = is_guest;
    }

    public String getAttendance_count() {
        return attendance_count;
    }

    public void setAttendance_count(String attendance_count) {
        this.attendance_count = attendance_count;
    }

    public String getIs_private() {
        return is_private;
    }

    public void setIs_private(String is_private) {
        this.is_private = is_private;
    }

    public String getIs_unlimited() {
        return is_unlimited;
    }

    public void setIs_unlimited(String is_unlimited) {
        this.is_unlimited = is_unlimited;
    }
}
