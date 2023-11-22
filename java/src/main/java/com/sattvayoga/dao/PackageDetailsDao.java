package com.sattvayoga.dao;

import com.sattvayoga.model.PackageDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PackageDetailsDao {

    void createPackage(PackageDetails packageDetails);

    List<PackageDetails> getAllPackages();

    void updateSinglePackage(PackageDetails packageDetails);

    void updatePackages(PackageDetails packageDetails);

    boolean deletePackage(int packageId);

    List<PackageDetails> getAllPublicPackages();

    PackageDetails findPackageByPackageName(String packageName);

    PackageDetails findPackageBySubscriptionDuration(int subscriptionDuration);

    void uploadPackageCsv(MultipartFile multipartFile);

}
