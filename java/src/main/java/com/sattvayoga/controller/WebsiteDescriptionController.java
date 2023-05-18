package com.sattvayoga.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sattvayoga.dao.WebsiteDescriptionDao;
import com.sattvayoga.model.WebsiteDescription;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;

@RestController
@CrossOrigin
public class WebsiteDescriptionController {

    private WebsiteDescriptionDao websiteDescriptionDao;

    public WebsiteDescriptionController(WebsiteDescriptionDao websiteDescriptionDao) {
        this.websiteDescriptionDao = websiteDescriptionDao;
    }

    @RequestMapping(value = "/getNewsAndEventsDescription", method = RequestMethod.GET)
    public String getNewsAndEvents() throws SQLException {
        return websiteDescriptionDao.getNewsAndEvents();
    }
    @RequestMapping(value = "/getClassScheduleDescription", method = RequestMethod.GET)
    public String getClassSchedule() throws SQLException {
        return websiteDescriptionDao.getClassSchedule();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/updateClassScheduleDescription", method = RequestMethod.PUT)
    public void updateClassScheduleDescription(@RequestBody WebsiteDescription newDescription) {
        websiteDescriptionDao.updateClassSchedule(newDescription.getDescription());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/updateNewsAndEventsDescription", method = RequestMethod.PUT)
    public void updateNewsAndEventsDescription(@RequestBody WebsiteDescription newDescription) {
        websiteDescriptionDao.updateNewsAndEvents(newDescription.getDescription());
    }
}
