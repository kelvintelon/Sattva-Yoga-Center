package com.sattvayoga.dao;

import com.sattvayoga.model.PackageDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcPackageDetailsDao implements PackageDetailsDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcPackageDetailsDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate;}


    @Override
    public boolean createPackage(PackageDetails packageDetails) {
        String sql = "INSERT INTO package_details (description, package_cost, activation_date, expiration_date, " +
                "classes_remaining) VALUES " +
                "(?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql, packageDetails.getDescription(), packageDetails.getPackage_cost(),
                packageDetails.getActivation_date(),packageDetails.getExpiration_date(),
                packageDetails.getClasses_remaining()) == 1;
    }
}
