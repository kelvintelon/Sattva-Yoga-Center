package com.sattvayoga.controller;

import com.sattvayoga.dao.PackagePurchaseDao;
import com.sattvayoga.model.PackagePurchase;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("isAuthenticated()")
@RestController
@CrossOrigin
public class PackagePurchaseController {

    private PackagePurchaseDao packagePurchaseDao;

    public PackagePurchaseController(PackagePurchaseDao packagePurchaseDao) {
        this.packagePurchaseDao = packagePurchaseDao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/createPackagePurchase", method = RequestMethod.POST)
    public void createPackagePurchase(@RequestBody PackagePurchase packagePurchase) {

        packagePurchaseDao.createPackagePurchase(packagePurchase);
    }
}
