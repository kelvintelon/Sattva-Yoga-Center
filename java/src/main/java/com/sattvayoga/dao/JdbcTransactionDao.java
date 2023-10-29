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
        return 0;
    }

    @Override
    public List<Transaction> getTransactionsBySaleId(int saleId) {
        return null;
    }
}
