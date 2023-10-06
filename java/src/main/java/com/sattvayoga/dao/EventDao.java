package com.sattvayoga.dao;

import com.sattvayoga.model.*;

import java.sql.SQLException;
import java.util.List;

public interface EventDao {

    List<ClassEvent> createAndGetEvents(List<ClassDetails> classDetails);

    void createEvent(ClassEvent newClassEvent);

    void updateEventServerTask() throws Exception;

    List<ClassDetails> getAllClasses() throws SQLException;

    TeacherDetails getTeacherDetailsByTeacherId(int TeacherId);

    List<ClientDetailsDTO> getClientDetailsByClassId(int ClassId);

    List<ClassEvent> getAllEvents();

    boolean deleteEvent(int eventId);

    boolean updateEventDetails(ClassEvent classEvent);

    List<ClassEvent> getHundredEvents();

    List<ClassEvent> getHundredEventsForUser(int client_id);

    void registerForEvent(int client_id, int event_id, int package_purchase_id);

//    List<Event> getAllUpcomingClientEvents(int user_id);

    List<ClassEvent> getAllHistoricalClientEvents(int user_id);

    void deleteEventForClient(int event_id, int client_id);

    ClassEvent getEventByEventId(int eventId);

    int getPackagePurchaseIdByEventIdClientId(int eventId, int clientId);

    List<ClientEvent> getRedFlaggedClientEventsByClientId(int clientId);

    void reconcileClassWithPackageId(int packageId, int eventId, int clientId);

    String updateEventsByClass(ClassDetails originalClass, ClassDetails updatedClass);

    String deleteEventsByClass(ClassDetails originalClass);

    boolean isThereExistingEventWithStartTime(ClassEvent newClassEvent);

    void createNewEventsFromClass(ClassDetails classDetails) throws Exception;

    void updateAllClientsByLookingAtEvents();

    SignUpAggregate getSignUpAggregate();
}
