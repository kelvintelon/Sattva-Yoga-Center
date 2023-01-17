package com.sattvayoga.model;

import java.sql.Timestamp;

public class Event {
    private int event_id;
    private int class_id;
    private String event_name;
    private Timestamp start_time;
    private Timestamp end_time;
    private String color;
    private boolean timed;

    public Event() {

    }

    public Event(int event_id, int class_id, String event_name, Timestamp start_time, Timestamp end_time, String color, boolean timed) {
        this.event_id = event_id;
        this.class_id = class_id;
        this.event_name = event_name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.color = color;
        this.timed = timed;
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
}
