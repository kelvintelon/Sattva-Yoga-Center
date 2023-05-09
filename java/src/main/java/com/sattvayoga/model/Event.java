package com.sattvayoga.model;

import java.sql.Timestamp;
import java.util.List;

public class Event {
    private int event_id;
    private int class_id;
    private String event_name;
    private Timestamp start_time;
    private Timestamp end_time;
    private String color;
    private boolean timed;
    private boolean is_visible_online;
    private List<ClientDetails> attendanceList;
    private int package_purchase_id;
    // helper instance variable
    private String quick_details;

    public Event() {

    }

    public Event(int event_id, int class_id, String event_name, Timestamp start_time, Timestamp end_time, String color, boolean timed, boolean is_visible_online) {
        this.event_id = event_id;
        this.class_id = class_id;
        this.event_name = event_name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.color = color;
        this.timed = timed;
        this.is_visible_online = is_visible_online;
    }

    public String getQuick_details() {
        return quick_details;
    }

    public void setQuick_details(String quick_details) {
        this.quick_details = quick_details;
    }

    public int getPackage_purchase_id() {
        return package_purchase_id;
    }

    public void setPackage_purchase_id(int package_purchase_id) {
        this.package_purchase_id = package_purchase_id;
    }

    public List<ClientDetails> getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(List<ClientDetails> attendanceList) {
        this.attendanceList = attendanceList;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int class_id) {
        this.class_id = class_id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public Timestamp getStart_time() {
        return start_time;
    }

    public void setStart_time(Timestamp start_time) {
        this.start_time = start_time;
    }

    public Timestamp getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Timestamp end_time) {
        this.end_time = end_time;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isTimed() {
        return timed;
    }

    public void setTimed(boolean timed) {
        this.timed = timed;
    }

    public boolean isIs_visible_online() {
        return is_visible_online;
    }

    public void setIs_visible_online(boolean is_visible_online) {
        this.is_visible_online = is_visible_online;
    }
}
