package com.sattvayoga.model;

import java.math.BigDecimal;

public class ClassAttendance {

    private int class_attendance_id;
    private int class_id;
    private int client_id;
    private int package_id;
    private int class_purchase_id;
    private boolean is_new_client;
    private boolean is_drop_in;
    private BigDecimal drop_in_fee;
    private BigDecimal mat_use_fee;
    private boolean is_guest;
    private int attendance_count; // not including it in the constructors until we figure out what it's for

    public ClassAttendance(int class_attendance_id, int class_id, int client_id, int package_id, int class_purchase_id, boolean is_new_client, boolean is_drop_in, BigDecimal drop_in_fee, BigDecimal mat_use_fee, boolean is_guest, int attendance_count) {
        this.class_attendance_id = class_attendance_id;
        this.class_id = class_id;
        this.client_id = client_id;
        this.package_id = package_id;
        this.class_purchase_id = class_purchase_id;
        this.is_new_client = is_new_client;
        this.is_drop_in = is_drop_in;
        this.drop_in_fee = drop_in_fee;
        this.mat_use_fee = mat_use_fee;
        this.is_guest = is_guest;
        this.attendance_count = attendance_count;
    }

    public int getClass_attendance_id() {
        return class_attendance_id;
    }

    public void setClass_attendance_id(int class_attendance_id) {
        this.class_attendance_id = class_attendance_id;
    }

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getPackage_id() {
        return package_id;
    }

    public void setPackage_id(int package_id) {
        this.package_id = package_id;
    }

    public int getClass_purchase_id() {
        return class_purchase_id;
    }

    public void setClass_purchase_id(int class_purchase_id) {
        this.class_purchase_id = class_purchase_id;
    }

    public boolean isIs_new_client() {
        return is_new_client;
    }

    public void setIs_new_client(boolean is_new_client) {
        this.is_new_client = is_new_client;
    }

    public boolean isIs_drop_in() {
        return is_drop_in;
    }

    public void setIs_drop_in(boolean is_drop_in) {
        this.is_drop_in = is_drop_in;
    }

    public BigDecimal getDrop_in_fee() {
        return drop_in_fee;
    }

    public void setDrop_in_fee(BigDecimal drop_in_fee) {
        this.drop_in_fee = drop_in_fee;
    }

    public BigDecimal getMat_use_fee() {
        return mat_use_fee;
    }

    public void setMat_use_fee(BigDecimal mat_use_fee) {
        this.mat_use_fee = mat_use_fee;
    }

    public boolean isIs_guest() {
        return is_guest;
    }

    public void setIs_guest(boolean is_guest) {
        this.is_guest = is_guest;
    }

    public int getAttendance_count() {
        return attendance_count;
    }

    public void setAttendance_count(int attendance_count) {
        this.attendance_count = attendance_count;
    }
}
