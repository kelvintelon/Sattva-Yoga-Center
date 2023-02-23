package com.sattvayoga.controller;

import com.sattvayoga.dao.PackagePurchaseDao;
import com.sattvayoga.dao.UserDao;
import com.sattvayoga.model.ClassDetails;
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

    public PackagePurchaseController(PackagePurchaseDao packagePurchaseDao, UserDao userDao) {
        this.packagePurchaseDao = packagePurchaseDao;
        this.userDao = userDao;
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
    public List<PackagePurchase> getAllUserPackagePurchase(@PathVariable int userId) throws SQLException {
        return packagePurchaseDao.getAllUserPackagePurchases(userId);
    }

    @RequestMapping(value= "/expirePackage", method = RequestMethod.PUT)
    public void expirePackage(@RequestBody PackagePurchase packagePurchase) {
        packagePurchaseDao.expirePackage(packagePurchase);
    }

    @RequestMapping(value= "/decrement/{packagePurchaseId}", method = RequestMethod.PUT)
    public void decrementByOne(@PathVariable int packagePurchaseId){
        packagePurchaseDao.decrementByOne(packagePurchaseId);
    }
}
