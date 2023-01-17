package com.sattvayoga.dao;

import com.sattvayoga.model.ClassDetails;
import com.sattvayoga.model.Event;

import java.util.List;

public interface EventDao {

    List<Event> createAndGetEvents(List<ClassDetails> classDetails);

}
