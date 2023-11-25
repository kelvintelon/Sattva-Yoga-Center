package com.sattvayoga.dao;

import com.sattvayoga.model.Sale;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class JdbcSaleDao implements SaleDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSaleDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int createSaleNoBatch(Sale sale) {
        String sql = "INSERT INTO sales (client_id, packages_purchased_array) VALUES (?, ?) RETURNING sale_id;";

        return jdbcTemplate.queryForObject(sql, Integer.class, sale.getClient_id(), sale.getPackages_purchased_array());
    }

    @Override
    public Set<Integer> getAllSaleIds() {
        Set<Integer> setOfSaleIds = new HashSet<>();

        String sql = "Select sale_id FROM sales";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            int saleId = results.getInt("sale_id");
            setOfSaleIds.add(saleId);
        }
        return setOfSaleIds;
    }

    @Override
    public Sale getSaleBySaleId(int saleId) {
        return null;
    }
}
