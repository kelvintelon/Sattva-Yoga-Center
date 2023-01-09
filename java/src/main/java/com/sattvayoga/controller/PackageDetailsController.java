package com.sattvayoga.controller;

import com.sattvayoga.dao.PackageDetailsDao;
import com.sattvayoga.model.ClassDetails;
import com.sattvayoga.model.PackageDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin
public class PackageDetailsController {

    private PackageDetailsDao packageDetailsDao;

    public PackageDetailsController(PackageDetailsDao packageDetailsDao) {
        this.packageDetailsDao = packageDetailsDao;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/createPackage", method = RequestMethod.POST)
    public void createPackage(@RequestBody PackageDetails packageDetails) {

        // should we have exceptions if the package is already created
        // (an exception that means they are already inside the package details table)

        packageDetailsDao.createPackage(packageDetails);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value= "/packageList", method = RequestMethod.GET)
    public List<PackageDetails> getAllPackages() {
        return packageDetailsDao.getAllPackages();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value= "/updatePackage", method = RequestMethod.PUT)
    public void updatePackage(@RequestBody PackageDetails packageDetails) {
        packageDetailsDao.updatePackage(packageDetails);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/deletePackage/{packageId}", method = RequestMethod.DELETE)
    public void deleteClass (@PathVariable int packageId) {
        packageDetailsDao.deletePackage(packageId);
    }

    @RequestMapping(value= "/publicPackageList", method = RequestMethod.GET)
    public List<PackageDetails> getAllPublicPackages() {
        return packageDetailsDao.getAllPublicPackages();
    }

}
