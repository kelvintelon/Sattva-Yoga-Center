package com.sattvayoga.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sattvayoga.dao.ClassDetailsDao;
import com.sattvayoga.dao.ClientDetailsDao;
import com.sattvayoga.dao.EventDao;
import com.sattvayoga.dao.UserDao;
import com.sattvayoga.model.ClassDetails;
import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    @RequestMapping(value = "/100eventList", method = RequestMethod.GET)
    public List<Event> getHundredEvents() throws SQLException {
        return eventDao.getHundredEvents();
    }

//    @RequestMapping(value= "/clientEventList", method = RequestMethod.GET)
//    public List<Event> getAllUpcomingClientEvents(Principal principal) throws SQLException {
//        int userId = userDao.findIdByUsername(principal.getName());
//        return eventDao.getAllUpcomingClientEvents(userId);
//    }

    @RequestMapping(value= "/clientEventList", method = RequestMethod.GET)
    public List<Event> getAllHistoricalClientEvents(Principal principal) throws SQLException {
        int userId = userDao.findIdByUsername(principal.getName());
        return eventDao.getAllHistoricalClientEvents(userId);
    }

    @RequestMapping(value= "/clientEventListByClientId/{clientId}", method = RequestMethod.GET)
    public List<Event> getAllUpcomingClientEventsByClientId(@PathVariable int clientId) throws SQLException {
        return eventDao.getAllHistoricalClientEvents(clientDetailsDao.findClientByClientId(clientId).getUser_id());
    }


    @RequestMapping(value = "/removeEventForClient/{eventId}", method = RequestMethod.DELETE)
    public void deleteClassForClient (@PathVariable int eventId ,Principal principal) {
        ClientDetails clientDetails = clientDetailsDao.findClientByUserId(userDao.findIdByUsername(principal.getName()));
        eventDao.deleteEventForClient(eventId, clientDetails.getClient_id());
    }

    @RequestMapping(value = "/removeEventForClient/{eventId}/{clientId}", method = RequestMethod.DELETE)
    public void deleteClassForClientByClientId (@PathVariable int eventId, @PathVariable int clientId) {
        eventDao.deleteEventForClient(eventId, clientId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/getEventDetailsByEventId/{eventId}", method = RequestMethod.GET)
    public Event getEventDetailsByEventId (@PathVariable int eventId ) {

        return eventDao.getEventByEventId(eventId);
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

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/registerForEvent", method = RequestMethod.POST)
    public void registerForEvent(@RequestBody ClientEventWrapper clientEvent) {

        // should we have exceptions if the class is already registered
        // (an exception that means they are already inside the class table)

        eventDao.registerForEvent(clientEvent.getClient_id(),clientEvent.getEvent_id(),clientEvent.getPackage_purchase_id());
    }

    static class ClientEventWrapper {

        private int client_id;
        private int event_id;
        private int package_purchase_id;

        ClientEventWrapper(int client_id, int event_id, int package_purchase_id) {
            this.client_id = client_id;
            this.event_id = event_id;
            this.package_purchase_id = package_purchase_id;
        }

        @JsonProperty("client_id")
        int getClient_id() {
            return client_id;
        }

        void setClient_id(int client_id) {
            this.client_id = client_id;
        }

        @JsonProperty("event_id")
        public int getEvent_id() {
            return event_id;
        }

        public void setEvent_id(int event_id) {
            this.event_id = event_id;
        }

        @JsonProperty("package_purchase_id")
        public int getPackage_purchase_id() {
            return package_purchase_id;
        }

        public void setPackage_purchase_id(int package_purchase_id) {
            this.package_purchase_id = package_purchase_id;
        }
    }

}
