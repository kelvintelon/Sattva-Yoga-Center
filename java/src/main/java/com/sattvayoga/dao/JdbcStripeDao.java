package com.sattvayoga.dao;

import com.google.gson.Gson;
import com.sattvayoga.dto.order.ClientCheckoutDTO;
import com.sattvayoga.model.*;
import com.stripe.Stripe;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.model.terminal.Reader;
import com.stripe.param.CustomerListPaymentMethodsParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.SubscriptionScheduleCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.sattvayoga.dto.order.CheckoutItemDTO;
import com.stripe.param.terminal.ReaderProcessPaymentIntentParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class JdbcStripeDao implements StripeDao {

    @Value("${BASE_URL}")
    private String baseURL;

    @Value("${STRIPE_SECRET_KEY}")
    private String apiKey;

    @Autowired
    PackageDetailsDao packageDetailsDao;

    // TODO: If you want to deploy uncomment below
//    @Autowired
//    private SecretManagerService secretManagerService;

    @Autowired
    private ClientDetailsDao clientDetailsDao;

    @Override
    public Session createSession(List<CheckoutItemDTO> checkoutItemDTOList) throws StripeException {

        String successURL = baseURL + "clientPackageManagement";
        String failureURL = baseURL + "shoppingCart";

        SetStripeKey();

        //  Find customer ID for Client, create one if needed.
        int retrievedClientId = checkoutItemDTOList.get(0).getClient_id();

        String customer_id = getCustomerIdString(retrievedClientId);

        List<SessionCreateParams.LineItem> sessionItemList = new ArrayList<>();

        Boolean foundSubscription = false;

        for (CheckoutItemDTO checkoutItemDTO : checkoutItemDTOList) {

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
                .setMode((foundSubscription) ? SessionCreateParams.Mode.SUBSCRIPTION : SessionCreateParams.Mode.PAYMENT)
                .setCancelUrl(failureURL)
                .setSuccessUrl(successURL)
                .addAllLineItem(sessionItemList)
                .setCustomer(customer_id)
                .build();

        return Session.create(params);

    }

    @Override
    public String processClientPurchaseThroughAdmin(ClientCheckoutDTO clientCheckoutDTO) throws StripeException {

        SetStripeKey();

        Reader readerResource = getReader();

        boolean discountNeeded = determineDiscountNeeded(clientCheckoutDTO);

        //TODO:
        // 1. Pull in all data
        // 2. Set the email for the receipt?
        PackageDetails firstPackageDetails = clientCheckoutDTO.getListOfPackages().get(0);
        if (firstPackageDetails.isIs_recurring() && firstPackageDetails.isIs_subscription()) {


            // Compare the renewal Date to today's date to see if they are the same.
            // If they are not then follow through changing the subscription scheduling
            if (!LocalDate.now().toString().equals(clientCheckoutDTO.getRenewalDate())) {

                Customer customer = getCustomerObjectByClientId(clientCheckoutDTO.getClient_id());

                LocalDate renewalDate = LocalDate.parse(clientCheckoutDTO.getRenewalDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                ZoneId zone = ZoneId.of("America/New_York");
                LocalDateTime renewalDateLDT = renewalDate.atStartOfDay();
                ZoneOffset zoneOffSet = zone.getRules().getOffset(renewalDateLDT);

                if (!discountNeeded) {
                    // Regular subscription scheduling with no discount

                    // Gift card used logic for metadata
                    Map<String, String> metaDataMap = new HashMap<>();
                    usedGiftCardInMetaData(clientCheckoutDTO, metaDataMap);

                    SubscriptionScheduleCreateParams params =
                            SubscriptionScheduleCreateParams.builder()
                                    .setCustomer(customer.getId())
                                    .setStartDate(renewalDate.toEpochSecond(LocalTime.from(renewalDateLDT), zoneOffSet))
                                    .setEndBehavior(SubscriptionScheduleCreateParams.EndBehavior.RELEASE)
                                    .setDefaultSettings(SubscriptionScheduleCreateParams.DefaultSettings.builder().setDefaultPaymentMethod(clientCheckoutDTO.getPaymentMethodId()).build())
                                    .addPhase(
                                            SubscriptionScheduleCreateParams.Phase.builder()
                                                    .addItem(
                                                            SubscriptionScheduleCreateParams.Phase.Item.builder()
                                                                    .setPrice((firstPackageDetails.getDescription().equals("One Month Subscription")) ? "price_1NieCWBV0tnIJdW6WqIm2dti" : "price_1NkWmnBV0tnIJdW6ZGHtezqw")
                                                                    .setQuantity(1L)
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .setMetadata(metaDataMap)
                                    .build();

                    SubscriptionSchedule subscriptionSchedule = SubscriptionSchedule.create(params);

                } else {
                    // This one is for creating a discount coupon for a subscription schedule
                    Map<String, Object> couponParams = new HashMap<>();
                    couponParams.put("amount_off", clientCheckoutDTO.getDiscount()*100);
                    couponParams.put("currency", "usd");
                    couponParams.put("duration", "once");
                    couponParams.put("name", "$" + clientCheckoutDTO.getDiscount() + " off");
                    couponParams.put("max_redemptions", 1);

                    Coupon coupon = Coupon.create(couponParams);

                    // Now we need meta data as well
                    Map<String, String> metaDataMap = new HashMap<>();
                    metaDataMap.put("discountAmount", String.valueOf(clientCheckoutDTO.getDiscount()));

                    //Logic for gift card used
                    usedGiftCardInMetaData(clientCheckoutDTO, metaDataMap);

                    SubscriptionScheduleCreateParams params =
                            SubscriptionScheduleCreateParams.builder()
                                    .setCustomer(customer.getId())
                                    .setStartDate(renewalDate.toEpochSecond(LocalTime.from(renewalDateLDT), zoneOffSet))
                                    .setEndBehavior(SubscriptionScheduleCreateParams.EndBehavior.RELEASE)
                                    .setDefaultSettings(SubscriptionScheduleCreateParams.DefaultSettings.builder().setDefaultPaymentMethod(clientCheckoutDTO.getPaymentMethodId()).build())
                                    .addPhase(
                                            SubscriptionScheduleCreateParams.Phase.builder()
                                                    .addItem(
                                                            SubscriptionScheduleCreateParams.Phase.Item.builder()
                                                                    .setPrice((firstPackageDetails.getDescription().equals("One Month Subscription")) ? "price_1NieCWBV0tnIJdW6WqIm2dti" : "price_1NkWmnBV0tnIJdW6ZGHtezqw")
                                                                    .setQuantity(1L)
                                                                    .build()
                                                    )
                                                    .setCoupon(coupon.getId())
                                                    .build()
                                    )
                                    .setMetadata(metaDataMap)
                                    .build();

                    SubscriptionSchedule subscriptionSchedule = SubscriptionSchedule.create(params);
                }
            }
            else {

                Customer customer = getCustomerObjectByClientId(clientCheckoutDTO.getClient_id());

                // By setting the customer ID for creating a subscription. The customer's saved payment method forgoes any need to tap the card
                List<Object> items = new ArrayList<>();
                Map<String, Object> item1 = new HashMap<>();

                if (!discountNeeded) {
                    // Regular subscription no discount no scheduling

                    // Logic for gift card used
                    Map<String, String> metaDataMap = new HashMap<>();
                    usedGiftCardInMetaData(clientCheckoutDTO, metaDataMap);

                    item1.put(
                            "price",
                            (firstPackageDetails.getDescription().equals("One Month Subscription")) ? "price_1NieCWBV0tnIJdW6WqIm2dti" : "price_1NkWmnBV0tnIJdW6ZGHtezqw"
                    );
                    items.add(item1);
                    Map<String, Object> subscriptionParams = new HashMap<>();
                    subscriptionParams.put("customer", customer.getId());
                    subscriptionParams.put("items", items);
                    subscriptionParams.put("default_payment_method", clientCheckoutDTO.getPaymentMethodId());
                    subscriptionParams.put("metadata", metaDataMap);
                    Subscription subscription =
                            Subscription.create(subscriptionParams);

                    Invoice invoice = Invoice.retrieve(subscription.getLatestInvoice());

                    PaymentIntent paymentIntent = PaymentIntent.retrieve(invoice.getPaymentIntent());
                } else {
                    // This one is for adding a coupon/discount to a subscription that isnt on a schedule
                    Map<String, Object> couponParams = new HashMap<>();
                    couponParams.put("amount_off", clientCheckoutDTO.getDiscount()*100);
                    couponParams.put("currency", "usd");
                    couponParams.put("duration", "once");
                    couponParams.put("name", "$" + clientCheckoutDTO.getDiscount() + " off");
                    couponParams.put("max_redemptions", 1);

                    Coupon coupon = Coupon.create(couponParams);

                    // Now we need meta data as well
                    Map<String, String> metaDataMap = new HashMap<>();
                    metaDataMap.put("discountAmount", String.valueOf(clientCheckoutDTO.getDiscount()));

                    // Logic for gift card used
                    usedGiftCardInMetaData(clientCheckoutDTO, metaDataMap);

                    item1.put(
                            "price",
                            (firstPackageDetails.getDescription().equals("One Month Subscription")) ? "price_1NieCWBV0tnIJdW6WqIm2dti" : "price_1NkWmnBV0tnIJdW6ZGHtezqw"
                    );
                    items.add(item1);
                    Map<String, Object> subscriptionParams = new HashMap<>();
                    subscriptionParams.put("customer", customer.getId());
                    subscriptionParams.put("items", items);
                    subscriptionParams.put("default_payment_method", clientCheckoutDTO.getPaymentMethodId());
                    subscriptionParams.put("coupon", coupon.getId());
                    // add metaData into params
                    subscriptionParams.put("metadata", metaDataMap);

                    Subscription subscription =
                            Subscription.create(subscriptionParams);

                    Invoice invoice = Invoice.retrieve(subscription.getLatestInvoice());

                    PaymentIntent paymentIntent = PaymentIntent.retrieve(invoice.getPaymentIntent());
                }

            }



            return "";
        } else {

            if (clientCheckoutDTO.getPaymentMethodId() != null && clientCheckoutDTO.getPaymentMethodId().length() > 0) {


                //TODO Gift card logic

                String customer_id = getCustomerIdString(clientCheckoutDTO.getClient_id());

                String returnUrl = baseURL + "clientPackageManagement";

                Map<String, String> metaDataMap = new HashMap<>();
                metaDataMap.put("process", "admin");

                modifyMap(clientCheckoutDTO, metaDataMap);

                PaymentIntentCreateParams paymentIntentCreateParams =
                        PaymentIntentCreateParams.builder()
                                .setCurrency("usd")
                                .setCustomer(customer_id)
                                .setAmount((long) clientCheckoutDTO.getTotal() * 100)
                                .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
                                .setPaymentMethod(clientCheckoutDTO.getPaymentMethodId())
                                .setReturnUrl(returnUrl)
                                .setConfirm(true)
                                .putAllMetadata(metaDataMap)
                                .build();

                // This creates a payment intent
                PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentCreateParams);

                return "";
            } else {

                String customer_id = getCustomerIdString(clientCheckoutDTO.getClient_id());

                Map<String, String> metaDataMap = new HashMap<>();
                metaDataMap.put("process", "admin");

                modifyMap(clientCheckoutDTO, metaDataMap);

                PaymentIntentCreateParams paymentIntentCreateParams = getPaymentIntentCreateParams(clientCheckoutDTO, customer_id, metaDataMap);

                // This creates a payment intent
                PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentCreateParams);

                // This creates a parameter to pass in through the reader using the payment intent ID
                ReaderProcessPaymentIntentParams readerProcessParams = ReaderProcessPaymentIntentParams.builder().setPaymentIntent(paymentIntent.getId()).build();

                int attempt = 0;
                int tries = 3;
                while (true) {
                    attempt++;
                    try {
                        // This passes the params into the reader
                        readerResource.processPaymentIntent(readerProcessParams);

                        //TODO: Comment out the following line for a physical reader
                        // 1. This is for a simulated tap card
                        Reader reader = readerResource.getTestHelpers().presentPaymentMethod();

                        // TODO: You can return any string
                        return readerResource.toJson();
                    } catch (InvalidRequestException e) {
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
    }

    private boolean determineDiscountNeeded(ClientCheckoutDTO clientCheckoutDTO) {
        int originalRunningTotal = 0;

        List<PackageDetails> listOfPackages = clientCheckoutDTO.getListOfPackages();
        for (int i = 0; i < listOfPackages.size(); i++) {
            PackageDetails currentPackage = listOfPackages.get(i);
            originalRunningTotal += currentPackage.getPackage_cost().intValue();
        }

        clientCheckoutDTO.setDiscount(originalRunningTotal - clientCheckoutDTO.getTotal());

        int discountAmount = clientCheckoutDTO.getDiscount();
        if (discountAmount > 0) {
            return true;
        }
        return false;
    }

    private void modifyMap(ClientCheckoutDTO clientCheckoutDTO, Map<String, String> metaDataMap) {
        List<PackageDetails> listOfPackagesBeingPurchased = clientCheckoutDTO.getListOfPackages();


        int runningDiscountAmount = clientCheckoutDTO.getDiscount();

        for (int i = 0; i < listOfPackagesBeingPurchased.size(); i++) {
            String currentPackageName = listOfPackagesBeingPurchased.get(i).getDescription();

            if (currentPackageName.contains("Gift")) {
                metaDataMap.put("giftCardEmail", clientCheckoutDTO.getEmail());
                metaDataMap.put("saveGiftCardEmail", String.valueOf(clientCheckoutDTO.isSaveEmail()));
            }

            int packagePrice = listOfPackagesBeingPurchased.get(i).getPackage_cost().intValue();

            int discountApplied = 0;
            if (runningDiscountAmount > 0) {

                if (runningDiscountAmount - packagePrice <= 0) {
                    packagePrice -= runningDiscountAmount;
                    discountApplied = runningDiscountAmount;
                    runningDiscountAmount = 0;
                } else if (runningDiscountAmount - packagePrice > 0) {
                    runningDiscountAmount -= packagePrice;
                    discountApplied = packagePrice;
                    packagePrice = 0;
                }
            }

            String mapValueForPackage = packagePrice + "," + discountApplied;
            metaDataMap.put(currentPackageName, mapValueForPackage);
        }

        //TODO: Logic for gift card code
        usedGiftCardInMetaData(clientCheckoutDTO, metaDataMap);
    }

    private void usedGiftCardInMetaData(ClientCheckoutDTO clientCheckoutDTO, Map<String, String> metaDataMap) {
        String giftCode = "";
        String giftAmountUsed = "";

        if (clientCheckoutDTO.getGiftCard() != null && clientCheckoutDTO.getGiftCard().getCode() != null && clientCheckoutDTO.getGiftCard().getAmount() > 0) {
            GiftCard giftCardUsed = clientCheckoutDTO.getGiftCard();
            giftCode = giftCardUsed.getCode();
            giftAmountUsed = String.valueOf(giftCardUsed.getAmount());
            metaDataMap.put("giftCodeUsed", giftCode);
            metaDataMap.put("giftAmountUsed", giftAmountUsed);
        }
    }

    private PaymentIntentCreateParams getPaymentIntentCreateParams(ClientCheckoutDTO clientCheckoutDTO, String customer_id, Map<String, String> metaDataMap) {
        PaymentIntentCreateParams paymentIntentCreateParams = null;

        if (clientCheckoutDTO.isSaveCard()) {
            paymentIntentCreateParams =
                    PaymentIntentCreateParams.builder()
                            .setCurrency("usd")
                            .setCustomer(customer_id)
                            .setAmount((long) clientCheckoutDTO.getTotal() * 100)
                            .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
                            .addPaymentMethodType("card_present")
                            .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION)
                            .putAllMetadata(metaDataMap)
                            .build();
        } else {
            paymentIntentCreateParams =
                    PaymentIntentCreateParams.builder()
                            .setCurrency("usd")
                            .setCustomer(customer_id)
                            .setAmount((long) clientCheckoutDTO.getTotal() * 100)
                            .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
                            .addPaymentMethodType("card_present")
                            .putAllMetadata(metaDataMap)
                            .build();

        }
        return paymentIntentCreateParams;
    }

    @Override
    public List<PaymentMethodOptions> retrievePaymentMethodOptions(int clientId) throws StripeException {

        SetStripeKey();

        List<PaymentMethodOptions> listOfPaymentMethodOptions = new ArrayList<>();

        //  Find customer ID for Client, create a customer ID if needed.
        int retrievedClientId = clientId;

        String customer_id = getCustomerIdString(retrievedClientId);

        Customer customer =
                Customer.retrieve(customer_id);

        CustomerListPaymentMethodsParams customerListPaymentMethodsParams =
                CustomerListPaymentMethodsParams.builder()
                        .setType(CustomerListPaymentMethodsParams.Type.CARD)
                        .build();

        PaymentMethodCollection paymentMethods =
                customer.listPaymentMethods(customerListPaymentMethodsParams);

        List<PaymentMethod> paymentMethodsData = paymentMethods.getData();

        for (int i = 0; i < paymentMethodsData.size(); i++) {
            PaymentMethod currentPaymentData = paymentMethodsData.get(i);
            PaymentMethodOptions paymentMethodOptionObject = mapPaymentDataToObject(currentPaymentData);
            listOfPaymentMethodOptions.add(paymentMethodOptionObject);
        }

        return listOfPaymentMethodOptions;
    }

    @Override
    public void addPaymentMethodThroughReader(int clientId) throws StripeException {
        SetStripeKey();

        int retrievedClientId = clientId;

        String customer_id = getCustomerIdString(retrievedClientId);

        Reader readerResource = getReader();

        Map<String, Object> automaticPaymentMethods = new HashMap<>();
        automaticPaymentMethods.put("enabled", true);
        Map<String, Object> setupIntentParams = new HashMap<>();
        setupIntentParams.put("automatic_payment_methods",automaticPaymentMethods);
        setupIntentParams.put("customer", customer_id);

        SetupIntent setupIntent =
                SetupIntent.create(setupIntentParams);

        String setupIntentId = setupIntent.getId();

        Map<String, Object> readerParams = new HashMap<>();
        readerParams.put(
                "setup_intent",
               setupIntentId
        );
        readerParams.put("customer_consent_collected", "true");

        Reader updatedReader =
                readerResource.processSetupIntent(readerParams);

        updatedReader.toJson();

    }

    private Reader getReader() throws StripeException {
        //TODO:
        // 1. Replace this simulated reader ID ("tmr_FPKgUQOJ7fdmwi") with physical reader ID
        return Reader.retrieve("tmr_FPKgUQOJ7fdmwi");
    }

//    @Override
//    public void addPaymentMethodManually(int clientId, PaymentMethodOptions paymentMethodOption) throws StripeException {
//
//        SetStripeKey();
//
//        //  Find customer ID for Client, create a customer ID if needed.
//        int retrievedClientId = clientId;
//
//        String customer_id = getCustomerIdString(retrievedClientId);
//
//        Map<String, Object> card = new HashMap<>();
//        card.put("number", paymentMethodOption.getCardNumber());
//        card.put("exp_month", paymentMethodOption.getExpirationMonth());
//        card.put("exp_year", paymentMethodOption.getExpirationYear());
//        card.put("cvc", paymentMethodOption.getCvc());
//
//        Map<String, Object> paymentMethodParams = new HashMap<>();
//        paymentMethodParams.put("type", "card");
//        paymentMethodParams.put("card", card);
//
//        PaymentMethod paymentMethod =
//                PaymentMethod.create(paymentMethodParams);
//
//        Map<String, Object> automaticPaymentMethods =
//                new HashMap<>();
//        automaticPaymentMethods.put("enabled", true);
//        Map<String, Object> setupIntentParams = new HashMap<>();
//        setupIntentParams.put(
//                "automatic_payment_methods",
//                automaticPaymentMethods
//        );
//        setupIntentParams.put("customer", customer_id);
//        setupIntentParams.put("payment_method", paymentMethod);
//
//        SetupIntent.create(setupIntentParams);
//
//    }

    private void SetStripeKey() {
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
    }


    private PaymentMethodOptions mapPaymentDataToObject(PaymentMethod paymentMethod) {
        PaymentMethodOptions paymentMethodOptions = new PaymentMethodOptions();

        String pmID = paymentMethod.getId();
        paymentMethodOptions.setPaymentMethodId(pmID);
        String brand = paymentMethod.getCard().getBrand();
        paymentMethodOptions.setBrand(brand);
        int expMonth = paymentMethod.getCard().getExpMonth().intValue();
        paymentMethodOptions.setExpirationMonth(expMonth);
        int expYear = paymentMethod.getCard().getExpYear().intValue();
        paymentMethodOptions.setExpirationYear(expYear);
        String last4 = paymentMethod.getCard().getLast4();
        paymentMethodOptions.setCardNumber(last4);
        paymentMethodOptions.setCardDescription(brand.toUpperCase() + " (" + last4 + ") " + expMonth + "/" + expYear);


        return paymentMethodOptions;
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
                .setUnitAmount((long) (checkoutItemDTO.getPrice() * 100))
                .setProductData(
                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(checkoutItemDTO.getProductName())
                                .build()
                ).build();
    }

    private String getCustomerIdString(int retrievedClientId) throws StripeException {
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
        return customer_id;
    }

    private Customer getCustomerObjectByClientId(int retrievedClientId) throws StripeException {
        ClientDetails clientDetails = clientDetailsDao.findClientByClientId(retrievedClientId);

        String customer_id = "";

        if (clientDetails.getCustomer_id() != null) {
            customer_id = clientDetails.getCustomer_id();
            return Customer.retrieve(customer_id);
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("name", clientDetails.getFirst_name() + " " + clientDetails.getLast_name());
            Customer customer = Customer.create(params);

            customer_id = customer.getId();

            // Save customer_id into DB
            clientDetailsDao.updateClientCustomerId(retrievedClientId, customer_id);
        }
        return Customer.retrieve(customer_id);
    }
}
