package com.sattvayoga.dao;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.model.terminal.Reader;
import com.stripe.param.checkout.SessionCreateParams;
import com.sattvayoga.dto.order.CheckoutItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
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

//    @Autowired
//    private SecretManagerService secretManagerService;


    @Override
    public Session createSession(List<CheckoutItemDTO> checkoutItemDTOList) throws StripeException {

        String successURL = baseURL + "payment/success";
        String failureURL = baseURL + "shoppingCart";


        Stripe.apiKey = apiKey;

        // TODO: If you want to deploy uncomment below

//        String retrievedValue = "";
//        try {
//           retrievedValue  = secretManagerService.getStripeKey();
//        } catch (Throwable e) {
//            System.out.println("Error retrieving stripe key");
//        }
//
//        Stripe.apiKey = retrievedValue.substring(20,retrievedValue.length()-2);

        List<SessionCreateParams.LineItem> sessionItemList = new ArrayList<>();

        Boolean foundSubscription = false;

        for(CheckoutItemDTO checkoutItemDTO: checkoutItemDTOList){

            if (checkoutItemDTO.isIs_monthly_renew()) {
                foundSubscription = true;
                // Handle subscription items
                sessionItemList.add(createSubscriptionLineItem(checkoutItemDTO));
            } else {
                // Handle one-time purchase items
                sessionItemList.add(createSessionLineItem(checkoutItemDTO));
            }
        }
        // (foundSubscription) ? SessionCreateParams.Mode.SUBSCRIPTION :SessionCreateParams.Mode.PAYMENT
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode((foundSubscription) ? SessionCreateParams.Mode.SUBSCRIPTION :SessionCreateParams.Mode.PAYMENT)
                .setCancelUrl(failureURL)
                .setSuccessUrl(successURL)
                .addAllLineItem(sessionItemList)
                .build();

        return Session.create(params);

    }


    private SessionCreateParams.LineItem createSessionLineItem(CheckoutItemDTO checkoutItemDTO) {
        return SessionCreateParams.LineItem.builder()
                .setPriceData(createPriceData(checkoutItemDTO))
                .setQuantity(Long.parseLong(String.valueOf(checkoutItemDTO.getQuantity())))
                .build();
    }

    private SessionCreateParams.LineItem createSubscriptionLineItem(CheckoutItemDTO checkoutItemDTO) {
        return SessionCreateParams.LineItem.builder()
                .setPrice((checkoutItemDTO.getProductName().equals("One Month Subscription")) ? "price_1NieCWBV0tnIJdW6WqIm2dti" : "price_1NkWmnBV0tnIJdW6ZGHtezqw")
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

//    @Override
//    public Reader createSimulatedReader() throws StripeException {
//
//        Stripe.apiKey = apiKey;
//
//        ReaderCreateParams params =
//                ReaderCreateParams.builder()
//                        .setLocation("tml_FPKfeAB7hN4NDL")
//                        .setRegistrationCode("simulated-wpe")
//                        .build();
//
//        Reader reader = Reader.create(params);
//
//        return reader;
//    }


//    private SessionCreateParams.Discount createDiscount() {
//        return SessionCreateParams.Discount.builder()
//                .setCoupon("BQnEKjVU")
//                .build();
//    }


//    @Override
//    public Session createSubscriptionSession(List<CheckoutSubscriptionItemDTO> checkoutItemDTOList) throws StripeException {
//
//        String successURL = baseURL + "payment/success";
//        String failureURL = baseURL + "payment/failed";
//
//        Stripe.apiKey = apiKey;
//
//        List<SessionCreateParams.LineItem> sessionItemList = new ArrayList<>();
//
//        for(CheckoutSubscriptionItemDTO checkoutItemDTO: checkoutItemDTOList){
//            sessionItemList.add(createSubscriptionSessionLineItem(checkoutItemDTO));
//        }
//
//        SessionCreateParams params = SessionCreateParams.builder()
//                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
//                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
//                .setCancelUrl(failureURL)
//                .setSuccessUrl(successURL)
//                .addAllLineItem(sessionItemList)
////                .addDiscount(createDiscount())
//                .build();
//
//        return Session.create(params);
//
//    }

//    private SessionCreateParams.LineItem createSubscriptionSessionLineItem(CheckoutSubscriptionItemDTO checkoutItemDTO) {
//        return SessionCreateParams.LineItem.builder()
//                .setPriceData(createSubscriptionPriceData(checkoutItemDTO))
//                .setQuantity(Long.parseLong(String.valueOf(checkoutItemDTO.getQuantity())))
//                .build();
//    }



//    private SessionCreateParams.LineItem.PriceData createSubscriptionPriceData(CheckoutSubscriptionItemDTO checkoutItemDTO) {
//        return SessionCreateParams.LineItem.PriceData.builder()
//                .setCurrency("usd")
//                .setUnitAmount(95L)
//                .setProductData(
//                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                                .setName("One Month Subscription")
//
//                                .build()
//                ).build();
//    }

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
