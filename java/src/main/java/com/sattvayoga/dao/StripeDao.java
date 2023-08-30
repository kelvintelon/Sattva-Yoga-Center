package com.sattvayoga.dao;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.sattvayoga.dto.order.CheckoutItemDTO;

import java.util.List;

public interface StripeDao {
    Session createSession(List<CheckoutItemDTO> checkoutItemDTOList) throws StripeException;
//    Session createSubscriptionSession(List<CheckoutSubscriptionItemDTO> checkoutItemDTOList) throws StripeException;
}
