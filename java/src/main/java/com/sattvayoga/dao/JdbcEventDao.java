package com.sattvayoga.dao;

import com.sattvayoga.model.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.parameters.P;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class JdbcEventDao implements EventDao {

    private final JdbcTemplate jdbcTemplate;


    public JdbcEventDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void createEvent(Event newEvent) {
        String sql = "INSERT INTO events (class_id, event_name, start_time, " +
                "end_time, color, timed, is_visible_online) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, newEvent.getClass_id(),
                newEvent.getEvent_name(), newEvent.getStart_time(),
                newEvent.getEnd_time(), newEvent.getColor(), newEvent.isTimed(), newEvent.isIs_visible_online());
    }

    @Override
    public void updateEventServerTask() throws Exception {
        List<ClassDetails> classDetails = getAllClasses();
        String end_time = "SELECT * FROM events ORDER BY end_time DESC LIMIT 1";
        SqlRowSet results = jdbcTemplate.queryForRowSet(end_time);
        Event event;
        Date date = new Date();
        Timestamp theLatestTimestamp = new Timestamp(date.getTime());
        Calendar cal1 = new GregorianCalendar();
        Calendar cal2 = new GregorianCalendar();
        LocalDate currentDate = LocalDate.now();


        if (results.next()) {
            event = mapRowToEvent(results);
            theLatestTimestamp = event.getEnd_time();
        }
        try {
            theLatestTimestamp = addDays(1, theLatestTimestamp);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Error incrementing the timestamp");
        }

        LocalDate latestDate = theLatestTimestamp.toLocalDateTime().toLocalDate();
        cal1.set(latestDate.getYear(), latestDate.getMonthValue(), latestDate.getDayOfMonth());

        LocalDate todayNextYear = LocalDate.now().plusWeeks(52).plusDays(1);

        // Figure out what day it is (e.g. Monday, Tuesday, Wednesday)

        DayOfWeek dayOfWeek = todayNextYear.getDayOfWeek();

        todayNextYear = getLocalDate(todayNextYear, dayOfWeek);

        String startTimeStampBuilder = getStartTimeStampBuilder(todayNextYear);


        cal2.set(todayNextYear.getYear(), todayNextYear.getMonthValue(), todayNextYear.getDayOfMonth());

        Timestamp OneYearFromToday = Timestamp.valueOf(startTimeStampBuilder);

//        Integer value 0 if this Timestamp object is equal to given Timestamp object.
//        A value less than 0 if this Timestamp object is before the given argument.
//        A value greater than 0 if this Timestamp object is after the given argument.
        int numberValueFromComparison = theLatestTimestamp.compareTo(OneYearFromToday);

        // < means that we need to make up for more events to fill the gap between (the last event time we have on file VS a year from this current time)

        if (numberValueFromComparison < 0) {
            System.out.println("Lacking Future Events... Creating..");

            // compare the days between (the latest Timestamp in our DB) and (one year from today)
            int days = daysBetween(cal1.getTime(), cal2.getTime());


            for (int i = 0; i < days; i++) {
                // loop through each class
                for (int b = 0; b < classDetails.size(); b++) {
                    ClassDetails currentClass = classDetails.get(b);
                    String[] dateRange = currentClass.getDate_range();

                    // loop through the range of days for each individual class
                    for (int j = 0; j < dateRange.length; j++) {
                        LocalDate startTimeDate = theLatestTimestamp.toInstant().atZone(ZoneId.of("America/New_York")).toLocalDate();
                        DayOfWeek newDayOfWeek = startTimeDate.getDayOfWeek();
                        String newDay = newDayOfWeek.toString();
                        if (dateRange[j].equals("Sun") && newDay.equals("SUNDAY")) {
                            // empty event Object
                            Event newEvent = new Event();
                            // set class ID
                            newEvent.setClass_id(currentClass.getClass_id());
                            // set name
                            newEvent.setEvent_name(currentClass.getClass_description());
                            // set color (default to blue)
                            newEvent.setColor("blue");
                            // set timed (default to true)
                            newEvent.setTimed(true);
                            // set visible to true
                            newEvent.setIs_visible_online(true);

                            LocalDate nextOrSameSun = startTimeDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

                            startTimeStampBuilder = "";
                            String month = String.valueOf(nextOrSameSun.getMonthValue());
                            String day = String.valueOf(nextOrSameSun.getDayOfMonth());
                            String year = String.valueOf(nextOrSameSun.getYear());

                            String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                            startTimeStampBuilder += year + "-" + month + "-" + day + " " + time.substring(0, 5) + ":00.00";

                            Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                            Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                            newEvent.setStart_time(start);
                            newEvent.setEnd_time(end);

                            // call another method that takes in a Event Object

                            createEvent(newEvent);

                        }

                        if (dateRange[j].equals("Mon") && newDay.equals("MONDAY")) {
                            // empty event Object
                            Event newEvent = new Event();
                            // set class ID
                            newEvent.setClass_id(currentClass.getClass_id());
                            // set name
                            newEvent.setEvent_name(currentClass.getClass_description());
                            // set color (default to blue)
                            newEvent.setColor("blue");
                            // set timed (default to true)
                            newEvent.setTimed(true);
                            // set visible to true
                            newEvent.setIs_visible_online(true);

                            LocalDate nextOrSameMon = startTimeDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));

                            startTimeStampBuilder = "";
                            String month = String.valueOf(nextOrSameMon.getMonthValue());
                            String day = String.valueOf(nextOrSameMon.getDayOfMonth());
                            String year = String.valueOf(nextOrSameMon.getYear());

                            String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                            startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                            Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                            Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                            newEvent.setStart_time(start);
                            newEvent.setEnd_time(end);

                            createEvent(newEvent);

                        }

                        if (dateRange[j].equals("Tue") && newDay.equals("TUESDAY")) {
                            // empty event Object
                            Event newEvent = new Event();
                            // set class ID
                            newEvent.setClass_id(currentClass.getClass_id());
                            // set name
                            newEvent.setEvent_name(currentClass.getClass_description());
                            // set color (default to blue)
                            newEvent.setColor("blue");
                            // set timed (default to true)
                            newEvent.setTimed(true);
                            // set visible to true
                            newEvent.setIs_visible_online(true);

                            LocalDate nextOrSameTue = startTimeDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));

                            startTimeStampBuilder = "";
                            String month = String.valueOf(nextOrSameTue.getMonthValue());
                            String day = String.valueOf(nextOrSameTue.getDayOfMonth());
                            String year = String.valueOf(nextOrSameTue.getYear());

                            String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                            startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                            Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                            Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                            newEvent.setStart_time(start);
                            newEvent.setEnd_time(end);

                            createEvent(newEvent);

                        }

                        if (dateRange[j].equals("Wed") && newDay.equals("WEDNESDAY")) {
                            // empty event Object
                            Event newEvent = new Event();
                            // set class ID
                            newEvent.setClass_id(currentClass.getClass_id());
                            // set name
                            newEvent.setEvent_name(currentClass.getClass_description());
                            // set color (default to blue)
                            newEvent.setColor("blue");
                            // set timed (default to true)
                            newEvent.setTimed(true);
                            // set visible to true
                            newEvent.setIs_visible_online(true);

                            LocalDate nextOrSameWed = startTimeDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY));

                            startTimeStampBuilder = "";
                            String month = String.valueOf(nextOrSameWed.getMonthValue());
                            String day = String.valueOf(nextOrSameWed.getDayOfMonth());
                            String year = String.valueOf(nextOrSameWed.getYear());

                            String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                            startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                            Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                            Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                            newEvent.setStart_time(start);
                            newEvent.setEnd_time(end);

                            createEvent(newEvent);
                        }

                        if (dateRange[j].equals("Thu") && newDay.equals("THURSDAY")) {
                            // empty event Object
                            Event newEvent = new Event();
                            // set class ID
                            newEvent.setClass_id(currentClass.getClass_id());
                            // set name
                            newEvent.setEvent_name(currentClass.getClass_description());
                            // set color (default to blue)
                            newEvent.setColor("blue");
                            // set timed (default to true)
                            newEvent.setTimed(true);
                            // set visible to true
                            newEvent.setIs_visible_online(true);

                            LocalDate nextOrSameThu = startTimeDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));

                            startTimeStampBuilder = "";
                            String month = String.valueOf(nextOrSameThu.getMonthValue());
                            String day = String.valueOf(nextOrSameThu.getDayOfMonth());
                            String year = String.valueOf(nextOrSameThu.getYear());

                            String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                            startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                            Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                            Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                            newEvent.setStart_time(start);
                            newEvent.setEnd_time(end);

                            createEvent(newEvent);
                        }

                        if (dateRange[j].equals("Fri") && newDay.equals("FRIDAY")) {
                            // empty event Object
                            Event newEvent = new Event();
                            // set class ID
                            newEvent.setClass_id(currentClass.getClass_id());
                            // set name
                            newEvent.setEvent_name(currentClass.getClass_description());
                            // set color (default to blue)
                            newEvent.setColor("blue");
                            // set timed (default to true)
                            newEvent.setTimed(true);
                            // set visible to true
                            newEvent.setIs_visible_online(true);

                            LocalDate nextOrSameFri = startTimeDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

                            startTimeStampBuilder = "";
                            String month = String.valueOf(nextOrSameFri.getMonthValue());
                            String day = String.valueOf(nextOrSameFri.getDayOfMonth());
                            String year = String.valueOf(nextOrSameFri.getYear());

                            String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                            startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                            Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                            Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                            newEvent.setStart_time(start);
                            newEvent.setEnd_time(end);

                            createEvent(newEvent);
                        }

                        if (dateRange[j].equals("Sat") && newDay.equals("SATURDAY")) {
                            // empty event Object
                            Event newEvent = new Event();
                            // set class ID
                            newEvent.setClass_id(currentClass.getClass_id());
                            // set name
                            newEvent.setEvent_name(currentClass.getClass_description());
                            // set color (default to blue)
                            newEvent.setColor("blue");
                            // set timed (default to true)
                            newEvent.setTimed(true);
                            // set visible to true
                            newEvent.setIs_visible_online(true);

                            LocalDate nextOrSameSat = startTimeDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

                            startTimeStampBuilder = "";
                            String month = String.valueOf(nextOrSameSat.getMonthValue());
                            String day = String.valueOf(nextOrSameSat.getDayOfMonth());
                            String year = String.valueOf(nextOrSameSat.getYear());

                            String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                            startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                            Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                            Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                            newEvent.setStart_time(start);
                            newEvent.setEnd_time(end);

                            createEvent(newEvent);
                        }


                    }

                }
                // add another day to the local date
                try {
                    theLatestTimestamp = addDays(1, theLatestTimestamp);
                } catch (Exception e) {
                    System.out.println("Error incrementing the timestamp");
                }
            }
        }
    }

    @Override
    public boolean deleteEvent(int eventId) {
        String sql = "BEGIN TRANSACTION;\n" +

                "\n" +
                "DELETE FROM client_event \n" +
                "WHERE client_event.event_id = ?;\n" +
                "\n" +
                "DELETE FROM events\n" +
                "WHERE event_id = ?;\n" +
                "\n" +
                "COMMIT TRANSACTION;";
        return jdbcTemplate.update(sql, eventId, eventId) == 1;
    }

    @Override
    public boolean updateEventDetails(Event event) {
        String sql = "UPDATE events SET class_id = ? , " +
                "event_name = ? , " +
                "start_time = ? , " +
                "end_time = ? , " +
                "color = ? , " +
                "timed = ? , " +
                "is_visible_online = ? " +
                "WHERE event_id = ?";
        return jdbcTemplate.update(sql, event.getClass_id(), event.getEvent_name(), event.getStart_time(),
                event.getEnd_time(), event.getColor(), event.isTimed(),
                event.isIs_visible_online(), event.getEvent_id()) == 1;
    }

    @Override
    public void updateEventsByClass(ClassDetails originalClass, ClassDetails updatedClass) {
        // find all Event objects with the same start time, end time, and class_id

        String sql = "SELECT * FROM events WHERE start_time >= now() AND class_id = ? ; ";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, originalClass.getClass_id());

        // A map
        // CREATE YOUR PROPERTIES

        String[] originalDateRangeArray = originalClass.getDate_range();
        String[] updateDateRangeArray = updatedClass.getDate_range();

        //Map<String, String> originalClassDateRangeMap = new HashMap<>(ArrayToMapDateRange(originalDateRangeArray));
        Map<String, String> updatedClassDateRangeMap = new HashMap<>(ArrayToMapDateRange(updateDateRangeArray));


        // A set
        //Set<String> updatedClassDateRangeSet = new HashSet<>(Arrays.asList(updateDateRangeArray));
        Set<String> originalClassDateRangeSet = new HashSet<>(Arrays.asList(originalDateRangeArray));

        while (result.next()) {
            // PULL THE EVENT
            Event event = mapRowToEvent(result);
            event.setAttendanceList(getAttendanceByEventId(event.getEvent_id()));


            // check this current event and compare if it has the same start-time and end-time as the original
            // filtering it down so that we don't accidentally change any events that weren't exact matches
            // CONVERSION ROUTINE TO EXTRACT THE HOUR AND MINUTES am/pm (e.g. "06:00 am")
            Timestamp startTimeStamp = event.getStart_time();
            Timestamp endTimeStamp = event.getEnd_time();


            LocalDateTime startTimeLocalDate = startTimeStamp.toLocalDateTime();
            LocalDateTime endTimeLocalDate = endTimeStamp.toLocalDateTime();

            DayOfWeek currentDayOfWeek = startTimeLocalDate.getDayOfWeek();
            String currentDay = currentDayOfWeek.toString().substring(0, 3).toLowerCase();
            currentDay = currentDay.substring(0,1).toUpperCase() + currentDay.substring(1);

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

            String startTimeString = dateTimeFormatter.format(startTimeLocalDate);
            String originalClassStartTime = originalClass.getStart_time();
            String updatedClassStartTime = updatedClass.getStart_time();

            LocalDateTime nextHourEndTime = startTimeLocalDate;

            int originalClassDuration = originalClass.getClass_duration();
            int updatedClassDuration = updatedClass.getClass_duration();
            // GRAB THE CORRECT "06:30" END TIME  If it's not an hour
            if (originalClassDuration != 60) {
                nextHourEndTime = startTimeLocalDate.plusMinutes(originalClassDuration);
            } else {
                nextHourEndTime = startTimeLocalDate.plusHours(1);
            }

            String actualEndTimeString = dateTimeFormatter.format(endTimeLocalDate);
            String expectedEndTimeString = dateTimeFormatter.format(nextHourEndTime);

            // Check if This event had already been updated thus it isn't an exact match and something to change across the board

            if (startTimeString.equals(originalClassStartTime) && actualEndTimeString.equals(expectedEndTimeString) && event.isIs_visible_online()) {
                //just update it while you're here.

                // Happy Path: The date range never changed

                // check the size first

                int sizeOfUpdatedClassDateRange = updateDateRangeArray.length;
                int sizeOfOriginalClassDateRange = originalDateRangeArray.length;
                boolean sameDateRange = true;
                boolean sameSize = false;
                String largerRange = "None";
                if (sizeOfOriginalClassDateRange == sizeOfUpdatedClassDateRange) {
                    // compare to see if they have the same days
                    sameSize = true;
                    for (int i = 0; i < updateDateRangeArray.length; i++) {
                        boolean foundMatch = false;
                        for (int j = 0; j < originalDateRangeArray.length; j++) {
                            if (updateDateRangeArray[i].equals(originalDateRangeArray[j])) {
                                foundMatch = true;
                            }
                        }
                        if (!foundMatch) {
                            sameDateRange = false;
                        }
                    }
                } else {
                    sameDateRange = false;
                    if (sizeOfOriginalClassDateRange > sizeOfUpdatedClassDateRange) {
                        largerRange = "Original";
                    } else {
                        largerRange = "Updated";
                    }
                }

                // Check if They have the same start time and have the same duration, which means there's nothing to change time-wise

                boolean sameTimes = false;
                if (startTimeString.equals(updatedClassStartTime) && (originalClassDuration == updatedClassDuration)) {
                    sameTimes = true;
                }

                // CREATE YOUR PROPERTIES

                // A date range to keep track
                String[] dayRange = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

                // sort it immediately since you have to.
                int newPosition = 0;
                int originalPosition = 0;

                // sort the dayRange to keep track
                String[] orderedDayRange = new String[dayRange.length];

                boolean madeOnePass = false;
                for (int i = 0; i < dayRange.length; i++) {
                    if (dayRange[i].equals(currentDay)) {
                        orderedDayRange[newPosition] = dayRange[i];
                        originalPosition = i;
                        newPosition++;
                    }
                    if (i > originalPosition) {
                        orderedDayRange[newPosition] = dayRange[i];
                    }
                    // once you get to the end, if the originalPosition was not zero then loop through the remaining positions
                    if ((originalPosition > 0) && (i == dayRange.length - 1) && (newPosition != dayRange.length - 1)) {
                        madeOnePass = true;
                        i = -1;
                    }
                    if (madeOnePass && newPosition != dayRange.length - 1 && i >= 0 && i < originalPosition) {
                        orderedDayRange[newPosition] = dayRange[i];
                    }

                }

                //Iterator<String> it = updatedClassDateRangeSet.iterator();

                //  Go through the values of the Map and Set, just one quick pass,
                //  where you make another "put" into the map for existing matching values
                //  but what if one of them is bigger than the other?

                if (sameSize) {
                    // loop through either map or set
                    for (String day : updatedClassDateRangeMap.keySet()) {
                        if (originalClassDateRangeSet.contains(day)) {
                            updatedClassDateRangeMap.put(day, day);
                        }
                    }
                } else if (largerRange.equals("Original")) {
                    // loop through the original (map)
                    for (String day : originalClassDateRangeSet) {
                        if (updatedClassDateRangeMap.containsKey(day)) {
                            updatedClassDateRangeMap.put(day, day);
                        }
                    }

                } else if (largerRange.equals("Updated")) {
                    // loop through the updated (set)
                    for (String day : updatedClassDateRangeMap.keySet()) {
                        if (originalClassDateRangeSet.contains(day)) {
                            updatedClassDateRangeMap.put(day, day);
                        }
                    }
                }


                if (sameDateRange && !sameTimes) {
                    // FOR THIS CONDITION BLOCK ONLY CHANGE THE HOURS OF THE START AND END TIMESTAMPS
                    // BECAUSE THEY KEPT THE EXACT SAME DATE RANGE

                    // empty event Object
                    Event newEvent = new Event();
                    // set event ID
                    newEvent.setEvent_id(event.getEvent_id());
                    // set class ID
                    newEvent.setClass_id(updatedClass.getClass_id());
                    // set name
                    newEvent.setEvent_name(updatedClass.getClass_description());
                    // set color (default to blue)
                    newEvent.setColor("blue");
                    // set timed (default to true)
                    newEvent.setTimed(true);
                    // set visible to true
                    newEvent.setIs_visible_online(true);

                    // Process to Prepare the new startTime/endTime timestamps for the updated event
                    LocalDate startTimeDate = startTimeStamp.toInstant().atZone(ZoneId.of("America/New_York")).toLocalDate();

                    // Use that assigned Day to update this event for the next assigned day
                    DayOfWeek assignedDayOfWeek = getDayOfWeekByString(currentDay);

                    // finds the same or next date object at the assigned day
                    LocalDate nextOrSameAssignedDay = startTimeDate.with(TemporalAdjusters.nextOrSame(assignedDayOfWeek));

                    String startTimeStampBuilder = "";
                    String month = String.valueOf(nextOrSameAssignedDay.getMonthValue());
                    String day = String.valueOf(nextOrSameAssignedDay.getDayOfMonth());
                    String year = String.valueOf(nextOrSameAssignedDay.getYear());

                    String time = LocalTime.parse(updatedClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                    startTimeStampBuilder += year + "-" + month + "-" + day + " " + time.substring(0, 5) + ":00.00";

                    Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                    Timestamp end = new Timestamp(start.getTime());

                    if (updatedClassDuration != 60) {
                        end = new Timestamp(start.getTime() + TimeUnit.MINUTES.toMillis(updatedClassDuration));
                    } else {
                        end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                    }

                    // startTime and endTime are prepared here
                    newEvent.setStart_time(start);
                    newEvent.setEnd_time(end);

                    // Maybe make sure no event has the same timestamps already so that you don't double book
                    boolean checkForExistingEventWithStartTime = isThereExistingEventWithStartTime(newEvent);

                    if (!checkForExistingEventWithStartTime) {
                        updateEventDetails(newEvent);
                    }


                } else if (!sameDateRange) {


                    // See if the map with the updated Date Range contains the currentDay as a key

                    if (updatedClassDateRangeMap.containsKey(currentDay)) {
                        // Use that value in the map to create the new event

                        // empty event Object
                        Event newEvent = new Event();
                        // set event ID
                        newEvent.setEvent_id(event.getEvent_id());
                        // set class ID
                        newEvent.setClass_id(updatedClass.getClass_id());
                        // set name
                        newEvent.setEvent_name(updatedClass.getClass_description());
                        // set color (default to blue)
                        newEvent.setColor("blue");
                        // set timed (default to true)
                        newEvent.setTimed(true);
                        // set visible to true
                        newEvent.setIs_visible_online(true);

                        // Process to Prepare the new startTime/endTime timestamps for the updated event
                        LocalDate startTimeDate = startTimeStamp.toInstant().atZone(ZoneId.of("America/New_York")).toLocalDate();

                        // Use that assigned Day to update this event for the next assigned day
                        DayOfWeek assignedDayOfWeek = getDayOfWeekByString(currentDay);

                        // finds the same or next date object at the assigned day
                        LocalDate nextOrSameAssignedDay = startTimeDate.with(TemporalAdjusters.nextOrSame(assignedDayOfWeek));

                        String startTimeStampBuilder = "";
                        String month = String.valueOf(nextOrSameAssignedDay.getMonthValue());
                        String day = String.valueOf(nextOrSameAssignedDay.getDayOfMonth());
                        String year = String.valueOf(nextOrSameAssignedDay.getYear());

                        String time = LocalTime.parse(updatedClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                        startTimeStampBuilder += year + "-" + month + "-" + day + " " + time.substring(0, 5) + ":00.00";

                        Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                        Timestamp end = new Timestamp(start.getTime());

                        if (updatedClassDuration != 60) {
                            end = new Timestamp(start.getTime() + TimeUnit.MINUTES.toMillis(updatedClassDuration));
                        } else {
                            end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                        }

                        // startTime and endTime are prepared here
                        newEvent.setStart_time(start);
                        newEvent.setEnd_time(end);

                        // Maybe make sure no event has the same timestamps already so that you don't double book
                        boolean checkForExistingEventWithStartTime = isThereExistingEventWithStartTime(newEvent);

                        if (!checkForExistingEventWithStartTime) {
                            updateEventDetails(newEvent);
                        }


                    } else {
                        // Loop through and see if the map has any slots open where the value is an empty stirng
                        boolean foundEmptySlot = false;
                        String assignedDay = "";
                        for (String day : updatedClassDateRangeMap.keySet()) {
                            String currentValue = updatedClassDateRangeMap.get(day);
                            if (currentValue.equals("")) {
                                foundEmptySlot = true;
                                assignedDay = day;
                                updatedClassDateRangeMap.put(day, currentDay);
                            }
                        }

                        // Delete it only if we cant switch it to another day and there's no attendance
                        // Find out if this event has an attendance
                        if (!foundEmptySlot && event.getAttendanceList().size() == 0) {
                            deleteEvent(event.getEvent_id());
                        } else if (foundEmptySlot && !(assignedDay.equals("") && updatedClassDateRangeMap.get(assignedDay).equals(currentDay))) {


                            // empty event Object
                            Event newEvent = new Event();
                            // set event ID
                            newEvent.setEvent_id(event.getEvent_id());
                            // set class ID
                            newEvent.setClass_id(updatedClass.getClass_id());
                            // set name
                            newEvent.setEvent_name(updatedClass.getClass_description());
                            // set color (default to blue)
                            newEvent.setColor("blue");
                            // set timed (default to true)
                            newEvent.setTimed(true);
                            // set visible to true
                            newEvent.setIs_visible_online(true);


                            // Process to Prepare the new startTime/endTime timestamps for the updated event
                            LocalDate startTimeDate = startTimeStamp.toInstant().atZone(ZoneId.of("America/New_York")).toLocalDate();

                            // Use that assigned Day to update this event for the next assigned day
                            DayOfWeek assignedDayOfWeek = getDayOfWeekByString(assignedDay);

                            // finds the next date object at the assigned day
                            LocalDate nextOrSameAssignedDay = startTimeDate.with(TemporalAdjusters.nextOrSame(assignedDayOfWeek));

                            String startTimeStampBuilder = "";
                            String month = String.valueOf(nextOrSameAssignedDay.getMonthValue());
                            String day = String.valueOf(nextOrSameAssignedDay.getDayOfMonth());
                            String year = String.valueOf(nextOrSameAssignedDay.getYear());

                            String time = LocalTime.parse(updatedClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                            startTimeStampBuilder += year + "-" + month + "-" + day + " " + time.substring(0, 5) + ":00.00";

                            Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                            Timestamp end = new Timestamp(start.getTime());

                            if (updatedClassDuration != 60) {
                                end = new Timestamp(start.getTime() + TimeUnit.MINUTES.toMillis(updatedClassDuration));
                            } else {
                                end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                            }

                            // startTime and endTime are prepared here
                            newEvent.setStart_time(start);
                            newEvent.setEnd_time(end);

                            // Maybe make sure no event has the same timestamps already so that you don't double book
                            boolean checkForExistingEventWithStartTime = isThereExistingEventWithStartTime(newEvent);

                            if (!checkForExistingEventWithStartTime) {
                                updateEventDetails(newEvent);
                            }

                        }

                    }

               }
                else if (sameDateRange && sameTimes && sameSize) {
                    // Remember to Update the rest of the event since the Timestamps match

                    // empty event Object
                    Event newEvent = new Event();
                    // set event ID
                    newEvent.setEvent_id(event.getEvent_id());
                    // set class ID
                    newEvent.setClass_id(updatedClass.getClass_id());
                    // set name
                    newEvent.setEvent_name(updatedClass.getClass_description());
                    // set color (default to blue)
                    newEvent.setColor("blue");
                    // set timed (default to true)
                    newEvent.setTimed(true);
                    // set visible to true
                    newEvent.setIs_visible_online(true);

                    // startTime and endTime are prepared here
                    newEvent.setStart_time(event.getStart_time());
                    newEvent.setEnd_time(event.getEnd_time());

                    // Maybe make sure no event has the same timestamps already so that you don't double book
                    boolean checkForExistingEventWithStartTime = isThereExistingEventWithStartTime(newEvent);

                    if (!checkForExistingEventWithStartTime) {
                        updateEventDetails(newEvent);
                    }
                }

            }


        }
        // Loop through the remaining empty string values and make events for that specific key


        for (String day : updatedClassDateRangeMap.keySet()) {
            String currentValue = updatedClassDateRangeMap.get(day);
            if (currentValue.equals("")) {
                updatedClassDateRangeMap.put(day,day);

                Date date = new Date();
                Timestamp theLatestTimestamp = new Timestamp(date.getTime());
                Calendar cal1 = new GregorianCalendar();
                Calendar cal2 = new GregorianCalendar();
                LocalDate currentDate = LocalDate.now();


                LocalDate latestDate = theLatestTimestamp.toLocalDateTime().toLocalDate();
                cal1.set(latestDate.getYear(), latestDate.getMonthValue(), latestDate.getDayOfMonth());

                LocalDate todayNextYear = LocalDate.now().plusWeeks(52).plusDays(1);

                // Figure out what day it is (e.g. Monday, Tuesday, Wednesday)

                DayOfWeek dayOfWeek = todayNextYear.getDayOfWeek();

                todayNextYear = getLocalDate(todayNextYear, dayOfWeek);

                String startTimeStampBuilder = getStartTimeStampBuilder(todayNextYear);

                cal2.set(todayNextYear.getYear(), todayNextYear.getMonthValue(), todayNextYear.getDayOfMonth());

                Timestamp OneYearFromToday = Timestamp.valueOf(startTimeStampBuilder);

//        Integer value 0 if this Timestamp object is equal to given Timestamp object.
//        A value less than 0 if this Timestamp object is before the given argument.
//        A value greater than 0 if this Timestamp object is after the given argument.
                int numberValueFromComparison = theLatestTimestamp.compareTo(OneYearFromToday);

                if (numberValueFromComparison < 0) {
                    int days = daysBetween(cal1.getTime(), cal2.getTime());

                    for (int i = 0; i < days; i++) {

                        LocalDate startTimeDate = theLatestTimestamp.toInstant().atZone(ZoneId.of("America/New_York")).toLocalDate();
                        DayOfWeek newDayOfWeek = startTimeDate.getDayOfWeek();
                        String newDay = newDayOfWeek.toString().substring(0, 3).toLowerCase();
                        newDay = newDay.substring(0,1).toUpperCase() + newDay.substring(1);

                        if (newDay.equals(day)) {

                            // empty event Object
                            Event newEvent = new Event();
                            // set class ID
                            newEvent.setClass_id(updatedClass.getClass_id());
                            // set name
                            newEvent.setEvent_name(updatedClass.getClass_description());
                            // set color (default to blue)
                            newEvent.setColor("blue");
                            // set timed (default to true)
                            newEvent.setTimed(true);
                            // set visible to true
                            newEvent.setIs_visible_online(true);

                            LocalDate nextOrSameDay = startTimeDate.with(TemporalAdjusters.nextOrSame(getDayOfWeekByString(day)));

                            startTimeStampBuilder = "";
                            String month = String.valueOf(nextOrSameDay.getMonthValue());
                            String day2 = String.valueOf(nextOrSameDay.getDayOfMonth());
                            String year = String.valueOf(nextOrSameDay.getYear());

                            String time = LocalTime.parse(updatedClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                            startTimeStampBuilder += year + "-" + month + "-" + day2 + " " + time.substring(0, 5) + ":00.00";

                            Timestamp start = Timestamp.valueOf(startTimeStampBuilder);

                            Timestamp end = new Timestamp(start.getTime());

                            if (updatedClass.getClass_duration() != 60) {
                                end = new Timestamp(start.getTime() + TimeUnit.MINUTES.toMillis(updatedClass.getClass_duration()));
                            } else {
                                end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                            }

                            newEvent.setStart_time(start);
                            newEvent.setEnd_time(end);

                            boolean alreadyExists = isThereExistingEventWithStartTime(newEvent);
                            if (!alreadyExists) {
                                createEvent(newEvent);
                            }

                        }
                        // add another day to the local date
                        try {
                            theLatestTimestamp = addDays(1, theLatestTimestamp);
                        } catch (Exception e) {
                            System.out.println("Error incrementing the timestamp");
                        }

                    }





                }

            }
        }

    }

    // helper method
    private boolean isThereExistingEventWithStartTime(Event newEvent) {
        String sql = "SELECT * FROM events WHERE start_time = ? AND end_time = ?;";
        List<Event> checkForExistingEventList = new ArrayList<>();
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql,newEvent.getStart_time(), newEvent.getEnd_time());
        while (result.next()) {
            Event event = mapRowToEvent(result);

            checkForExistingEventList.add(event);
        }
        return checkForExistingEventList.size() > 0;
    }

    // helper method
    private DayOfWeek getDayOfWeekByString(String assignedDay) {
        if (assignedDay.equals("Sun")) {
            return DayOfWeek.SUNDAY;
        }
        else if (assignedDay.equals("Mon")) {
            return DayOfWeek.MONDAY;
        }
        else if (assignedDay.equals("Tue")) {
            return DayOfWeek.TUESDAY;
        }
        else if (assignedDay.equals("Wed")) {
            return DayOfWeek.WEDNESDAY;
        }
        else if (assignedDay.equals("Thu")) {
            return DayOfWeek.THURSDAY;
        }
        else if (assignedDay.equals("Fri")) {
            return DayOfWeek.FRIDAY;
        }
        else if (assignedDay.equals("Sat")) {
            return DayOfWeek.SATURDAY;
        }
        return null;
    }

    // helper method
    private Map<String, String> ArrayToMapDateRange(String[] originalDateRangeArray) {
        Map<String, String> originalClassDateRange = new HashMap<>();
        for (int i = 0; i < originalDateRangeArray.length; i++) {
            String day = originalDateRangeArray[i];
            originalClassDateRange.put(day, "");
        }
        return originalClassDateRange;
    }

    @Override
    public List<Event> getAllEvents() {
        List<Event> allEvents = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM events; ";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while (result.next()) {
            Event event = mapRowToEvent(result);

            allEvents.add(event);
        }
        return allEvents;
    }

    @Override
    public List<Event> getHundredEvents() {
        List<Event> allEvents = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE is_visible_online = true AND start_time >= now() ORDER BY start_time LIMIT 200  ; ";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while (result.next()) {
            Event event = mapRowToEvent(result);
            allEvents.add(event);
        }
        return allEvents;
    }

    @Override
    public List<Event> getHundredEventsForUser(int client_id) {
        List<Event> allEvents = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE NOT EXISTS (SELECT event_id FROM client_event WHERE client_event.event_id = events.event_id AND client_event.client_id = ?) AND is_visible_online = true AND start_time >= now() ORDER BY start_time LIMIT 200 ; ";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, client_id);
        while (result.next()) {
            Event event = mapRowToEvent(result);
            allEvents.add(event);
        }
        return allEvents;
    }

    @Override
    public Event getEventByEventId(int eventId) {
        Event event = null;

        // Pull a list of clients from the client_event table for anyone who signed up.
        String sql = "SELECT * FROM events WHERE event_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, eventId);
        if (result.next()) {
            event = mapRowToEvent(result);
            event.setAttendanceList(getAttendanceByEventId(eventId));
        }

        return event;
    }

    @Override
    public int getPackagePurchaseIdByEventIdClientId(int eventId, int clientId) {
        String sql = "SELECT package_purchase_id FROM client_event WHERE event_id = ? AND client_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, eventId, clientId);
        int packageId = 0;
        if (result.next()) {
            packageId = result.getInt("package_purchase_id");
        }
        return packageId;
    }


    public List<ClientDetails> getAttendanceByEventId(int eventId) {
        List<ClientDetails> listOfAttendance = new ArrayList<>();

        String sql = "SELECT * FROM client_details JOIN client_event ON " +
                "client_event.client_id = client_details.client_id " +
                "WHERE client_event.event_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, eventId);

        while (result.next()) {
            ClientDetails clientDetails = mapRowToClient(result);
            // before you add it to the list, include whether they are red-flagged or not
            clientDetails.setRedFlag(getRedFlaggedClientEventsByClientId(clientDetails.getClient_id()).size() > 0);
            listOfAttendance.add(clientDetails);
        }
        return listOfAttendance;
    }

    @Override
    public List<ClientEvent> getRedFlaggedClientEventsByClientId(int clientId) {
        List<ClientEvent> clientEventObjectList = new ArrayList<>();
        String sql = "SELECT * FROM client_event WHERE client_id = ? AND package_purchase_id = 0";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, clientId);
        while (result.next()) {
            ClientEvent clientEvent = mapRowToClientEvent(result);
            clientEventObjectList.add(clientEvent);
        }
        return clientEventObjectList;
    }


    @Override
    public void registerForEvent(int client_id, int event_id, int package_purchase_id) {
        String sql = "INSERT INTO client_event (client_id, event_id, package_purchase_id) VALUES (?,?,?);";
        jdbcTemplate.update(sql, client_id, event_id, package_purchase_id);

        String sql2 = "UPDATE client_details SET is_new_client = FALSE WHERE client_id = ?";

        jdbcTemplate.update(sql2, client_id);
    }

    @Override
    public void reconcileClassWithPackageId(int packageId, int eventId, int clientId) {
        String sql = "UPDATE client_event SET package_purchase_id = ? WHERE event_id = ? AND client_id = ?";
        jdbcTemplate.update(sql, packageId, eventId, clientId);
    }

//    @Override
//    public List<Event> getAllUpcomingClientEvents(int user_id) {
//        List<Event> allClientEvents = new ArrayList<>();
//        String sql = "SELECT events.event_id, class_id, event_name, start_time, end_time, color, timed, is_visible_online FROM events \n" +
//                "JOIN client_event ON events.event_id = client_event.event_id \n" +
//                "JOIN client_details ON client_details.client_id = client_event.client_id \n" +
//                "WHERE user_id = ? AND start_time > now() " +
//                "ORDER BY events.start_time";
//        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, user_id);
//        while (result.next()) {
//            allClientEvents.add(mapRowToEvent(result));
//        }
//        return allClientEvents;
//    }

    @Override
    public List<Event> getAllHistoricalClientEvents(int user_id) {
        List<Event> allClientEvents = new ArrayList<>();
        Event event = new Event();
        String sql = "SELECT events.event_id, class_id, event_name, start_time, end_time, color, timed, is_visible_online, package_purchase_id FROM events \n" +
                "JOIN client_event ON events.event_id = client_event.event_id \n" +
                "JOIN client_details ON client_details.client_id = client_event.client_id \n" +
                "WHERE user_id = ? " +
                "ORDER BY events.start_time";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, user_id);
        while (result.next()) {
            event = mapRowToEvent(result);
            event.setPackage_purchase_id(result.getInt("package_purchase_id"));
            allClientEvents.add(event);
        }
        return allClientEvents;
    }

    @Override
    public void deleteEventForClient(int event_id, int client_id) {
        String sql = "DELETE FROM client_event where event_id = ? and client_id = ?";
        jdbcTemplate.update(sql, event_id, client_id);
    }


    @Override
    public List<Event> createAndGetEvents(List<ClassDetails> classDetails) {
        List<Event> eventList = new ArrayList<>();

        // loop through each class
        for (int i = 0; i < classDetails.size(); i++) {
            ClassDetails currentClass = classDetails.get(i);
            String[] dateRange = currentClass.getDate_range();

            // loop through the range of days for each individual class
            for (int j = 0; j < dateRange.length; j++) {

                if (dateRange[j].equals("Sun")) {
                    // loop for a year here, create events a year in advance.
                    for (int k = 0; k < 52; k++) {
                        // empty event Object
                        Event event = new Event();
                        // set class ID
                        event.setClass_id(currentClass.getClass_id());
                        // set name
                        event.setEvent_name(currentClass.getClass_description());
                        // set color (default to blue)
                        event.setColor("blue");
                        // set timed (default to true)
                        event.setTimed(true);

                        LocalDate currentDate = LocalDate.now();
                        LocalDate nextOrSameSun = currentDate.plusWeeks(k).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

                        String startTimeStampBuilder = "";
                        String month = String.valueOf(nextOrSameSun.getMonthValue());
                        String day = String.valueOf(nextOrSameSun.getDayOfMonth());
                        String year = String.valueOf(nextOrSameSun.getYear());

                        String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                        startTimeStampBuilder += year + "-" + month + "-" + day + " " + time.substring(0, 5) + ":00.00";

                        Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                        Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                        event.setStart_time(start);
                        event.setEnd_time(end);
                        eventList.add(event);
                    }
                }

                if (dateRange[j].equals("Mon")) {
                    for (int k = 0; k < 52; k++) {
                        // empty event Object
                        Event event = new Event();
                        // set class ID
                        event.setClass_id(currentClass.getClass_id());
                        // set name
                        event.setEvent_name(currentClass.getClass_description());
                        // set color (default to blue)
                        event.setColor("blue");
                        // set timed (default to true)
                        event.setTimed(true);

                        LocalDate currentDate = LocalDate.now();
                        LocalDate nextOrSameMon = currentDate.plusWeeks(k).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));

                        String startTimeStampBuilder = "";
                        String month = String.valueOf(nextOrSameMon.getMonthValue());
                        String day = String.valueOf(nextOrSameMon.getDayOfMonth());
                        String year = String.valueOf(nextOrSameMon.getYear());

                        String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                        startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                        Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                        Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                        event.setStart_time(start);
                        event.setEnd_time(end);
                        eventList.add(event);
                    }
                }

                if (dateRange[j].equals("Tue")) {
                    for (int k = 0; k < 52; k++) {
                        // empty event Object
                        Event event = new Event();
                        // set class ID
                        event.setClass_id(currentClass.getClass_id());
                        // set name
                        event.setEvent_name(currentClass.getClass_description());
                        // set color (default to blue)
                        event.setColor("blue");
                        // set timed (default to true)
                        event.setTimed(true);

                        LocalDate currentDate = LocalDate.now();
                        LocalDate nextOrSameTue = currentDate.plusWeeks(k).with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));

                        String startTimeStampBuilder = "";
                        String month = String.valueOf(nextOrSameTue.getMonthValue());
                        String day = String.valueOf(nextOrSameTue.getDayOfMonth());
                        String year = String.valueOf(nextOrSameTue.getYear());

                        String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                        startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                        Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                        Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                        event.setStart_time(start);
                        event.setEnd_time(end);
                        eventList.add(event);
                    }
                }

                if (dateRange[j].equals("Wed")) {
                    for (int k = 0; k < 52; k++) {
                        // empty event Object
                        Event event = new Event();
                        // set class ID
                        event.setClass_id(currentClass.getClass_id());
                        // set name
                        event.setEvent_name(currentClass.getClass_description());
                        // set color (default to blue)
                        event.setColor("blue");
                        // set timed (default to true)
                        event.setTimed(true);

                        LocalDate currentDate = LocalDate.now();
                        LocalDate nextOrSameWed = currentDate.plusWeeks(k).with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY));

                        String startTimeStampBuilder = "";
                        String month = String.valueOf(nextOrSameWed.getMonthValue());
                        String day = String.valueOf(nextOrSameWed.getDayOfMonth());
                        String year = String.valueOf(nextOrSameWed.getYear());

                        String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                        startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                        Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                        Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                        event.setStart_time(start);
                        event.setEnd_time(end);
                        eventList.add(event);
                    }
                }

                if (dateRange[j].equals("Thu")) {
                    for (int k = 0; k < 52; k++) {
                        // empty event Object
                        Event event = new Event();
                        // set class ID
                        event.setClass_id(currentClass.getClass_id());
                        // set name
                        event.setEvent_name(currentClass.getClass_description());
                        // set color (default to blue)
                        event.setColor("blue");
                        // set timed (default to true)
                        event.setTimed(true);

                        LocalDate currentDate = LocalDate.now();
                        LocalDate nextOrSameThu = currentDate.plusWeeks(k).with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));

                        String startTimeStampBuilder = "";
                        String month = String.valueOf(nextOrSameThu.getMonthValue());
                        String day = String.valueOf(nextOrSameThu.getDayOfMonth());
                        String year = String.valueOf(nextOrSameThu.getYear());

                        String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                        startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                        Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                        Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                        event.setStart_time(start);
                        event.setEnd_time(end);
                        eventList.add(event);
                    }
                }

                if (dateRange[j].equals("Fri")) {
                    for (int k = 0; k < 52; k++) {
                        // empty event Object
                        Event event = new Event();
                        // set class ID
                        event.setClass_id(currentClass.getClass_id());
                        // set name
                        event.setEvent_name(currentClass.getClass_description());
                        // set color (default to blue)
                        event.setColor("blue");
                        // set timed (default to true)
                        event.setTimed(true);

                        LocalDate currentDate = LocalDate.now();
                        LocalDate nextOrSameFri = currentDate.plusWeeks(k).with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

                        String startTimeStampBuilder = "";
                        String month = String.valueOf(nextOrSameFri.getMonthValue());
                        String day = String.valueOf(nextOrSameFri.getDayOfMonth());
                        String year = String.valueOf(nextOrSameFri.getYear());

                        String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                        startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                        Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                        Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                        event.setStart_time(start);
                        event.setEnd_time(end);
                        eventList.add(event);
                    }
                }

                if (dateRange[j].equals("Sat")) {
                    for (int k = 0; k < 52; k++) {
                        // empty event Object
                        Event event = new Event();
                        // set class ID
                        event.setClass_id(currentClass.getClass_id());
                        // set name
                        event.setEvent_name(currentClass.getClass_description());
                        // set color (default to blue)
                        event.setColor("blue");
                        // set timed (default to true)
                        event.setTimed(true);

                        LocalDate currentDate = LocalDate.now();
                        LocalDate nextOrSameSat = currentDate.plusWeeks(k).with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

                        String startTimeStampBuilder = "";
                        String month = String.valueOf(nextOrSameSat.getMonthValue());
                        String day = String.valueOf(nextOrSameSat.getDayOfMonth());
                        String year = String.valueOf(nextOrSameSat.getYear());

                        String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).format(DateTimeFormatter.ofPattern("HH:mm"));
                        startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                        Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                        Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                        event.setStart_time(start);
                        event.setEnd_time(end);
                        eventList.add(event);
                    }
                }
            }


        }
        return eventList;
    }


    private Event mapRowToEvent(SqlRowSet rs) {
        Event event = new Event();
        event.setEvent_id(rs.getInt("event_id"));
        if (rs.getInt("class_id") > 0) {
            event.setClass_id(rs.getInt("class_id"));
        }
        event.setEvent_name(rs.getString("event_name"));
        event.setStart_time(rs.getTimestamp("start_time"));
        event.setEnd_time(rs.getTimestamp("end_time"));
        event.setColor(rs.getString("color"));
        event.setTimed(rs.getBoolean("timed"));

        event.setIs_visible_online(rs.getBoolean("is_visible_online"));

        return event;
    }

    private ClientDetails mapRowToClient(SqlRowSet rs) {
        ClientDetails clientDetails = new ClientDetails();
        clientDetails.setClient_id(rs.getInt("client_id"));
        clientDetails.setLast_name(rs.getString("last_name"));
        clientDetails.setFirst_name(rs.getString("first_name"));
        clientDetails.setIs_client_active(rs.getBoolean("is_client_active"));
        clientDetails.setIs_new_client(rs.getBoolean("is_new_client"));
        clientDetails.setStreet_address(rs.getString("street_address"));
        clientDetails.setCity(rs.getString("city"));
        clientDetails.setState_abbreviation(rs.getString("state_abbreviation"));
        clientDetails.setZip_code(rs.getString("zip_code"));
        clientDetails.setPhone_number(rs.getString("phone_number"));
        clientDetails.setIs_on_email_list(rs.getBoolean("is_on_email_list"));
        clientDetails.setEmail(rs.getString("email"));
        clientDetails.setHas_record_of_liability(rs.getBoolean("has_record_of_liability"));
        clientDetails.setDate_of_entry(rs.getTimestamp("date_of_entry"));
        clientDetails.setUser_id(rs.getInt("user_id"));
        return clientDetails;
    }

    private ClientEvent mapRowToClientEvent(SqlRowSet rs) {
        ClientEvent clientEvent = new ClientEvent();
        clientEvent.setEvent_id(rs.getInt("event_id"));
        clientEvent.setClient_id(rs.getInt("client_id"));
        clientEvent.setPackage_purchase_id(rs.getInt("package_purchase_id"));
        return clientEvent;
    }

    public int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    private String getStartTimeStampBuilder(LocalDate nextYearDayWeek) {
        String startTimeStampBuilder = "";
        String month = String.valueOf(nextYearDayWeek.getMonthValue());
        String day = String.valueOf(nextYearDayWeek.getDayOfMonth());
        String year = String.valueOf(nextYearDayWeek.getYear());

        String buildTime = new SimpleDateFormat("HH:mm:ss")
                .format(new Date());
        startTimeStampBuilder += year + "-" + month + "-" + day + " " + buildTime.substring(0, 5) + ":00.00";
        return startTimeStampBuilder;
    }

    private LocalDate getLocalDate(LocalDate nextYearDayWeek, DayOfWeek dayOfWeek) {
        if (dayOfWeek.equals("SUNDAY")) {// One year from today
            nextYearDayWeek = nextYearDayWeek.plusWeeks(52).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        }
        if (dayOfWeek.equals("MONDAY")) {// One year from today
            nextYearDayWeek = nextYearDayWeek.plusWeeks(52).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
        }
        if (dayOfWeek.equals("TUESDAY")) {// One year from today
            nextYearDayWeek = nextYearDayWeek.plusWeeks(52).with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
        }
        if (dayOfWeek.equals("WEDNESDAY")) {// One year from today
            nextYearDayWeek = nextYearDayWeek.plusWeeks(52).with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY));
        }
        if (dayOfWeek.equals("THURSDAY")) {// One year from today
            nextYearDayWeek = nextYearDayWeek.plusWeeks(52).with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));
        }
        if (dayOfWeek.equals("FRIDAY")) {// One year from today
            nextYearDayWeek = nextYearDayWeek.plusWeeks(52).with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        }
        if (dayOfWeek.equals("SATURDAY")) {// One year from today
            nextYearDayWeek = nextYearDayWeek.plusWeeks(52).with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        }
        return nextYearDayWeek;
    }

    private Long dayToMiliseconds(int days) {
        Long result = Long.valueOf(days * 24 * 60 * 60 * 1000);
        return result;
    }

    public Timestamp addDays(int days, Timestamp t1) throws Exception {
        if (days < 0) {
            throw new Exception("Day in wrong format.");
        }
        Long miliseconds = dayToMiliseconds(days);
        return new Timestamp(t1.getTime() + miliseconds);
    }


    // TODO: Modify the follow so you don't need this in here at some point
    @Override
    public List<ClassDetails> getAllClasses() throws SQLException {
        List<ClassDetails> allClasses = new ArrayList<>();
        String sql = "SELECT class_id, teacher_id, class_duration, is_paid, class_description, " +
                "is_repeating, date_range, start_time " +
                "FROM class_details; ";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while (result.next()) {
            ClassDetails classDetails = mapRowToClass(result);

            // set teacher name for class calling helper method
            TeacherDetails teacherDetails = getTeacherDetailsByTeacherId(classDetails.getTeacher_id());
            classDetails.setTeacher_name(teacherDetails.getFirst_name() + " " + teacherDetails.getLast_name());

            // set a list of clients for each class calling helper method
            classDetails.setClient_list(getClientDetailsByClassId(classDetails.getClass_id()));

            allClasses.add(classDetails);
        }
        return allClasses;
    }

    public TeacherDetails getTeacherDetailsByTeacherId(int TeacherId) {
        TeacherDetails teacherDetails = null;
        String sql = "SELECT teacher_id, last_name, first_name, is_teacher_active FROM teacher_details WHERE teacher_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, TeacherId);
        if (result.next()) {
            teacherDetails = mapRowToTeacher(result);
        }
        return teacherDetails;
    }

    @Override
    public List<ClientDetailsDTO> getClientDetailsByClassId(int ClassId) {
        List<ClientDetailsDTO> client_list = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM client_details " +
                "JOIN client_class ON client_details.client_id = client_class.client_id " +
                "WHERE class_id = ?";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, ClassId);
        while (result.next()) {
            ClientDetailsDTO clientDetails = new ClientDetailsDTO(result.getInt("client_id"), result.getString("first_name"), result.getString("last_name"));

            client_list.add(clientDetails);
        }
        return client_list;
    }

    private TeacherDetails mapRowToTeacher(SqlRowSet rs) {
        TeacherDetails teacherDetails = new TeacherDetails();
        teacherDetails.setTeacher_id(rs.getInt("teacher_id"));
        teacherDetails.setFirst_name(rs.getString("first_name"));
        teacherDetails.setLast_name(rs.getString("last_name"));
        teacherDetails.setIs_teacher_active(rs.getBoolean("is_teacher_active"));
        return teacherDetails;
    }


    private ClassDetails mapRowToClass(SqlRowSet rs) throws SQLException {
        ClassDetails classDetails = new ClassDetails();
        classDetails.setClass_id(rs.getInt("class_id"));
        classDetails.setTeacher_id(rs.getInt("teacher_id"));
        classDetails.setClass_duration(rs.getInt("class_duration"));
        classDetails.setIs_paid(rs.getBoolean("is_paid"));
        classDetails.setClass_description(rs.getString("class_description"));
        classDetails.setIs_repeating(rs.getBoolean("is_repeating"));
        classDetails.setStart_time(rs.getString("start_time"));

        Object newObject = rs.getObject("date_range");

        if (newObject instanceof Array) {
            Array tempArray = (Array) newObject;
            Object[] tempObjectArray = (Object[]) tempArray.getArray();
            String[] dateRange = new String[tempObjectArray.length];
            for (int i = 0; i < tempObjectArray.length; i++) {
                dateRange[i] = tempObjectArray[i].toString();
            }
            classDetails.setDate_range(dateRange);
        }

        return classDetails;
    }

}
