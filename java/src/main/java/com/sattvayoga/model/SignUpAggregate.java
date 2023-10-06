package com.sattvayoga.model;

public class SignUpAggregate {

    private int dailySignUp;
    private int weeklySignUp;
    private int monthlySignUp;

    public SignUpAggregate(int dailySignUp, int weeklySignUp, int monthlySignUp) {
        this.dailySignUp = dailySignUp;
        this.weeklySignUp = weeklySignUp;
        this.monthlySignUp = monthlySignUp;
    }

    public SignUpAggregate() {
    }

    public int getDailySignUp() {
        return dailySignUp;
    }

    public void setDailySignUp(int dailySignUp) {
        this.dailySignUp = dailySignUp;
    }

    public int getWeeklySignUp() {
        return weeklySignUp;
    }

    public void setWeeklySignUp(int weeklySignUp) {
        this.weeklySignUp = weeklySignUp;
    }

    public int getMonthlySignUp() {
        return monthlySignUp;
    }

    public void setMonthlySignUp(int monthlySignUp) {
        this.monthlySignUp = monthlySignUp;
    }
}
