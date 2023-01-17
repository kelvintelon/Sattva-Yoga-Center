package com.sattvayoga.controller;

import com.sattvayoga.dao.ClassDetailsDao;
import com.sattvayoga.dao.ClientDetailsDao;
import com.sattvayoga.dao.EventDao;
import com.sattvayoga.dao.UserDao;
import com.sattvayoga.model.Event;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@CrossOrigin
public class EventController {

    private ClassDetailsDao classDetailsDao;
    private UserDao userDao;
    private ClientDetailsDao clientDetailsDao;
    private EventDao eventDao;

    public EventController(ClassDetailsDao classDetailsDao, UserDao userDao, ClientDetailsDao clientDetailsDao, EventDao eventDao) {
        this.classDetailsDao = classDetailsDao;
        this.userDao = userDao;
        this.clientDetailsDao = clientDetailsDao;
        this.eventDao = eventDao;
    }

    @RequestMapping(value = "/eventList", method = RequestMethod.GET)
    public List<Event> getAllEvenets() throws SQLException {
        return eventDao.createAndGetEvents(classDetailsDao.getAllClasses());
    }

}
