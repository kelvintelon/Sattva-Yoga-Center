package com.sattvayoga.controller;

import com.sattvayoga.dao.FamilyDao;
import com.sattvayoga.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@CrossOrigin
public class FamilyController {

    private FamilyDao familyDao;

    public FamilyController(FamilyDao familyDao){
        this.familyDao = familyDao;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(path = "/getFamilyList", method = RequestMethod.GET)
    public List<Family> getAllFamilies() throws SQLException {
        return familyDao.getAllFamilies();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/addMultipleClientsForFamily", method = RequestMethod.POST)
    public void addMultipleClientsForFamily(@RequestBody List<ClientFamily> clientFamilyObjects) {
        for (int i = 0; i < clientFamilyObjects.size(); i++) {
            ClientFamily clientFamily = clientFamilyObjects.get(i);
            familyDao.addClientToFamily(clientFamily.getClient_id(),clientFamily.getFamily_id());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/addMultipleClientsToNewFamily", method = RequestMethod.POST)
    public void addMultipleClientsTlNewFamily(@RequestBody List<ClientFamily> clientFamilyObjects) {
        ClientFamily clientFamily = clientFamilyObjects.get(0);
        int newFamilyId = familyDao.createNewFamily(clientFamily.getClient_id(),clientFamily.getFamily_name());
        for (int i = 0; i < clientFamilyObjects.size(); i++) {
            clientFamily = clientFamilyObjects.get(i);
            familyDao.addClientToFamily(clientFamily.getClient_id(),newFamilyId);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/createFamily", method = RequestMethod.POST)
    public void createFamily(@RequestBody Family familyObj) {
        familyDao.createNewFamily(0,familyObj.getFamily_name());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/updateFamilyName", method = RequestMethod.PUT)
    public void updateFamilyName(@RequestBody Family updateFamily) {
        familyDao.updateFamilyName(updateFamily);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/deleteFamily/{familyId}", method = RequestMethod.DELETE)
    public void deleteFamily(@PathVariable int familyId) {
        Family familyToDelete = new Family();
        familyToDelete.setFamily_id(familyId);
        familyDao.deleteFamily(familyToDelete);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/getFamilyDetailsByFamilyId/{familyId}", method = RequestMethod.GET)
    public Family getFamilyDetailsByFamilyId (@PathVariable int familyId ) {

        return familyDao.getFamilyDetailsByFamilyId(familyId);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/removeFamilyMembersFromSelectedClients", method = RequestMethod.PUT)
    public void removeFamilyMembersFromSelectedClients(@RequestBody List<ClientDetails> clientDetailsList) {
        familyDao.removeFamilyMembersFromSelectedClients(clientDetailsList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/registerMultipleClientsForFamily", method = RequestMethod.POST)
    public void registerMultipleClientsForFamily(@RequestBody List<ClientDetails> clientDetailsList) {

        // retrieve the event object once
        Family family = familyDao.getFamilyDetailsByFamilyId(clientDetailsList.get(0).getFamily_id());


        for (int i = 0; i < clientDetailsList.size(); i++) {
            // current clientEvent object
            ClientDetails client = clientDetailsList.get(i);

            familyDao.addClientToFamily(client.getClient_id(), family.getFamily_id());
        }

    }


}
