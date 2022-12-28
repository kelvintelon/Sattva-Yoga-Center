package com.sattvayoga.model;

import java.sql.Timestamp;
import java.util.List;

public class ClassDetails {
    private int class_id;
    private int teacher_id;
    private int class_duration;
    private boolean is_paid;
    private String class_description;

    // calendar properties
    private boolean is_repeating;
    private String[] date_range;
    private String start_time;

    // additional information properties
    private List<ClientDetailsDTO> client_list;
    private String teacher_name;

    public ClassDetails(int class_id, int teacher_id, int class_duration, boolean is_paid, String class_description, String teacher_name, boolean is_repeating, String[] date_range, String start_time) {
        this.class_id = class_id;
        this.teacher_id = teacher_id;
        this.class_duration = class_duration;
        this.is_paid = is_paid;
        this.class_description = class_description;
        this.teacher_name = teacher_name;
        this.is_repeating = is_repeating;
        this.date_range = date_range;
        this.start_time = start_time;
    }

    public ClassDetails(int class_id, int teacher_id, int class_duration, boolean is_paid, String class_description, String teacher_name, boolean is_repeating, String[] date_range, String start_time, List<ClientDetailsDTO> client_list) {
        this.class_id = class_id;
        this.teacher_id = teacher_id;
        this.class_duration = class_duration;
        this.is_paid = is_paid;
        this.class_description = class_description;
        this.teacher_name = teacher_name;
        this.is_repeating = is_repeating;
        this.date_range = date_range;
        this.start_time = start_time;
        this.client_list = client_list;
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

    public boolean isIs_repeating() {
        return is_repeating;
    }

    public void setIs_repeating(boolean is_repeating) {
        this.is_repeating = is_repeating;
    }

    public String[] getDate_range() {
        return date_range;
    }

    public void setDate_range(String[] date_range) {
        this.date_range = date_range;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

}
