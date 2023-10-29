package com.sattvayoga.dao;

import com.sattvayoga.model.Sale;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcSaleDao implements SaleDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSaleDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int createSaleNoBatch(Sale sale) {
        String sql = "INSERT INTO sales (packages_purchased_array) VALUES (?) RETURNING sale_id;";

        return jdbcTemplate.queryForObject(sql, Integer.class, sale.getPackages_purchased_array());
    }

    @Override
    public Sale getSaleBySaleId(int saleId) {
        return null;
    }
}
