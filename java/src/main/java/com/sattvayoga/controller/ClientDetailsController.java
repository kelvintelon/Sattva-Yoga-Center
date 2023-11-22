package com.sattvayoga.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sattvayoga.dao.ClientDetailsDao;
import com.sattvayoga.dao.EventDao;
import com.sattvayoga.dao.StripeDao;
import com.sattvayoga.dao.UserDao;
import com.sattvayoga.model.*;
import com.stripe.Stripe;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Random;

@PreAuthorize("isAuthenticated()")
@RestController
@CrossOrigin
public class ClientDetailsController {

    private ClientDetailsDao clientDetailsDao;
    private UserDao userDao;
    private EventDao eventDao;
    private StripeDao stripeDao;

    public ClientDetailsController(ClientDetailsDao clientDao, UserDao userDao, EventDao eventDao, StripeDao stripeDao) {
        this.clientDetailsDao = clientDao;
        this.userDao = userDao;
        this.eventDao = eventDao;
        this.stripeDao = stripeDao;
    }

//    http://localhost:8080/getPaginatedClients?page=1&limit=10
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/getPaginatedClients", method = RequestMethod.GET)
    public PaginatedListOfClients getPaginatedClients(@RequestParam(defaultValue = "1")  int page,
                                                      @RequestParam(defaultValue = "10") int pageSize,
                                                      @RequestParam(defaultValue = "") String search,
                                                      @RequestParam(defaultValue = "client_id") String sortBy,
                                                      @RequestParam(defaultValue = "false") boolean sortDesc) {
        // sort by is just another string concatenation
            return clientDetailsDao.getAllPaginatedClients(page,pageSize,search, sortBy, sortDesc);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/getPaginatedClientsForEvent", method = RequestMethod.GET)
    public PaginatedListOfClients getPaginatedClientsForEvent(
                                                      @RequestParam int eventId,
                                                      @RequestParam(defaultValue = "1")  int page,
                                                      @RequestParam(defaultValue = "10") int pageSize,
                                                      @RequestParam(defaultValue = "") String search,
                                                      @RequestParam(defaultValue = "client_id") String sortBy,
                                                      @RequestParam(defaultValue = "false") boolean sortDesc) {
        // sort by is just another string concatenation
        return clientDetailsDao.getPaginatedClientsForEvent(page,pageSize,search, sortBy, sortDesc, eventId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "/clientList", method = RequestMethod.GET)
    public List<ClientDetails> getAllClients() {

        return clientDetailsDao.getAllClients();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/getPaginatedDuplicateClients", method = RequestMethod.GET)
    public PaginatedListOfClients getPaginatedDuplicateClientList(@RequestParam(defaultValue = "1")  int page,
                                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                                  @RequestParam(defaultValue = "") String search) {
        return clientDetailsDao.getAllPaginatedDuplicateClients(page,pageSize,search);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path="/duplicateList", method = RequestMethod.GET)
    public List<ClientDetails> getAllDuplicateClients(){
        return clientDetailsDao.getAllDuplicateClients();
    }

    //TODO: This is for testing purposes
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/register100Clients", method = RequestMethod.POST)
    public void register100Clients() {
        for (int k = 0; k < 1000; k++) {


            int leftLimit = 48; // numeral '0'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();

            // create password
            String generatedPassword = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();


            // create username
            String generatedUsername = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            generatedUsername = "user" + generatedUsername;

            // create user with username and password
            userDao.create(generatedUsername, generatedPassword, "user");

            // retrieve the user ID,
            YogaUser newUser = userDao.findByUsername(generatedUsername);
            int userId = newUser.getId();

            ClientDetails clientDetails = new ClientDetails();

            clientDetails.setFirst_name(generatedUsername);
            clientDetails.setLast_name("Telon");
            clientDetails.setUser_id(userId);

            Date date = new Date();
            Timestamp theLatestTimestamp = new Timestamp(date.getTime());
            clientDetails.setIs_client_active(true);
            clientDetails.setIs_allowed_video(false);
            clientDetails.setDate_of_entry(theLatestTimestamp);
            // use it to create a client
            int clientId = clientDetailsDao.createNewClient(clientDetails).getClient_id();

        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/registerNewClient", method = RequestMethod.POST)
    public void registerNewClient(@RequestBody ClientDetails newClientDetails) {

        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        // create password
        String generatedPassword = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();


        // create username
        String generatedUsername = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        generatedUsername = "user" + generatedUsername;

        // create user with username and password
        userDao.create(generatedUsername,generatedPassword, "user");

        // retrieve the user ID,
        YogaUser newUser = userDao.findByUsername(generatedUsername);
        int userId = newUser.getId();

        ClientDetails clientDetails = new ClientDetails();

        clientDetails.setFirst_name(newClientDetails.getFirst_name());
        clientDetails.setLast_name(newClientDetails.getLast_name());
        clientDetails.setUser_id(userId);

        Date date = new Date();
        Timestamp theLatestTimestamp = new Timestamp(date.getTime());
        clientDetails.setEmail(newClientDetails.getEmail());
        clientDetails.setIs_client_active(false);
        clientDetails.setIs_on_email_list(newClientDetails.isIs_on_email_list());
        clientDetails.setIs_allowed_video(newClientDetails.isIs_allowed_video());
        clientDetails.setIs_new_client(true);
        clientDetails.setDate_of_entry(theLatestTimestamp);
        // use it to create a client
        int clientId = clientDetailsDao.createNewClient(clientDetails).getClient_id();
    }

    @RequestMapping(value = "/updateClientDetails", method = RequestMethod.PUT)
    public void updateClientDetails(@RequestBody ClientDetails clientDetails) {


        // look up the email here
        boolean foundEmail = false;
        if (clientDetails.getEmail() != null && !(clientDetails.getEmail().equalsIgnoreCase(""))) {
            foundEmail = clientDetailsDao.isEmailDuplicate(clientDetails.getClient_id(),clientDetails.getEmail());
        }

        if (foundEmail || (clientDetails.getEmail() != null && ( clientDetails.getEmail().equalsIgnoreCase("info@sattva-yoga-center.com") || clientDetails.getEmail().equalsIgnoreCase("sattva.yoga.center.michigan@gmail.com")))) {
            throw new EmailAlreadyExistsException();
        } else {
            clientDetailsDao.updateClientDetails(clientDetails);
        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value ="/updateEmailForClient", method = RequestMethod.PUT)
    public String updateEmailForClient(@RequestBody ClientDetails clientDetails) {
        boolean foundEmail = false;

        if (clientDetails.getEmail() != null && !(clientDetails.getEmail().equalsIgnoreCase(""))) {
            foundEmail = clientDetailsDao.isEmailDuplicate(clientDetails.getClient_id(), clientDetails.getEmail());
        }

        if (foundEmail || (clientDetails.getEmail() != null && ( clientDetails.getEmail().equalsIgnoreCase("info@sattva-yoga-center.com") || clientDetails.getEmail().equalsIgnoreCase("sattva.yoga.center.michigan@gmail.com")))) {
            return "Email in use";
        } else {
            ClientDetails retrievedClient =
                    clientDetailsDao.findClientByClientId(clientDetails.getClient_id());

            clientDetailsDao.saveNewClientEmail(clientDetails.getClient_id(), clientDetails.getEmail());

            stripeDao.updateCustomerEmail(retrievedClient.getCustomer_id(), clientDetails.getEmail());

        }

        return "Successful";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/mergeClients", method = RequestMethod.PUT)
    public void mergeClients(@RequestBody List<ClientDetails> clientDetails) {

        // loop through each individual client, grab their ID, and update the client ID in every table one iteration at a time
        // then delete them
        for (int i = 1; i < clientDetails.size(); i++) {
            clientDetailsDao.removeDuplicateClients(clientDetails.get(0).getClient_id(), clientDetails.get(i).getClient_id());
        }

        // Update the Client Details of the one they want to keep but make sure the email isn't taken by a user that wasn't merged

        // look up the email here
        boolean foundEmail = false;
        if (clientDetails.get(0).getEmail() != null && !clientDetails.get(0).getEmail().isEmpty()) {
            foundEmail = clientDetailsDao.isEmailDuplicate(clientDetails.get(0).getClient_id(),clientDetails.get(0).getEmail());
        }

        if (foundEmail || (clientDetails.get(0).getEmail() != null && ( clientDetails.get(0).getEmail().equalsIgnoreCase("info@sattva-yoga-center.com") || clientDetails.get(0).getEmail().equalsIgnoreCase("sattva.yoga.center.michigan@gmail.com")))) {
            clientDetails.get(0).setEmail("");
            clientDetailsDao.updateClientDetails(clientDetails.get(0));
            throw new EmailAlreadyExistsException();
        } else {
            clientDetailsDao.updateClientDetails(clientDetails.get(0));
        }

    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/registerClient", method = RequestMethod.POST)
    public ResponseEntity<ClientDetailsResponse> registerClient(@RequestBody ClientDetails client) {

        // look up the email here
        boolean foundEmail = false;
        if (client.getEmail() != null && !client.getEmail().isEmpty()) {
            foundEmail = clientDetailsDao.isEmailDuplicate(0,client.getEmail());
        }

        if (foundEmail || (client.getEmail() != null && ( client.getEmail().equalsIgnoreCase("info@sattva-yoga-center.com") || client.getEmail().equalsIgnoreCase("sattva.yoga.center.michigan@gmail.com")))) {
            throw new EmailAlreadyExistsException();
        } else {
            // if we don't want all the hardcoded values passed in from the user we can call the setters and
            // set them before the following method ha@ppens:
            ClientDetails clientDetails = clientDetailsDao.createClient(client);
            HttpHeaders httpHeaders = new HttpHeaders();
            return new ResponseEntity<>(new ClientDetailsResponse(clientDetails.getClient_id(), clientDetails), httpHeaders, HttpStatus.CREATED);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/removeClient/{clientId}", method = RequestMethod.DELETE)
    public void deleteClient(@PathVariable int clientId) {
        clientDetailsDao.deleteClient(clientId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/getStripeCustomerPageByClientId/{clientId}", method = RequestMethod.GET)
    public String getStripeCustomerPageByClientId(@PathVariable int clientId) {
        String customerId = stripeDao.getCustomerIdString(clientId);

        //TODO: Replace the following URL when you go live
        return "https://dashboard.stripe.com/test/customers/" + customerId;
    }

    @RequestMapping(path = "/getClientDetails", method = RequestMethod.GET)
    public ClientDetails getClientDetails(Principal principal) {
        ClientDetails clientDetails =
                clientDetailsDao.findClientByUserId(
                        userDao.findIdByUsername(
                                principal.getName()));
        return clientDetails;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/getClientDetailsOfAdminUser", method = RequestMethod.GET)
    public String getClientDetailsOfAdminUser() {
        return "Success";
    }

    // TODO: Make sure that you verify that the client ID matches the user that is logged in who is making the request
    @RequestMapping(path = "/getClientDetailsByClientId/{clientId}", method = RequestMethod.GET)
    public ClientDetails getClientDetailsByClientId(@PathVariable int clientId) {
        ClientDetails clientDetails = clientDetailsDao.findClientByClientId(clientId);
        clientDetails.setUsername(userDao.getUserById(clientDetails.getUser_id()).getUsername());
        clientDetails.setRedFlag(eventDao.getRedFlaggedClientEventsByClientId(clientId).size() > 0);
        return clientDetails;
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
