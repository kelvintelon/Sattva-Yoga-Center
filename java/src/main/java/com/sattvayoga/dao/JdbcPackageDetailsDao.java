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
        String sql = "INSERT INTO package_details (description, package_cost, cost_per_class) VALUES " +
                "(?, ?, ?)";

        return jdbcTemplate.update(sql, packageDetails.getDescription(), packageDetails.getPackage_cost(),
                packageDetails.getCost_per_class()) == 1;
    }
}
