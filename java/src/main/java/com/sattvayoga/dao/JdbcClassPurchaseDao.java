package com.sattvayoga.dao;

import com.sattvayoga.model.ClassPurchase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcClassPurchaseDao implements ClassPurchaseDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcClassPurchaseDao(JdbcTemplate jdbcTemplate) {this.jdbcTemplate = jdbcTemplate;}

    @Override
    public boolean createClassPurchase(ClassPurchase classPurchase) {
        String sql = "INSERT INTO class_purchase (client_id, classes_purchased, date_purchased, activation_date, " +
                "expiration_date, expired, classes_remaining, amount_paid, cost_per_class, is_monthly_renew, " +
                "next_renew_date, notes, date_of_entry, exclude_from_totals) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, classPurchase.getClient_id(), classPurchase.getClasses_purchased(),
                classPurchase.getDate_purchased(), classPurchase.getActivation_date(), classPurchase.getExpiration_date(),
                classPurchase.isExpired(), classPurchase.getClasses_remaining(), classPurchase.getAmount_paid(),
                classPurchase.getCost_per_class(), classPurchase.isIs_monthly_renew(), classPurchase.getNext_renew_date(),
                classPurchase.getNotes(), classPurchase.getDate_of_entry(),
                classPurchase.isExclude_from_totals()) == 1;
    }
}
