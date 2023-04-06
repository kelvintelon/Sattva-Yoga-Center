package com.sattvayoga.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sattvayoga.dao.ClassDetailsDao;
import com.sattvayoga.dao.ClientDetailsDao;
import com.sattvayoga.dao.EventDao;
import com.sattvayoga.dao.UserDao;
import com.sattvayoga.model.ClassDetails;
import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.YogaUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@CrossOrigin
public class ClassDetailsController {

    private ClassDetailsDao classDetailsDao;
    private UserDao userDao;
    private ClientDetailsDao clientDetailsDao;
    private EventDao eventDao;

    public ClassDetailsController(ClassDetailsDao classDetailsDao, UserDao userDao, ClientDetailsDao clientDetailsDao, EventDao eventDao) {
        this.classDetailsDao = classDetailsDao;
        this.userDao = userDao;
        this.clientDetailsDao = clientDetailsDao;
        this.eventDao = eventDao;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/createClass", method = RequestMethod.POST)
    public void createClass(@RequestBody ClassDetails classDetails) {

        // should we have exceptions if the class is already registered
        // (an exception that means they are already inside the class table)

        classDetailsDao.createClass(classDetails);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value= "/classList", method = RequestMethod.GET)
    public List<ClassDetails> getAllClasses() throws SQLException {
        return classDetailsDao.getAllClasses();
    }

    @RequestMapping(value= "/clientClassList", method = RequestMethod.GET)
    public List<ClassDetails> getAllClientClasses(Principal principal) throws SQLException {
        int userId = userDao.findIdByUsername(principal.getName());
        return classDetailsDao.getAllClientClasses(userId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/registerForClass", method = RequestMethod.POST)
    public void registerForClass(@RequestBody ClientClassWrapper clientClass) {

        // should we have exceptions if the class is already registered
        // (an exception that means they are already inside the class table)

        classDetailsDao.registerForClass(clientClass.getClient_id(),clientClass.getClass_id());
    }

    @RequestMapping(value = "/removeClassForClient/{classId}", method = RequestMethod.DELETE)
    public void deleteClassForClient (@PathVariable int classId ,Principal principal) {
       ClientDetails clientDetails = clientDetailsDao.findClientByUserId(userDao.findIdByUsername(principal.getName()));
        classDetailsDao.deleteClassForClient(classId, clientDetails.getClient_id());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value= "/updateClass", method = RequestMethod.PUT)
    public void updateClass(@RequestBody ClassDetails classDetails) {

        // find the original class object
        ClassDetails originalClass = classDetailsDao.getClassByClassId(classDetails.getClass_id());


        // then apply the changes with the new class object information
         eventDao.updateEventsByClass(originalClass, classDetails);

        // finally, update the class itself
        classDetailsDao.updateClass(classDetails);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/deleteClass/{classId}", method = RequestMethod.DELETE)
    public void deleteClass (@PathVariable int classId) {
        classDetailsDao.deleteClass(classId);
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
