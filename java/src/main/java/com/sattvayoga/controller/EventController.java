package com.sattvayoga.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sattvayoga.dao.*;
import com.sattvayoga.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;


@RestController
@CrossOrigin
public class EventController {

    @Autowired
    private ClassDetailsDao classDetailsDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ClientDetailsDao clientDetailsDao;
    @Autowired
    private EventDao eventDao;
    @Autowired
    private PackagePurchaseDao packagePurchaseDao;


    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/eventList", method = RequestMethod.GET)
    public List<ClassEvent> getAllEvents() throws SQLException {
        return eventDao.getAllEvents();
    }

    @RequestMapping(value = "/100eventList", method = RequestMethod.GET)
    public List<ClassEvent> getHundredEvents() throws SQLException {
        return eventDao.getHundredEvents();
    }

    @RequestMapping(value = "/100eventList/{clientId}", method = RequestMethod.GET)
    public List<ClassEvent> getHundredEventsForClient(@PathVariable int clientId) throws SQLException {
        return eventDao.getHundredEventsForUser(clientId);
    }

//    @RequestMapping(value= "/clientEventList", method = RequestMethod.GET)
//    public List<Event> getAllUpcomingClientEvents(Principal principal) throws SQLException {
//        int userId = userDao.findIdByUsername(principal.getName());
//        return eventDao.getAllUpcomingClientEvents(userId);
//    }

    @RequestMapping(value= "/clientEventList", method = RequestMethod.GET)
    public List<ClassEvent> getAllHistoricalClientEvents(Principal principal) throws SQLException {
        int userId = userDao.findIdByUsername(principal.getName());
        return eventDao.getAllHistoricalClientEvents(userId);
    }

    @RequestMapping(value= "/clientEventListByClientId/{clientId}", method = RequestMethod.GET)
    public List<ClassEvent> getAllUpcomingClientEventsByClientId(@PathVariable int clientId) throws SQLException {
        return eventDao.getAllHistoricalClientEvents(clientDetailsDao.findClientByClientId(clientId).getUser_id());
    }


    @RequestMapping(value = "/removeEventForClient/{eventId}", method = RequestMethod.DELETE)
    public void deleteClassForClient (@PathVariable int eventId ,Principal principal) {
        ClientDetails clientDetails = clientDetailsDao.findClientByUserId(userDao.findIdByUsername(principal.getName()));
        eventDao.deleteEventForClient(eventId, clientDetails.getClient_id());

        int packagePurchaseID = eventDao.getPackagePurchaseIdByEventIdClientId(eventId,clientDetails.getClient_id());
        PackagePurchase packagePurchase = packagePurchaseDao.getPackagePurchaseObjectByPackagePurchaseId(packagePurchaseID);

        if (!packagePurchase.isUnlimited() && packagePurchase.getPackage_id() != 22 && packagePurchaseID > 0) {
            packagePurchaseDao.incrementByOne(packagePurchaseID);
        }
    }

    @RequestMapping(value = "/removeEventForClient/{eventId}/{clientId}", method = RequestMethod.DELETE)
    public void deleteClassForClientByClientId (@PathVariable int eventId, @PathVariable int clientId) {

        int packagePurchaseID = eventDao.getPackagePurchaseIdByEventIdClientId(eventId,clientId);

        eventDao.deleteEventForClient(eventId, clientId);

        PackagePurchase packagePurchase = packagePurchaseDao.getPackagePurchaseObjectByPackagePurchaseId(packagePurchaseID);

        if (!packagePurchase.isUnlimited() && packagePurchase.getPackage_id() != 22 && packagePurchaseID > 0) {
            packagePurchaseDao.incrementByOne(packagePurchaseID);
        }
    }

    @RequestMapping(value = "/removeEventForSelectedClients", method = RequestMethod.PUT)
    public void deleteClassForMultipleClients (@RequestBody List<ClientEvent> clientEventObjects) {


        for (int i = 0; i < clientEventObjects.size(); i++) {
            int eventId = clientEventObjects.get(i).getEvent_id();
            int clientId = clientEventObjects.get(i).getClient_id();
            // TODO: Handle the logic where a client doesn't have a package wherever you have to here
            PackagePurchase packagePurchase = new PackagePurchase();

            // retrieve the package purchase ID that used to sign up, if there is one,
            int packagePurchaseId = eventDao.getPackagePurchaseIdByEventIdClientId(eventId, clientId);
            // retrieve the object that finds out if it's a subscription
            if (packagePurchaseId > 0) {
                packagePurchase = packagePurchaseDao.getPackagePurchaseObjectByPackagePurchaseId(packagePurchaseId);
            }

            // remove the client from the roster of that specific event, and increment the package if it's a bundle
            eventDao.deleteEventForClient(eventId, clientId);

            // increment back up if it wasn't a subscription
            if (packagePurchase.getPackage_purchase_id() > 0 && !packagePurchase.isUnlimited()) {
                packagePurchaseDao.incrementByOne(packagePurchaseId);
            }
        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/reconcileClassesForClient/{clientId}", method = RequestMethod.PUT)
    public void reconcileClassesForClient(@PathVariable int clientId) {
        ClientDetails clientDetails = clientDetailsDao.findClientByClientId(clientId);
        int userId = clientDetails.getUser_id();


        // grab the list of client-event objects to update
        List<ClientEvent> clientEventsList = eventDao.getRedFlaggedClientEventsByClientId(clientId);

        // turn each one into an event object, maybe a list of events to loop through
        List<ClassEvent> classEventList = new ArrayList<>();

        for (int i = 0; i < clientEventsList.size(); i++) {
            ClassEvent classEvent = eventDao.getEventByEventId(clientEventsList.get(i).getEvent_id());
            classEventList.add(classEvent);
        }

        // grab a list of packages
        List<PackagePurchase> allUserPackagePurchase = packagePurchaseDao.getAllActiveUserPackagePurchases(userId);

        for (int i = 0; i < classEventList.size(); i++) {
            // filter the list of packages to just one
            PackagePurchase packagePurchase = packagePurchaseDao.filterPackageList(allUserPackagePurchase, classEventList.get(i));

            if (packagePurchase.getClasses_remaining() == 0 && !packagePurchase.isUnlimited()) {
                //  update the packagePurchase in the database here then leave/break right after
                packagePurchaseDao.updatePackage(packagePurchase);

//                break;
            }

            // shared package reconcile
            if(packagePurchase.getPackage_purchase_id() == 0){
                List<PackagePurchase> allActiveSharedPackages = packagePurchaseDao.getAllSharedActivePackages(clientId);
                if(allActiveSharedPackages.size()>0){
                    packagePurchase = packagePurchaseDao.filterPackageList(allActiveSharedPackages, classEventList.get(i));
                }
            }

            // update each client-event object row individually using the packagePurchase ID
            eventDao.reconcileClassWithPackageId(packagePurchase.getPackage_purchase_id(), classEventList.get(i).getEvent_id(), clientId);

            // decrement each time if it's a bundle.
            if (!packagePurchase.isUnlimited() && packagePurchase.getClasses_remaining() > 0) {
                int currentAmountOfClasses = packagePurchase.getClasses_remaining();
                packagePurchase.setClasses_remaining(currentAmountOfClasses-1);
                packagePurchaseDao.updatePackage(packagePurchase);
            }

        }


    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/getEventDetailsByEventId/{eventId}", method = RequestMethod.GET)
    public ClassEvent getEventDetailsByEventId (@PathVariable int eventId ) {

        return eventDao.getEventByEventId(eventId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/getSignUpAggregate", method = RequestMethod.GET)
    public SignUpAggregate getSignUpAggregate() {
        return eventDao.getSignUpAggregate();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value= "/retrievePackagePurchaseId/{eventId}/{clientId}", method = RequestMethod.GET)
    public int getPackagePurchaseIdByEventIdClientId(@PathVariable int eventId, @PathVariable int clientId) {
        return eventDao.getPackagePurchaseIdByEventIdClientId(eventId, clientId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/createEvent", method = RequestMethod.POST)
    public String createEvent(@RequestBody ClassEvent classEvent) {
        // TODO: Make this into an exception thrown
        if (eventDao.isThereExistingEventWithStartTime(classEvent)) {
            return "Fail";
        } else {
            eventDao.createEvent(classEvent);
        }
        return "Success";

    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/updateEvent", method = RequestMethod.PUT)
    public String updateEvent (@RequestBody ClassEvent classEvent) {
        // TODO: Make this into an exception thrown
        if (eventDao.isThereExistingEventWithStartTime(classEvent)) {
            return "Fail";
        } else {
            eventDao.updateEventDetails(classEvent);
        }

        return "Success";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/deleteEvent/{eventId}", method = RequestMethod.DELETE)
    public void deleteEvent (@PathVariable int eventId) {
       eventDao.deleteEvent(eventId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/registerForEvent", method = RequestMethod.POST)
    public void registerForEvent(@RequestBody ClientEventWrapper clientEvent) {

        // should we have exceptions if the class is already registered
        // (an exception that means they are already inside the class table)

        eventDao.registerForEvent(clientEvent.getClient_id(),clientEvent.getEvent_id(),clientEvent.getPackage_purchase_id());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/registerNewClientToEvent", method = RequestMethod.POST)
    public void registerNewClientForEvent(@RequestBody NewClientSignUp newClientSignUp) {

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

        clientDetails.setFirst_name(newClientSignUp.getFirst_name());
        clientDetails.setLast_name(newClientSignUp.getLast_name());
        clientDetails.setEmail(newClientSignUp.getEmail());
        clientDetails.setUser_id(userId);

        Date date = new Date();
        Timestamp theLatestTimestamp = new Timestamp(date.getTime());
        clientDetails.setIs_client_active(true);
        clientDetails.setDate_of_entry(theLatestTimestamp);
        // use it to create a client
        int clientId = clientDetailsDao.createNewClient(clientDetails).getClient_id();


        ClientEvent clientEventObj = new ClientEvent();

        // use client ID, event ID, user ID to sign up client,
        clientEventObj.setPackage_purchase_id(0);
        clientEventObj.setClient_id(clientId);
        clientEventObj.setEvent_id(newClientSignUp.event_id);


        List<ClientEvent> clientEventObjects = new ArrayList<>();
        clientEventObjects.add(clientEventObj);

        // could use registerMultipleClientsForEvent if you pass this single client into a list as a clientEventObject
        // retrieve the event object once
        ClassEvent classEvent = eventDao.getEventByEventId(clientEventObjects.get(0).getEvent_id());

        for (int i = 0; i < clientEventObjects.size(); i++) {
            // current clientEvent object
            ClientEvent clientEvent = clientEventObjects.get(i);

            // if the event has to be paid or not
            if (classEvent.isIs_paid()) {

                // client details
                ClientDetails clientDetailsObj = clientDetailsDao.findClientByClientId(clientEvent.getClient_id());
                // user Id
                int userIdNum = clientDetailsObj.getUser_id();
                // find active packages for each client/user
                List<PackagePurchase> allUserPackagePurchase = packagePurchaseDao.getAllUserPackagePurchases(userIdNum);
                // filter the list of packages to just one
                PackagePurchase packagePurchase = packagePurchaseDao.filterPackageList(allUserPackagePurchase, classEvent);

                // finally once you've pinpointed the package, set the package purchase ID into the object
                if (packagePurchase.getPackage_purchase_id() > 0) {
                    clientEvent.setPackage_purchase_id(packagePurchase.getPackage_purchase_id());
                }

                // decrement if it's a quantity bundle but register it into the table either way
                if (!packagePurchase.isUnlimited()) {
                    packagePurchaseDao.decrementByOne(packagePurchase.getPackage_purchase_id());
                }

            } else {
                clientEvent.setPackage_purchase_id(-1);
            }
            eventDao.registerForEvent(clientEvent.getClient_id(),clientEvent.getEvent_id(),clientEvent.getPackage_purchase_id());
        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/registerMultipleClientsForEvent", method = RequestMethod.POST)
    public void registerMultipleClientsForEvent(@RequestBody List<ClientEvent> clientEventObjects) {

        // retrieve the event object once
        ClassEvent classEvent = eventDao.getEventByEventId(clientEventObjects.get(0).getEvent_id());


        for (int i = 0; i < clientEventObjects.size(); i++) {
            // current clientEvent object
            ClientEvent clientEvent = clientEventObjects.get(i);

            // if the event has to be paid or not
            if (classEvent.isIs_paid()) {
                // client details
                ClientDetails clientDetails = clientDetailsDao.findClientByClientId(clientEvent.getClient_id());
                // user Id
                int userId = clientDetails.getUser_id();
                // find active packages for each client/user
                List<PackagePurchase> allUserPackagePurchase = packagePurchaseDao.getAllActiveUserPackagePurchases(userId);
                // filter the list of packages to just one
                PackagePurchase packagePurchase = packagePurchaseDao.filterPackageList(allUserPackagePurchase, classEvent);


                // if user doesn't have any usable package, look for shared packages;
                if (packagePurchase.getPackage_purchase_id() == 0) {
                    List<PackagePurchase> allActiveSharedPackages = packagePurchaseDao.getAllSharedActivePackages(clientEvent.getClient_id());
                    if (allActiveSharedPackages.size() > 0) {
                        //Come up with logic that filters for
                        packagePurchase = packagePurchaseDao.filterPackageList(allActiveSharedPackages,classEvent);
                    }
                }


                // finally once you've pinpointed the package, set the package purchase ID into the object
                if (packagePurchase.getPackage_purchase_id() > 0) {
                    clientEvent.setPackage_purchase_id(packagePurchase.getPackage_purchase_id());
                }

                // decrement if it's a quantity bundle but register it into the table either way
                if (!packagePurchase.isUnlimited()) {
                    packagePurchaseDao.decrementByOne(packagePurchase.getPackage_purchase_id());
                }
            } else {
                clientEvent.setPackage_purchase_id(-1);
            }
            eventDao.registerForEvent(clientEvent.getClient_id(),clientEvent.getEvent_id(),clientEvent.getPackage_purchase_id());
        }

    }

    //TODO: This is for package+client testing purposes
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/registerAClientAndPurchasePackage/{id}", method = RequestMethod.POST)
    public void registerAClientAndPurchasePackage(@PathVariable int id) {

        for (int i = 1; i < 1664; i++) {
            PackagePurchase packagePurchase = new PackagePurchase();
            packagePurchase.setClient_id(id);
            packagePurchase.setPackage_id(3);
            packagePurchase.setDate_purchased( new Timestamp(System.currentTimeMillis()));


            LocalDate ld = LocalDate.now();
            LocalDate monthLater = ld.plusMonths( 12 );
            java.sql.Date sqlDate = java.sql.Date.valueOf( monthLater );

            packagePurchase.setClasses_remaining(0);
            packagePurchase.setExpiration_date(sqlDate);
            packagePurchase.setTotal_amount_paid(new BigDecimal(14));
            packagePurchase.setIs_monthly_renew(false);
            packagePurchaseDao.createPackagePurchase(packagePurchase);
            eventDao.registerForEvent(id,i,i);
        }

    }



    static class ClientEventWrapper {

        private int client_id;
        private int event_id;
        private int package_purchase_id;

        ClientEventWrapper(int client_id, int event_id, int package_purchase_id) {
            this.client_id = client_id;
            this.event_id = event_id;
            this.package_purchase_id = package_purchase_id;
        }

        @JsonProperty("client_id")
        int getClient_id() {
            return client_id;
        }

        void setClient_id(int client_id) {
            this.client_id = client_id;
        }

        @JsonProperty("event_id")
        public int getEvent_id() {
            return event_id;
        }

        public void setEvent_id(int event_id) {
            this.event_id = event_id;
        }

        @JsonProperty("package_purchase_id")
        public int getPackage_purchase_id() {
            return package_purchase_id;
        }

        public void setPackage_purchase_id(int package_purchase_id) {
            this.package_purchase_id = package_purchase_id;
        }
    }
    static class NewClientSignUp {
        private int event_id;
        private String first_name;
        private String last_name;
        private String email;

        public NewClientSignUp() {
        }

        public NewClientSignUp(int event_id, String first_name, String last_name) {
            this.event_id = event_id;
            this.first_name = first_name;
            this.last_name = last_name;
        }

        public NewClientSignUp(int event_id, String first_name, String last_name, String email) {
            this.event_id = event_id;
            this.first_name = first_name;
            this.last_name = last_name;
            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getEvent_id() {
            return event_id;
        }

        public void setEvent_id(int event_id) {
            this.event_id = event_id;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }
    }

}
