package com.sattvayoga.dao;

import com.sattvayoga.dto.order.ClientCheckoutDTO;
import com.sattvayoga.model.PaymentMethodOptions;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.sattvayoga.dto.order.CheckoutItemDTO;

import java.util.List;

public interface StripeDao {
    Session createSession(List<CheckoutItemDTO> checkoutItemDTOList) throws StripeException;
    String processClientPurchaseThroughAdmin(ClientCheckoutDTO clientCheckoutDTO) throws StripeException;
    List<PaymentMethodOptions> retrievePaymentMethodOptions(int clientId) throws StripeException;
    void addPaymentMethodThroughReader(int clientId) throws StripeException;
//    void addPaymentMethodManually(int clientId, PaymentMethodOptions paymentMethodOption) throws StripeException;
    //   Reader createSimulatedReader() throws StripeException;
//    Session createSubscriptionSession(List<CheckoutSubscriptionItemDTO> checkoutItemDTOList) throws StripeException;
}
