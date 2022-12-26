package com.sattvayoga.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sattvayoga.dao.ClassDetailsDao;
import com.sattvayoga.model.ClassDetails;
import com.sattvayoga.model.YogaUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@CrossOrigin
public class ClassDetailsController {

    private ClassDetailsDao classDetailsDao;

    public ClassDetailsController(ClassDetailsDao classDetailsDao) {
        this.classDetailsDao = classDetailsDao;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value= "/updateClass", method = RequestMethod.PUT)
    public void updateClass(@RequestBody ClassDetails classDetails) {
        classDetailsDao.updateClass(classDetails);
    }



    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value= "/classList", method = RequestMethod.GET)
    public List<ClassDetails> getAllClasses() {
        return classDetailsDao.getAllClasses();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/createClass", method = RequestMethod.POST)
    public void createClass(@RequestBody ClassDetails classDetails) {

        // should we have exceptions if the class is already registered
        // (an exception that means they are already inside the class table)

        classDetailsDao.createClass(classDetails);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/registerForClass", method = RequestMethod.POST)
    public void registerForClass(@RequestBody ClientClassWrapper clientClass) {

        // should we have exceptions if the class is already registered
        // (an exception that means they are already inside the class table)

        classDetailsDao.registerForClass(clientClass.getClient_id(),clientClass.getClass_id());
    }

    static class ClientClassWrapper {

        private int client_id;
        private int class_id;

        ClientClassWrapper(int client_id, int class_id) {
            this.client_id = client_id;
            this.class_id = class_id;
        }

        @JsonProperty("client_id")
        int getClient_id() {
            return client_id;
        }

        void setClient_id(int client_id) {
            this.client_id = client_id;
        }

        @JsonProperty("class_id")
        int getClass_id() {
            return class_id;
        }

        void setClass_id(int class_id) {
            this.class_id = class_id;
        }

    }
}
