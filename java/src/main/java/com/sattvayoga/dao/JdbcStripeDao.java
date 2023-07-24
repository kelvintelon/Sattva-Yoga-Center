package com.sattvayoga.dao;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.sattvayoga.dto.order.CheckoutItemDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcStripeDao implements StripeDao{

    @Value("${BASE_URL}")
    private String baseURL;
    @Value("${STRIPE_SECRET_KEY}")
    private String apiKey;
    @Override
    public Session createSession(List<CheckoutItemDTO> checkoutItemDTOList) throws StripeException {

        String successURL = baseURL + "payment/success";
        String failureURL = baseURL + "payment/failed";

        Stripe.apiKey = apiKey;

        List<SessionCreateParams.LineItem> sessionItemList = new ArrayList<>();

        for(CheckoutItemDTO checkoutItemDTO: checkoutItemDTOList){
            sessionItemList.add(createSessionLineItem(checkoutItemDTO));
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCancelUrl(failureURL)
                .setSuccessUrl(successURL)
                .addAllLineItem(sessionItemList)
//                .addDiscount(createDiscount())
                .build();

        return Session.create(params);

    }

//    private SessionCreateParams.Discount createDiscount() {
//        return SessionCreateParams.Discount.builder()
//                .setCoupon("BQnEKjVU")
//                .build();
//    }

    private SessionCreateParams.LineItem createSessionLineItem(CheckoutItemDTO checkoutItemDTO) {
        return SessionCreateParams.LineItem.builder()
                .setPriceData(createPriceData(checkoutItemDTO))
                .setQuantity(Long.parseLong(String.valueOf(checkoutItemDTO.getQuantity())))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(CheckoutItemDTO checkoutItemDTO) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("usd")
                .setUnitAmount((long)(checkoutItemDTO.getPrice()*100))
                .setProductData(
                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(checkoutItemDTO.getProductName())
                                .build()
                ).build();
    }

//    @Value("${BASE_URL}")
//    private String baseURL;
//    @Value("${STRIPE_SECRET_KEY}")
//    private String apiKey;
//    @Override
//    public Session createSession(List<CheckoutItemDTO> checkoutItemDTOList) throws StripeException {
//
//        String successURL = baseURL + "payment/success";
//        String failureURL = baseURL + "payment/failed";
//
//        Stripe.apiKey = apiKey;
//        List<Object> lineItems = new ArrayList<>();
//        Map<String, Object> lineItem1 = new HashMap<>();
//        lineItem1.put("price", "price_1N6c2YHIFPdFs4yB3HUeqY5I");
//        lineItem1.put("quantity", 2);
//        lineItems.add(lineItem1);
//        Map<String, Object> lineItem2 = new HashMap<>();
//        lineItem2.put("price", "price_1N6c2YHIFPdFs4yB3HUeqY5I");
//        lineItem2.put("quantity", 1);
//        lineItems.add(lineItem2);
//        Map<String, Object> lineItem3 = new HashMap<>();
//        lineItem3.put("price", "price_1N6cHnHIFPdFs4yBxuAIkPU7");
//        lineItem3.put("quantity", 2);
//        lineItems.add(lineItem3);
//
//
//        Map<String, Object> params = new HashMap<>();
//        params.put(
//                "success_url",
//                successURL
//        );
//        params.put("line_items", lineItems);
//        params.put("mode", "payment");
//
//        Session session = Session.create(params);
//        return session;
//    }
}
