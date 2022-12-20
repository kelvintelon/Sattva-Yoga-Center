package com.sattvayoga.controller;

import com.sattvayoga.dao.ClassDetailsDao;
import com.sattvayoga.model.ClassDetails;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class ClassDetailsController {

    private ClassDetailsDao classDetailsDao;

    public ClassDetailsController(ClassDetailsDao classDetailsDao) {
        this.classDetailsDao = classDetailsDao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/createClass", method = RequestMethod.POST)
    public void createClass(@RequestBody ClassDetails classDetails) {

        // should we have exceptions if the class is already registered
        // (an exception that means they are already inside the class table)

        classDetailsDao.createClass(classDetails);
    }
}
