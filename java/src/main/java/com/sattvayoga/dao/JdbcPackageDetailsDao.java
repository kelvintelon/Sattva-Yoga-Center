package com.sattvayoga.dao;

import com.sattvayoga.model.PackageDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcPackageDetailsDao implements PackageDetailsDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPackageDetailsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public boolean createPackage(PackageDetails packageDetails) {
        String sql = "INSERT INTO package_details (description, package_cost, " +
                "classes_amount, subscription_duration, is_subscription, is_only_online) VALUES " +
                "(?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql, packageDetails.getDescription(), packageDetails.getPackage_cost(),
                packageDetails.getClasses_amount(), packageDetails.getSubscription_duration(),
                packageDetails.isIs_subscription(), packageDetails.isIs_only_online()) == 1;
    }


    @Override
    public List<PackageDetails> getAllPackages() {
        List<PackageDetails> allPackages = new ArrayList<>();

        String sql = "SELECT * FROM package_details;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while (result.next()) {
            PackageDetails packageDetails = mapRowToPackage(result);


            allPackages.add(packageDetails);
        }

        return allPackages;
    }

   @Override
   public boolean updatePackage(PackageDetails packageDetails) {
        String sql = "UPDATE package_details SET package_id = ? , " +
                "description = ? , " +
                "package_cost = ? , " +
                "classes_amount = ? , " +
                "subscription_duration = ? , " +
                "is_subscription = ? , " +
                "is_only_online = ? " +
                "WHERE package_id = ?";
        return jdbcTemplate.update(sql, packageDetails.getPackage_id(), packageDetails.getDescription(),
                packageDetails.getPackage_cost(), packageDetails.getClasses_amount(),
                packageDetails.getSubscription_duration(), packageDetails.isIs_subscription(),
                packageDetails.isIs_only_online(), packageDetails.getPackage_id())==1;
   }

    @Override
    public boolean deletePackage(int packageId) {
        String sql = "BEGIN TRANSACTION;\n" +
                "\n" +
                "DELETE FROM package_purchase \n" +
                "WHERE package_purchase.package_id = ?;\n" +
                "\n" +
                "DELETE FROM package_details\n" +
                "WHERE package_id = ?;\n" +
                "\n" +
                "COMMIT TRANSACTION;";
        return jdbcTemplate.update(sql, packageId, packageId)==1;
    }

    @Override
    public List<PackageDetails> getAllPublicPackages() {
        List<PackageDetails> allPackages = new ArrayList<>();

        String sql = "SELECT * FROM package_details WHERE is_only_online = TRUE;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while (result.next()) {
            PackageDetails packageDetails = mapRowToPackage(result);


            allPackages.add(packageDetails);
        }

        return allPackages;
    }

    private PackageDetails mapRowToPackage(SqlRowSet rs) {
        PackageDetails packageDetails = new PackageDetails();
        packageDetails.setPackage_id(rs.getInt("package_id"));
        packageDetails.setDescription(rs.getString("description"));
        packageDetails.setPackage_cost(rs.getBigDecimal("package_cost"));
        if (rs.getInt("classes_amount") != 0) {
            packageDetails.setClasses_amount(rs.getInt("classes_amount"));
        }
        if (rs.getInt("subscription_duration") != 0) {
            packageDetails.setSubscription_duration(rs.getInt("subscription_duration"));
        }
        packageDetails.setIs_subscription(rs.getBoolean("is_subscription"));
        packageDetails.setIs_only_online(rs.getBoolean("is_only_online"));

        return packageDetails;
    }

}
