package com.sattvayoga.dao;

import com.sattvayoga.model.Sale;

public interface SaleDao {

    int createSaleNoBatch(Sale sale);

    Sale getSaleBySaleId(int saleId);
}
