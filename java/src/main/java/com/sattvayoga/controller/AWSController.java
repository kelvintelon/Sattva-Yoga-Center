package com.sattvayoga.controller;

import com.sattvayoga.dao.SecretManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class AWSController {

    private final SecretManagerService secretManagerService;

    public AWSController(SecretManagerService secretManagerService) {
        this.secretManagerService = secretManagerService;
    }

    @RequestMapping(value="/getMap", method = RequestMethod.GET)
    public String getMapKey() throws Throwable {
        return secretManagerService.getApiKey();
    }
}
