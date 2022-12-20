package com.sattvayoga.controller;

import com.sattvayoga.dao.ClassAttendanceDao;
import com.sattvayoga.model.ClassAttendance;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class ClassAttendanceController {

    private ClassAttendanceDao classAttendanceDao;

    public ClassAttendanceController(ClassAttendanceDao classAttendanceDao) {
        this.classAttendanceDao = classAttendanceDao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/registerForClass", method = RequestMethod.POST)
    public void registerForClass(@RequestBody ClassAttendance classAttendance) {


        // if we don't want all the hardcoded values passed in from the user we can call the setters and
        // set them for the following method:

        classAttendanceDao.registerIntoClass(classAttendance);
    }
}
