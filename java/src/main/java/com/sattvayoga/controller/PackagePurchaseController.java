package com.sattvayoga.controller;

import com.sattvayoga.dao.ClientDetailsDao;
import com.sattvayoga.dao.PackagePurchaseDao;
import com.sattvayoga.dao.UserDao;
import com.sattvayoga.model.ClassDetails;
import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.PackagePurchase;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@CrossOrigin
public class PackagePurchaseController {

    private PackagePurchaseDao packagePurchaseDao;
    private UserDao userDao;
    private ClientDetailsDao clientDetailsDao;

    public PackagePurchaseController(PackagePurchaseDao packagePurchaseDao, UserDao userDao, ClientDetailsDao clientDetailsDao) {
        this.packagePurchaseDao = packagePurchaseDao;
        this.userDao = userDao;
        this.clientDetailsDao = clientDetailsDao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/createPackagePurchase", method = RequestMethod.POST)
    public void createPackagePurchase(@RequestBody PackagePurchase packagePurchase) {

        packagePurchaseDao.createPackagePurchase(packagePurchase);
    }

    @RequestMapping(value= "/userPackagePurchaseList", method = RequestMethod.GET)
    public List<PackagePurchase> getAllUserPackagePurchase(Principal principal) throws SQLException {
        return packagePurchaseDao.getAllUserPackagePurchases(userDao.findIdByUsername(principal.getName()));
    }

    @RequestMapping(value= "/userPackagePurchaseListByUserId/{userId}", method = RequestMethod.GET)
    public List<PackagePurchase> getAllUserPackagePurchaseByUserId(@PathVariable int userId) throws SQLException {
        return packagePurchaseDao.getAllUserPackagePurchases(userId);
    }

    @RequestMapping(value= "/userPackagePurchaseListByClientId/{clientId}", method = RequestMethod.GET)
    public List<PackagePurchase> getAllUserPackagePurchaseByClientId(@PathVariable int clientId) throws SQLException {
        return packagePurchaseDao.getAllUserPackagePurchases(clientDetailsDao.findClientByClientId(clientId).getUser_id());
    }

    @GetMapping(path="/getAllSharedActiveQuantityPackages")
    public List<PackagePurchase> getAllSharedActiveQuantityPackages(Principal principal){
        int userId = userDao.findIdByUsername(principal.getName());
        int clientId = clientDetailsDao.findClientByUserId(userId).getClient_id();
        return packagePurchaseDao.getAllSharedActiveQuantityPackages(clientId);
    }

    @GetMapping(path="/getAllSharedActiveQuantityPackagesByClientId/{clientId}")
    public List<PackagePurchase> getAllSharedActiveQuantityPackagesByClientId(@PathVariable int clientId){
        return packagePurchaseDao.getAllSharedActiveQuantityPackages(clientId);
    }

    @RequestMapping(value= "/expirePackage", method = RequestMethod.PUT)
    public boolean expirePackage(@RequestBody PackagePurchase packagePurchase) {
        return packagePurchaseDao.expirePackage(packagePurchase);
    }

    @RequestMapping(value= "/decrement/{packagePurchaseId}", method = RequestMethod.PUT)
    public boolean decrementByOne(@PathVariable int packagePurchaseId){
        return packagePurchaseDao.decrementByOne(packagePurchaseId);
    }

    @RequestMapping(value= "/updatePackagePurchase", method = RequestMethod.PUT)
    public boolean updatePackage(@RequestBody PackagePurchase packagePurchase) {
        return packagePurchaseDao.updatePackage(packagePurchase);
    }

    @RequestMapping(value= "/increment/{packagePurchaseId}", method = RequestMethod.PUT)
    public boolean incrementByOne(@PathVariable int packagePurchaseId){
        return packagePurchaseDao.incrementByOne(packagePurchaseId);
    }
}
