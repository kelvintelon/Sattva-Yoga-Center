package com.sattvayoga.controller;

import com.sattvayoga.dao.TeacherDetailsDao;
import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.TeacherDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("isAuthenticated()")
@RestController
@CrossOrigin
public class TeacherDetailsController {

    private TeacherDetailsDao teacherDetailsDao;

    public TeacherDetailsController(TeacherDetailsDao teacherDao) {
        this.teacherDetailsDao = teacherDao;
    }

    // add an annotation for admin role only to call this endpoint
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/registerTeacher", method = RequestMethod.POST)
    public void registerTeacher(@RequestBody TeacherDetails teacher) {

        // should we have exceptions if the teacher is already registered
        // (an exception that means they are already inside the teacher table)
        teacherDetailsDao.createTeacher(teacher);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/updateTeacherDetails", method = RequestMethod.PUT)
    public void updateTeacherDetails (@RequestBody TeacherDetails teacherDetails) {
        teacherDetailsDao.updateTeacher(teacherDetails);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/deleteTeacher/{teacherId}", method = RequestMethod.DELETE)
    public void deleteTeacher (@PathVariable int teacherId) {
        teacherDetailsDao.deleteTeacher(teacherId);
    }
}
