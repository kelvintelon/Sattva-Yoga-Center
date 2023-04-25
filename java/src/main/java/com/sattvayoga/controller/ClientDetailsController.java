package com.sattvayoga.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sattvayoga.dao.ClientDetailsDao;
import com.sattvayoga.dao.EventDao;
import com.sattvayoga.dao.UserDao;
import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.EmailAlreadyExistsException;
import com.sattvayoga.model.EmailNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@CrossOrigin
public class ClientDetailsController {

    private ClientDetailsDao clientDetailsDao;
    private UserDao userDao;
    private EventDao eventDao;

    public ClientDetailsController(ClientDetailsDao clientDao, UserDao userDao, EventDao eventDao) {
        this.clientDetailsDao = clientDao;
        this.userDao = userDao;
        this.eventDao = eventDao;
    }

    @RequestMapping(value = "/updateClientDetails", method = RequestMethod.PUT)
    public void updateClientDetails(@RequestBody ClientDetails clientDetails) {

        // TODO: Change the following line
        // look up the email here
        boolean foundEmail = false;
        if (clientDetails.getEmail() != null) {
            foundEmail = clientDetailsDao.isEmailDuplicate(clientDetails.getClient_id(),clientDetails.getEmail());
        }

        if (foundEmail) {
            throw new EmailAlreadyExistsException();
        } else {
            clientDetailsDao.updateClientDetails(clientDetails);
        }

    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/registerClient", method = RequestMethod.POST)
    public ResponseEntity<ClientDetailsResponse> registerClient(@RequestBody ClientDetails client) {

        // look up the email here
        boolean foundEmail = false;
        if (client.getEmail() != null) {
            foundEmail = clientDetailsDao.isEmailDuplicate(0,client.getEmail());
        }

        if (foundEmail) {
            throw new EmailAlreadyExistsException();
        } else {
            // if we don't want all the hardcoded values passed in from the user we can call the setters and
            // set them before the following method happens:
            ClientDetails clientDetails = clientDetailsDao.createClient(client);
            HttpHeaders httpHeaders = new HttpHeaders();
            return new ResponseEntity<>(new ClientDetailsResponse(clientDetails.getClient_id(), clientDetails), httpHeaders, HttpStatus.CREATED);
        }
    }
    @RequestMapping(value = "/removeClient/{clientId}", method = RequestMethod.DELETE)
    public void deleteClient(@PathVariable int clientId) {
        clientDetailsDao.deleteClient(clientId);
    }

    @RequestMapping(path = "/getClientDetails", method = RequestMethod.GET)
    public ClientDetails getClientDetails(Principal principal) {
        ClientDetails clientDetails =
                clientDetailsDao.findClientByUserId(
                        userDao.findIdByUsername(
                                principal.getName()));
        return clientDetails;
    }

    @RequestMapping(path = "/getClientDetailsByClientId/{clientId}", method = RequestMethod.GET)
    public ClientDetails getClientDetailsByClientId(@PathVariable int clientId) {
        ClientDetails clientDetails = clientDetailsDao.findClientByClientId(clientId);
        clientDetails.setUsername(userDao.getUserById(clientDetails.getUser_id()).getUsername());
        clientDetails.setRedFlag(eventDao.getRedFlaggedClientEventsByClientId(clientId).size() > 0);
        return clientDetails;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "/clientList", method = RequestMethod.GET)
    public List<ClientDetails> getAllClients() {

        return clientDetailsDao.getAllClients();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path="/duplicateList", method = RequestMethod.GET)
    public List<ClientDetails> getAllDuplicateClients(){
        return clientDetailsDao.getAllDuplicateClients();
    }



    static class ClientDetailsResponse {

        private int client_id;
        private ClientDetails clientDetails;

        public ClientDetailsResponse(int client_id, ClientDetails clientDetails) {
            this.client_id = client_id;
            this.clientDetails = clientDetails;
        }

        @JsonProperty("client_id")
        public int getClient_id() {
            return client_id;
        }

        public void setClient_id(int client_id) {
            this.client_id = client_id;
        }

        @JsonProperty("clientDetails")
        public ClientDetails getClientDetails() {
            return clientDetails;
        }

        public void setClientDetails(ClientDetails clientDetails) {
            this.clientDetails = clientDetails;
        }
    }
}
