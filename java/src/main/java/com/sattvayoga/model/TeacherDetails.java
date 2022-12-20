package com.sattvayoga.model;

public class TeacherDetails {
    private int teacher_id;
    private String last_name;
    private String first_name;
    private boolean is_teacher_active;
    private int user_id;

    public TeacherDetails(int teacher_id, String last_name, String first_name, boolean is_teacher_active, int user_id) {
        this.teacher_id = teacher_id;
        this.last_name = last_name;
        this.first_name = first_name;
        this.is_teacher_active = is_teacher_active;
        this.user_id = user_id;
    }

    public int getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
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

    public boolean isIs_teacher_active() {
        return is_teacher_active;
    }

    public void setIs_teacher_active(boolean is_teacher_active) {
        this.is_teacher_active = is_teacher_active;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
