package com.sattvayoga.dao;

import com.sattvayoga.model.Sale;

import java.util.Set;

public interface SaleDao {

    int createSaleNoBatch(Sale sale);

    Set<Integer> getAllSaleIds();
    Sale getSaleBySaleId(int saleId);
}
