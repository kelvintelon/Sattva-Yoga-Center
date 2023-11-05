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
                "classes_amount, package_duration, unlimited, is_visible_online, is_recurring, active) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql, packageDetails.getDescription(), packageDetails.getPackage_cost(),
                packageDetails.getClasses_amount(), packageDetails.getPackage_duration(),
                packageDetails.isUnlimited(), packageDetails.isIs_visible_online(), packageDetails.isIs_recurring(), packageDetails.isActive()) == 1;
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
    public PackageDetails findPackageByPackageName(String packageName) {
        String sql = "SELECT * FROM package_details WHERE description = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, packageName);
        PackageDetails packageDetails = new PackageDetails();
        if (result.next()) {
            packageDetails = mapRowToPackage(result);
        }
        return packageDetails;
    }

    @Override
    public PackageDetails findPackageBySubscriptionDuration(int subscriptionDuration) {
        String sql = "SELECT * FROM package_details WHERE is_recurring = true AND unlimited = true AND package_duration = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, subscriptionDuration);
        PackageDetails packageDetails = new PackageDetails();
        if (result.next()) {
            packageDetails = mapRowToPackage(result);
        }
        return packageDetails;
    }

   @Override
   public boolean updatePackage(PackageDetails packageDetails) {
        String sql = "UPDATE package_details SET package_id = ? , " +
                "description = ? , " +
                "package_cost = ? , " +
                "classes_amount = ? , " +
                "package_duration = ? , " +
                "unlimited = ? , " +
                "is_visible_online = ? , " +
                "active = ? , " +
                "is_recurring = ? " +
                "WHERE package_id = ?";
        return jdbcTemplate.update(sql, packageDetails.getPackage_id(), packageDetails.getDescription(),
                packageDetails.getPackage_cost(), packageDetails.getClasses_amount(),
                packageDetails.getPackage_duration(), packageDetails.isUnlimited(),
                packageDetails.isIs_visible_online(), packageDetails.isActive(), packageDetails.isIs_recurring(), packageDetails.getPackage_id())==1;
   }

    @Override
    public boolean deletePackage(int packageId) {
        String sql = "BEGIN TRANSACTION;\n" +
                "\n" +
                "DELETE FROM package_details\n" +
                "WHERE package_id = ?;\n" +
                "\n" +
                "COMMIT TRANSACTION;";
        return jdbcTemplate.update(sql, packageId)==1;
    }

    @Override
    public List<PackageDetails> getAllPublicPackages() {
        List<PackageDetails> allPackages = new ArrayList<>();

        String sql = "SELECT * FROM package_details WHERE is_visible_online = TRUE AND active = TRUE;";
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
        if (rs.getInt("package_duration") != 0) {
            packageDetails.setPackage_duration(rs.getInt("package_duration"));
        }
        packageDetails.setUnlimited(rs.getBoolean("unlimited"));
        packageDetails.setIs_visible_online(rs.getBoolean("is_visible_online"));
        packageDetails.setIs_recurring(rs.getBoolean("is_recurring"));
        packageDetails.setActive(rs.getBoolean("active"));
        return packageDetails;
    }

}
