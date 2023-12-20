package com.sattvayoga.dao;

import com.sattvayoga.model.CustomException;
import com.sattvayoga.model.Transaction;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JdbcTransactionDao implements TransactionDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcTransactionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int createTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (sale_id, client_id, payment_type, payment_amount, gift_code) " +
                "VALUES (?,?,?,?, ?) RETURNING transaction_id";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, transaction.getSale_id(), transaction.getClient_id(),
                    transaction.getPayment_type(), transaction.getPayment_amount(), transaction.getGift_code());
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to create transaction.");
        }
    }

    @Override
    public List<Transaction> getTransactionsBySaleId(int saleId) {
        return null;
    }
}
