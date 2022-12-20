package com.sattvayoga.controller;

import com.sattvayoga.dao.ClassDetailsDao;
import com.sattvayoga.model.ClassDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ClassDetailsController {

    private ClassDetailsDao classDetailsDao;

    public ClassDetailsController(ClassDetailsDao classDetailsDao) {
        this.classDetailsDao = classDetailsDao;
    }


}
