package com.sattvayoga.model;

import java.sql.Timestamp;
import java.util.List;

public class ClassDetails {
    private int class_id;
    private int teacher_id;
    private Timestamp class_datetime;
    private int class_duration;
    private boolean is_paid;
    private String class_description;
    private String teacher_name;
    private List<ClientDetailsDTO> client_list;

    public ClassDetails(int class_id, int teacher_id, Timestamp class_datetime, int class_duration, boolean is_paid, String class_description, String teacher_name, List<ClientDetailsDTO> client_list) {
        this.class_id = class_id;
        this.teacher_id = teacher_id;
        this.class_datetime = class_datetime;
        this.class_duration = class_duration;
        this.is_paid = is_paid;
        this.class_description = class_description;
        this.teacher_name = teacher_name;
        this.client_list = client_list;
    }

    public ClassDetails(int class_id, int teacher_id, Timestamp class_datetime, int class_duration, boolean is_paid, String class_description) {
        this.class_id = class_id;
        this.teacher_id = teacher_id;
        this.class_datetime = class_datetime;
        this.class_duration = class_duration;
        this.is_paid = is_paid;
        this.class_description = class_description;
    }

    public ClassDetails() {

    }

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }

    public int getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
    }

    public Timestamp getClass_datetime() {
        return class_datetime;
    }

    public void setClass_datetime(Timestamp class_datetime) {
        this.class_datetime = class_datetime;
    }

    public int getClass_duration() {
        return class_duration;
    }

    public void setClass_duration(int class_duration) {
        this.class_duration = class_duration;
    }

    public boolean isIs_paid() {
        return is_paid;
    }

    public void setIs_paid(boolean is_paid) {
        this.is_paid = is_paid;
    }

    public String getClass_description() {
        return class_description;
    }

    public void setClass_description(String class_description) {
        this.class_description = class_description;
    }

    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public List<ClientDetailsDTO> getClient_list() {
        return client_list;
    }

    public void setClient_list(List<ClientDetailsDTO> client_list) {
        this.client_list = client_list;
    }
}
