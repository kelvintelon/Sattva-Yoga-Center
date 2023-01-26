package com.sattvayoga.dao;

import com.sattvayoga.model.ClassDetails;
import com.sattvayoga.model.ClientDetailsDTO;
import com.sattvayoga.model.Event;
import com.sattvayoga.model.TeacherDetails;

import java.sql.SQLException;
import java.util.List;

public interface EventDao {

    List<Event> createAndGetEvents(List<ClassDetails> classDetails);

    void createEvent();

    void updateEventServerTask() throws Exception;

    List<ClassDetails> getAllClasses() throws SQLException;

    TeacherDetails getTeacherDetailsByTeacherId(int TeacherId);

    List<ClientDetailsDTO> getClientDetailsByClassId(int ClassId);

    List<Event> getAllEvents();
}
