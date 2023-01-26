package com.sattvayoga.dao;

import com.sattvayoga.model.ClassDetails;
import com.sattvayoga.model.ClientDetailsDTO;
import com.sattvayoga.model.Event;
import com.sattvayoga.model.TeacherDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
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
                "end_time, color, timed) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, newEvent.getClass_id(),
                newEvent.getEvent_name(), newEvent.getStart_time(),
                newEvent.getEnd_time(), newEvent.getColor(), newEvent.isTimed());
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
        cal1.set(currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth());

        if (results.next()) {
            event = mapRowToEvent(results);
            theLatestTimestamp = event.getEnd_time();
        }
        try {
            theLatestTimestamp = addDays(1, theLatestTimestamp);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Error incrementing the timestamp");
        }

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
            System.out.println("It worked");

            int days = daysBetween(cal1.getTime(), cal2.getTime());

            // TODO: Loop however many of those days

            for (int i = 0; i < days; i++) {
                // loop through each class
                for (int b = 0; b < classDetails.size(); b++) {
                    ClassDetails currentClass = classDetails.get(b);
                    String[] dateRange = currentClass.getDate_range();

                    // loop through the range of days for each individual class
                    for (int j = 0; j < dateRange.length; j++) {
                        LocalDate startTimeDate = theLatestTimestamp.toInstant().atZone(ZoneId.of("America/New_York" )).toLocalDate();
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
                } catch (EmptyResultDataAccessException e) {
                    System.out.println("Error incrementing the timestamp");
                }
            }
        }


    }

    @Override
    public boolean deleteEvent(int eventId) {
        String sql = "BEGIN TRANSACTION;\n" +
                // TODO: When events_client table is made uncomment this and add another event_id to the update
//                "\n" +
//                "DELETE FROM events_client \n" +
//                "WHERE events_client.event_id = ?;\n" +
//                "\n" +
                "DELETE FROM events\n" +
                "WHERE event_id = ?;\n" +
                "\n" +
                "COMMIT TRANSACTION;";
        return jdbcTemplate.update(sql, eventId)==1;
    }

    @Override
    public boolean updateEventDetails(Event event) {
        String sql = "UPDATE events SET class_id = ? , " +
                "event_name = ? , " +
                "start_time = ? , " +
                "end_time = ? , " +
                "color = ? , " +
                "timed = ? " +
                "WHERE event_id = ?";
        return jdbcTemplate.update(sql, event.getClass_id(), event.getEvent_name(), event.getStart_time(), event.getEnd_time(), event.getColor(), event.isTimed(), event.getEvent_id())==1;
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
        return event;
    }
    public int daysBetween(Date d1, Date d2) {
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    private String getStartTimeStampBuilder(LocalDate nextYearDayWeek) {
        String startTimeStampBuilder = "";
        String month = String.valueOf(nextYearDayWeek.getMonthValue());
        String day = String.valueOf(nextYearDayWeek.getDayOfMonth());
        String year = String.valueOf(nextYearDayWeek.getYear());

        String buildTime =  new SimpleDateFormat("HH:mm:ss")
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

    private Long dayToMiliseconds(int days){
        Long result = Long.valueOf(days * 24 * 60 * 60 * 1000);
        return result;
    }

    public Timestamp addDays(int days, Timestamp t1) throws Exception{
        if(days < 0){
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
            ClientDetailsDTO clientDetails = new ClientDetailsDTO(result.getInt("client_id"),result.getString("first_name"),result.getString("last_name"));

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

        if(newObject instanceof Array) {
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
