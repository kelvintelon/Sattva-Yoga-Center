package com.sattvayoga.controller;

import com.sattvayoga.dao.ClassDetailsDao;
import com.sattvayoga.dao.ClientDetailsDao;
import com.sattvayoga.dao.EventDao;
import com.sattvayoga.dao.UserDao;
import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@CrossOrigin
public class EventController {

    @Autowired
    private ClassDetailsDao classDetailsDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ClientDetailsDao clientDetailsDao;
    @Autowired
    private EventDao eventDao;



    @RequestMapping(value = "/eventList", method = RequestMethod.GET)
    public List<Event> getAllEvents() throws SQLException {
        return eventDao.getAllEvents();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/createEvent", method = RequestMethod.POST)
    public void createEvent(@RequestBody Event event) {
        eventDao.createEvent(event);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/updateEvent", method = RequestMethod.PUT)
    public void updateEvent (@RequestBody Event event) {
        eventDao.updateEventDetails(event);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/deleteEvent/{eventId}", method = RequestMethod.DELETE)
    public void deleteEvent (@PathVariable int eventId) {
       eventDao.deleteEvent(eventId);
    }


}
