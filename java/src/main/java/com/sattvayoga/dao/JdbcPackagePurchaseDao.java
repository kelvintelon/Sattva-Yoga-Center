package com.sattvayoga.dao;

import com.sattvayoga.model.PackagePurchase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcPackagePurchaseDao implements PackagePurchaseDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPackagePurchaseDao(JdbcTemplate jdbcTemplate) {this.jdbcTemplate = jdbcTemplate;}

    @Override
    public boolean createPackagePurchase(PackagePurchase packagePurchase) {
        String sql = "INSERT INTO package_purchase ( client_id, date_purchased, package_id, is_expired, " +
                "total_amount_paid,  is_monthly_renew,  discount) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, packagePurchase.getClient_id(), packagePurchase.getDate_purchased(),
                packagePurchase.getPackage_id(), packagePurchase.isIs_expired(),packagePurchase.getTotal_amount_paid(),
                packagePurchase.isIs_monthly_renew(),packagePurchase.getDiscount())==1;

    }
}
