package com.sattvayoga.dao;

import com.sattvayoga.model.PackageDetails;

import java.util.List;

public interface PackageDetailsDao {

    boolean createPackage(PackageDetails packageDetails);

    List<PackageDetails> getAllPackages();

    boolean updatePackage(PackageDetails packageDetails);

    boolean deletePackage(int packageId);
}
