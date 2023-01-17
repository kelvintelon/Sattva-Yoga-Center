package com.sattvayoga.dao;

import com.sattvayoga.model.ClassDetails;
import com.sattvayoga.model.Event;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Component
public class JdbcEventDao implements EventDao {

    @Override
    public List<Event> createAndGetEvents(List<ClassDetails> classDetails) {
        List<Event> eventList = new ArrayList<>();

        // loop through each class
        for (int i = 0; i < classDetails.size(); i++) {
            ClassDetails currentClass = classDetails.get(i);
            String[] dateRange = currentClass.getDate_range();

            // loop through the range of days for each individual class
            for (int j = 0; j < dateRange.length; j++) {
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

                // TODO: loop for a year here, create events a year in advance.
                if (dateRange[j].equals("Sun")) {
                    LocalDate nextOrSameSun = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

                    String startTimeStampBuilder = "";
                    String month = String.valueOf(nextOrSameSun.getMonthValue());
                    String day = String.valueOf(nextOrSameSun.getDayOfMonth());
                    String year = String.valueOf(nextOrSameSun.getYear());

                    String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US) ).format(DateTimeFormatter.ofPattern("HH:mm"));
                    startTimeStampBuilder += year + "-" + month + "-" + day + " " + time.substring(0,5) + ":00.00";

                    Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                    Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                    event.setStart_time(start);
                    event.setEnd_time(end);
                    eventList.add(event);
                }

                if (dateRange[j].equals("Mon")) {

                    LocalDate nextOrSameMon = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));

                    String startTimeStampBuilder = "";
                    String month = String.valueOf(nextOrSameMon.getMonthValue());
                    String day = String.valueOf(nextOrSameMon.getDayOfMonth());
                    String year = String.valueOf(nextOrSameMon.getYear());

                    String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US) ).format(DateTimeFormatter.ofPattern("HH:mm"));
                    startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                    Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                    Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                    event.setStart_time(start);
                    event.setEnd_time(end);
                    eventList.add(event);
                }

                if (dateRange[j].equals("Tue")) {

                    LocalDate nextOrSameTue = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));

                    String startTimeStampBuilder = "";
                    String month = String.valueOf(nextOrSameTue.getMonthValue());
                    String day = String.valueOf(nextOrSameTue.getDayOfMonth());
                    String year = String.valueOf(nextOrSameTue.getYear());

                    String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US) ).format(DateTimeFormatter.ofPattern("HH:mm"));
                    startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                    Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                    Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                    event.setStart_time(start);
                    event.setEnd_time(end);
                    eventList.add(event);
                }

                if (dateRange[j].equals("Wed")) {

                    LocalDate nextOrSameWed = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY));

                    String startTimeStampBuilder = "";
                    String month = String.valueOf(nextOrSameWed.getMonthValue());
                    String day = String.valueOf(nextOrSameWed.getDayOfMonth());
                    String year = String.valueOf(nextOrSameWed.getYear());

                    String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US) ).format(DateTimeFormatter.ofPattern("HH:mm"));
                    startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                    Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                    Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                    event.setStart_time(start);
                    event.setEnd_time(end);
                    eventList.add(event);
                }

                if (dateRange[j].equals("Thu")) {

                    LocalDate nextOrSameThu = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));

                    String startTimeStampBuilder = "";
                    String month = String.valueOf(nextOrSameThu.getMonthValue());
                    String day = String.valueOf(nextOrSameThu.getDayOfMonth());
                    String year = String.valueOf(nextOrSameThu.getYear());

                    String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US) ).format(DateTimeFormatter.ofPattern("HH:mm"));
                    startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                    Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                    Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                    event.setStart_time(start);
                    event.setEnd_time(end);
                    eventList.add(event);
                }

                if (dateRange[j].equals("Fri")) {

                    LocalDate nextOrSameFri = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));

                    String startTimeStampBuilder = "";
                    String month = String.valueOf(nextOrSameFri.getMonthValue());
                    String day = String.valueOf(nextOrSameFri.getDayOfMonth());
                    String year = String.valueOf(nextOrSameFri.getYear());

                    String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US) ).format(DateTimeFormatter.ofPattern("HH:mm"));
                    startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                    Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                    Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                    event.setStart_time(start);
                    event.setEnd_time(end);
                    eventList.add(event);
                }

                if (dateRange[j].equals("Sat")) {

                    LocalDate nextOrSameSat = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

                    String startTimeStampBuilder = "";
                    String month = String.valueOf(nextOrSameSat.getMonthValue());
                    String day = String.valueOf(nextOrSameSat.getDayOfMonth());
                    String year = String.valueOf(nextOrSameSat.getYear());

                    String time = LocalTime.parse(currentClass.getStart_time(), DateTimeFormatter.ofPattern("hh:mm a", Locale.US) ).format(DateTimeFormatter.ofPattern("HH:mm"));
                    startTimeStampBuilder += year + "-" + month + "-" + day + " " + time + ":00.0";

                    Timestamp start = Timestamp.valueOf(startTimeStampBuilder);
                    Timestamp end = new Timestamp(start.getTime() + TimeUnit.HOURS.toMillis(1));
                    event.setStart_time(start);
                    event.setEnd_time(end);
                    eventList.add(event);
                }

            }
        }
        return eventList;
    }
}
