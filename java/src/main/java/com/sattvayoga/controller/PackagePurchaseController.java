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

    @RequestMapping(value= "/expirePackage", method = RequestMethod.PUT)
    public boolean expirePackage(@RequestBody PackagePurchase packagePurchase) {
        return packagePurchaseDao.expirePackage(packagePurchase);
    }

    @RequestMapping(value= "/decrement/{packagePurchaseId}", method = RequestMethod.PUT)
    public boolean decrementByOne(@PathVariable int packagePurchaseId){
        return packagePurchaseDao.decrementByOne(packagePurchaseId);
    }

    @RequestMapping(value= "/increment/{packagePurchaseId}", method = RequestMethod.PUT)
    public boolean incrementByOne(@PathVariable int packagePurchaseId){
        return packagePurchaseDao.incrementByOne(packagePurchaseId);
    }
}
