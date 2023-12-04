package com.sattvayoga.controller;

import com.sattvayoga.dao.FamilyDao;
import com.sattvayoga.model.ClientFamily;
import com.sattvayoga.model.Family;
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
    @RequestMapping(value = "/updateFamilyName", method = RequestMethod.PUT)
    public void updateFamilyName(@RequestBody Family updateFamily) {
        familyDao.updateFamilyName(updateFamily);
    }



}
