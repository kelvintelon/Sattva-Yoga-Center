package com.sattvayoga.dao;

import com.google.gson.Gson;
import com.sattvayoga.dto.order.ClientCheckoutDTO;
import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.PackageDetails;
import com.stripe.Stripe;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.model.terminal.Reader;
import com.stripe.param.CustomerListPaymentMethodsParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.sattvayoga.dto.order.CheckoutItemDTO;
import com.stripe.param.terminal.ReaderProcessPaymentIntentParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class JdbcStripeDao implements StripeDao{

    @Value("${BASE_URL}")
    private String baseURL;

    @Value("${STRIPE_SECRET_KEY}")
    private String apiKey;

    // TODO: If you want to deploy uncomment below
//    @Autowired
//    private SecretManagerService secretManagerService;

    @Autowired
    private ClientDetailsDao clientDetailsDao;

    @Override
    public Session createSession(List<CheckoutItemDTO> checkoutItemDTOList) throws StripeException {

        String successURL = baseURL + "clientPackageManagement";
        String failureURL = baseURL + "shoppingCart";

        Stripe.apiKey = apiKey;

        // TODO: If you want to deploy uncomment below and comment out the Stripe key above

//        String retrievedValue = "";
//        try {
//           retrievedValue  = secretManagerService.getStripeKey();
//        } catch (Throwable e) {
//            System.out.println("Error retrieving stripe key");
//        }
//
//        Stripe.apiKey = retrievedValue.substring(20,retrievedValue.length()-2);

        System.out.println(Stripe.API_VERSION);

        //  Find customer ID for Client, create one if needed.
        int retrievedClientId = checkoutItemDTOList.get(0).getClient_id();

        ClientDetails clientDetails = clientDetailsDao.findClientByClientId(retrievedClientId);

        String customer_id = "";

        if (clientDetails.getCustomer_id() != null) {
            customer_id = clientDetails.getCustomer_id();
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("name", clientDetails.getFirst_name() + " " + clientDetails.getLast_name());
            Customer customer = Customer.create(params);

            customer_id = customer.getId();

            // Save customer_id into DB
            clientDetailsDao.updateClientCustomerId(retrievedClientId, customer_id);
        }

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
                .setCustomer(customer_id)
                .build();

        return Session.create(params);

    }

    @Override
    public String processClientPurchaseThroughAdmin(ClientCheckoutDTO clientCheckoutDTO) throws StripeException {

        Stripe.apiKey = apiKey;

        //TODO: Comment out the above when you are reader to deploy
        // 1. Uncomment and Pull in Secrets Manager to grab API Key

//        String retrievedValue = "";
//        try {
//           retrievedValue  = secretManagerService.getStripeKey();
//        } catch (Throwable e) {
//            System.out.println("Error retrieving stripe key");
//        }
//
//        Stripe.apiKey = retrievedValue.substring(20,retrievedValue.length()-2);

        //TODO:
        // 1. Replace this simulated reader ID ("tmr_FPKgUQOJ7fdmwi") with physical reader ID
        Reader readerResource = Reader.retrieve("tmr_FPKgUQOJ7fdmwi");

        //TODO:
        // 1. Pull in all data
        // 2. Set the email for the receipt?
        PackageDetails firstPackageDetails = clientCheckoutDTO.getListOfPackages().get(0);
        if (firstPackageDetails.isIs_recurring() && firstPackageDetails.isIs_subscription()) {

            Customer customer = Customer.retrieve("cus_Oaz2g2UAJU57rG"); // no default payment
            Customer customerWithDefault = Customer.retrieve("cus_Oaz92kcmvYhpTn");



            // By setting the customer ID for creating a subscription. The customer's saved payment method forgoes any need to tap the card
            List<Object> items = new ArrayList<>();
            Map<String, Object> item1 = new HashMap<>();
            item1.put(
                    "price",
                    (firstPackageDetails.getDescription().equals("One Month Subscription")) ? "price_1NieCWBV0tnIJdW6WqIm2dti" : "price_1NkWmnBV0tnIJdW6ZGHtezqw"
            );
            items.add(item1);
            Map<String, Object> params = new HashMap<>();
            params.put("customer", "cus_Oaz92kcmvYhpTn");
            params.put("items", items);

            Subscription subscription =
                    Subscription.create(params);

            Invoice invoice = Invoice.retrieve(subscription.getLatestInvoice());

            PaymentIntent paymentIntent = PaymentIntent.retrieve(invoice.getPaymentIntent());

            return "";
        } else {
            // TODO: Replace the following customer ID with a real one for this specific client.
            Customer customer =
                    Customer.retrieve("cus_Oaz92kcmvYhpTn");

            CustomerListPaymentMethodsParams customerListPaymentMethodsParams =
                    CustomerListPaymentMethodsParams.builder()
                            .setType(CustomerListPaymentMethodsParams.Type.CARD)
                            .build();

            PaymentMethodCollection paymentMethods =
                    customer.listPaymentMethods(customerListPaymentMethodsParams);

            String paymentMethodIdForFirstIndex = paymentMethods.getData().get(0).getId();

            String returnUrl = baseURL + "clientPackageManagement";

            //TODO: If they do not want to use a saved payment and plan to use a physical card, set everything else except:
            // 1. Do not set PaymentMethod string
            // 2. Do not set Confirm boolean to true
            // 3. Do not set return url string

            //TODO: If they want to use a saved payment and DO NOT plan to use a physical card, set everything else except:
            // 1. Do not set AddPaymentMethodType to "card_present"

            PaymentIntentCreateParams paymentIntentCreateParams =
                    PaymentIntentCreateParams.builder()
                            .setCurrency("usd")
                            .setCustomer("cus_Oaz92kcmvYhpTn")
                            .setAmount((long)clientCheckoutDTO.getTotal()*100)
                            .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
                            .addPaymentMethodType("card_present")
//                            .setPaymentMethod(paymentMethodIdForFirstIndex)
//                            .setReturnUrl(returnUrl)
//                            .setConfirm(true)
                            .build();

            // This creates a payment intent
            PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentCreateParams);

            // This creates a parameter to pass in through the reader using the payment intent ID
            ReaderProcessPaymentIntentParams params = ReaderProcessPaymentIntentParams.builder().setPaymentIntent(paymentIntent.getId()).build();

            int attempt = 0;
            int tries = 3;
            while (true) {
                attempt++;
                try {
                    // This passes the params into the reader
                    readerResource.processPaymentIntent(params);

                    //TODO: Comment out the following line for a physical reader
                    // 1. This is for a simulated tap card
                    //Reader reader = readerResource.getTestHelpers().presentPaymentMethod();

                    // TODO: You can return any string
                    return readerResource.toJson();
                }
                catch (InvalidRequestException e) {
                    switch (e.getCode()) {
                        case "terminal_reader_timeout":
                            // Temporary networking blip, automatically retry a few times.
                            if (attempt == tries) {
                                return e.getStripeError().toJson();
                            }
                            break;
                        case "terminal_reader_offline":
                            // Reader is offline and won't respond to API requests. Make sure the reader is
                            // powered on and connected to the internet before retrying.
                            return e.getStripeError().toJson();
                        case "terminal_reader_busy":
                            // Reader is currently busy processing another request, installing updates or
                            // changing settings. Remember to disable the pay button in your point-of-sale
                            // application while waiting for a reader to respond to an API request.
                            return e.getStripeError().toJson();
                        case "intent_invalid_state":
                            // Check PaymentIntent status because it's not ready to be processed. It might
                            // have been already successfully processed or canceled.
                            PaymentIntent paymentIntentCase = PaymentIntent.retrieve(paymentIntent.getId());
                            Map<String, String> errorResponse = Collections.singletonMap("error",
                                    "PaymentIntent is already in " + paymentIntentCase.getStatus() + " state.");
                            return new Gson().toJson(errorResponse);

                        default:
                            return e.getStripeError().toJson();
                    }
                }
//              finally {
//                   return "";
//               }
            }
        }


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


}
