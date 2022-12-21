package com.sattvayoga.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sattvayoga.dao.ClientDetailsDao;
import com.sattvayoga.model.ClientDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("isAuthenticated()")
@RestController
@CrossOrigin
public class ClientDetailsController {

    private ClientDetailsDao clientDetailsDao;

    public ClientDetailsController(ClientDetailsDao clientDao) {
        this.clientDetailsDao = clientDao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/registerClient", method = RequestMethod.POST)
    public ResponseEntity<ClientDetailsResponse> registerClient(@RequestBody ClientDetails client) {

        // should we have exceptions if the client is already registered
        // (an exception that means they are already inside the client table)


        // if we don't want all the hardcoded values passed in from the userwe can call the setters and
        // set them for the following method:

        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<>(new ClientDetailsResponse(clientDetailsDao.createClient(client)), httpHeaders, HttpStatus.CREATED);
    }

    static class ClientDetailsResponse {

        private int client_id;

        public ClientDetailsResponse(int client_id) {
            this.client_id = client_id;
        }

        @JsonProperty("client_id")
        public int getClient_id() {
            return client_id;
        }

        public void setClient_id(int client_id) {
            this.client_id = client_id;
        }
    }
}
