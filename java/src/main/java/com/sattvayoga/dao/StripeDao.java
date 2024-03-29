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
    void addPaymentMethodManually(int clientId, PaymentMethodOptions paymentMethodOption) throws StripeException;
    void updateCustomerEmail(String customerId, String newEmail);
    void sendEmailReceipt(ClientCheckoutDTO clientCheckoutDTO, String packagesBeingBoughtForEmail, int saleId, String saleDate, String firstName, String subject, String subTotal, String tax, String total, String usedPaymentTypes);
    String getCustomerIdString(int retrievedClientId);
    //   Reader createSimulatedReader() throws StripeException;
//    Session createSubscriptionSession(List<CheckoutSubscriptionItemDTO> checkoutItemDTOList) throws StripeException;
}
