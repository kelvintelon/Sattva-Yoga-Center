package com.sattvayoga.dao;

import com.sattvayoga.model.Sale;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.sql.Array;
import java.sql.SQLException;
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
        Sale sale = new Sale();

        String sql = "SELECT * FROM sales WHERE sale_id = ?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql,saleId);
        if (results.next()) {
            sale = mapRowToSale(results);
        }
        return sale;
    }

    private Sale mapRowToSale(SqlRowSet rs) {
        Sale sale = new Sale();
        sale.setSale_id(rs.getInt("sale_id"));
        Object newObject = rs.getObject("packages_purchased_array");

        if (newObject instanceof Array) {
            Array tempArray = (Array) newObject;
            Object[] tempObjectArray = new Object[0];
            try {
                tempObjectArray = (Object[]) tempArray.getArray();
            } catch (SQLException e) {
                System.out.println("Error retrieving packages purchased array from Sale ID");
            }
            int[] packagesPurchasedArray = new int[tempObjectArray.length];
            for (int i = 0; i < tempObjectArray.length; i++) {
                packagesPurchasedArray[i] = Integer.valueOf(tempObjectArray[i].toString());
            }
            sale.setPackages_purchased_array(packagesPurchasedArray);
        }
        sale.setClient_id(rs.getInt("client_id"));
        sale.setBatch_number(rs.getInt("batch_number"));
        return sale;
    }
}
