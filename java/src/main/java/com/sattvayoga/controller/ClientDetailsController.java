package com.sattvayoga.controller;

import com.sattvayoga.dao.ClientDetailsDao;
import com.sattvayoga.model.ClientDetails;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class ClientDetailsController {

    private ClientDetailsDao clientDetailsDao;

    public ClientDetailsController(ClientDetailsDao clientDao) {
        this.clientDetailsDao = clientDao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/registerClient", method = RequestMethod.POST)
    public void registerClient(@RequestBody ClientDetails client) {

        // should we have exceptions if the client is already registered
        // (an exception that means they are already inside the client table)

        clientDetailsDao.createClient(client);
    }
}