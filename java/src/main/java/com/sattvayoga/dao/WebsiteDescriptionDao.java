package com.sattvayoga.dao;

public interface WebsiteDescriptionDao {

    String getNewsAndEvents();

    String getClassSchedule();

    void updateClassSchedule(String newDescription);

    void updateNewsAndEvents(String newDescription);
}
