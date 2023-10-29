package com.sattvayoga.dao;

import com.sattvayoga.model.Transaction;

import java.util.List;

public interface TransactionDao {

    int createTransaction(Transaction transaction);

    List<Transaction> getTransactionsBySaleId(int saleId);


}
