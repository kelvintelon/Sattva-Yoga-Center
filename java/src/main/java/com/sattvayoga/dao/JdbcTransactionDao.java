package com.sattvayoga.dao;

import com.sattvayoga.model.Transaction;
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
        return jdbcTemplate.queryForObject(sql, Integer.class, transaction.getSale_id(), transaction.getClient_id(),
                transaction.getPayment_type(), transaction.getPayment_amount(), transaction.getGift_code());
    }

    @Override
    public List<Transaction> getTransactionsBySaleId(int saleId) {
        return null;
    }
}
