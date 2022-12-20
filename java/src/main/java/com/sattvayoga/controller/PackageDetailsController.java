package com.sattvayoga.controller;

import com.sattvayoga.dao.PackageDetailsDao;
import com.sattvayoga.model.PackageDetails;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class PackageDetailsController {

    private PackageDetailsDao packageDetailsDao;

    public PackageDetailsController(PackageDetailsDao packageDetailsDao) {
        this.packageDetailsDao = packageDetailsDao;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/createPackage", method = RequestMethod.POST)
    public void createPackage(@RequestBody PackageDetails packageDetails) {

        // should we have exceptions if the package is already created
        // (an exception that means they are already inside the package details table)

        packageDetailsDao.createPackage(packageDetails);
    }
}
