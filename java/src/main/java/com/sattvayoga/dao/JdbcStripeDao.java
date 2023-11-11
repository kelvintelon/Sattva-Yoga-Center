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
import com.stripe.param.*;
import com.stripe.param.checkout.SessionCreateParams;
import com.sattvayoga.dto.order.CheckoutItemDTO;
import com.stripe.param.terminal.ReaderProcessPaymentIntentParams;
import com.stripe.param.terminal.ReaderProcessSetupIntentParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Component
public class JdbcStripeDao implements StripeDao {

    @Value("${BASE_URL}")
    private String baseURL;

    @Value("${STRIPE_SECRET_KEY}")
    private String apiKey;

    @Autowired
    PackageDetailsDao packageDetailsDao;

    @Autowired
    PackagePurchaseDao packagePurchaseDao;

    // TODO: If you want to deploy uncomment below
//    @Autowired
//    private SecretManagerService secretManagerService;

    @Autowired
    private ClientDetailsDao clientDetailsDao;

    @Autowired
    private SaleDao saleDao;

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private EmailSenderService senderService;


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

//            if (checkoutItemDTO.isIs_monthly_renew()) {
//                foundSubscription = true;
//                // Handle subscription items
//                sessionItemList.add(createSubscriptionLineItem(checkoutItemDTO));
//            } else {
//                // Handle one-time purchase items
//                sessionItemList.add(createSessionLineItem(checkoutItemDTO));
//            }
            sessionItemList.add(createSessionLineItem(checkoutItemDTO));
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

        String customer_id = getCustomerIdString(clientCheckoutDTO.getClient_id());
//        boolean discountNeeded = determineDiscountNeeded(clientCheckoutDTO);

//        PackageDetails firstPackageDetails = clientCheckoutDTO.getListOfPackages().get(0);

        // if it's comp/Free then return immediately
        if (clientCheckoutDTO.isCompFree()) {
            // just process everything here and return at the end;
            double runningTotal = 0;

            List<PackageDetails> listOfPackagesBeingPurchased = clientCheckoutDTO.getSelectedCheckoutPackages();
            List<Integer> packagePurchaseIDs = new ArrayList<>();

            String packagesBeingBoughtForEmail = "";
//            for (int i = 0; i < clientCheckoutDTO.getSelectedCheckoutPackages().size(); i++) {
//                PackageDetails currentPackage = clientCheckoutDTO.getSelectedCheckoutPackages().get(i);
//                packagesBeingBoughtForEmail += currentPackage.getDescription() + "\n";
//            }

            // Insert everything that was in the list into package purchase
            //  1. grab package purchase IDs and package description
            for (int i = 0; i < listOfPackagesBeingPurchased.size(); i++) {
                PackageDetails currentPackage = listOfPackagesBeingPurchased.get(i);

                //  2. If there is an email to save or a gift card to process we should still do it
                if (currentPackage.getDescription().contains("Gift")) {
                    String giftEmail = clientCheckoutDTO.getEmailForGift();

                    CheckoutItemDTO checkoutItemDTO = new CheckoutItemDTO();
                    checkoutItemDTO.setClient_id(clientCheckoutDTO.getClient_id());
                    checkoutItemDTO.setPackage_id(currentPackage.getPackage_id());
                    runningTotal += currentPackage.getPackage_cost().doubleValue();
                    checkoutItemDTO.setTotal_amount_paid(BigDecimal.valueOf(0));
                    checkoutItemDTO.setDiscount(0);
                    checkoutItemDTO.setPaymentId("comp/free");

                    String code = packagePurchaseDao.generateGiftCardCode();
                    packagePurchaseDao.createGiftCard(code, currentPackage.getPackage_cost().doubleValue());
                    int packagePurchaseId = packagePurchaseDao.createGiftCardPurchase(checkoutItemDTO);

                    packagePurchaseIDs.add(packagePurchaseId);

                    try {
                        senderService.sendEmail(giftEmail,"Sattva Yoga Center Gift Card Code", "Your Gift Card code is: " + code + " . Please note: The Gift Card Code can only be redeemed in person. Once redeemed, it cannot be used by anyone else.");
                    } catch (Throwable e) {
                        System.out.println("Error sending gift card email to client id: " + clientCheckoutDTO.getClient_id());
                    }
                } else {
                    PackagePurchase packagePurchase = new PackagePurchase();
                    packagePurchase.setClient_id(clientCheckoutDTO.getClient_id());
                    packagePurchase.setPackage_id(currentPackage.getPackage_id());
                    packagePurchase.setClasses_remaining(currentPackage.getClasses_amount());
                    packagePurchase.setIs_monthly_renew(currentPackage.isIs_recurring());
                    packagePurchase.setPackage_duration(currentPackage.getPackage_duration());
                    runningTotal += currentPackage.getPackage_cost().doubleValue();
                    packagePurchase.setTotal_amount_paid(BigDecimal.valueOf(0));
                    packagePurchase.setDiscount(BigDecimal.valueOf(0.0));
                    packagePurchase.setPaymentId("comp/free");

                    int packagePurchaseId = packagePurchaseDao.createAdminPackagePurchase(packagePurchase);
                    PackagePurchase packagePurchase1 = packagePurchaseDao.getPackagePurchaseObjectByPackagePurchaseId(packagePurchaseId);
                    packagesBeingBoughtForEmail += currentPackage.getDescription() + " - $0.00 - " + "Expires on: " + packagePurchase1.getExpiration_date().toString() + "<br>";
                    packagePurchaseIDs.add(packagePurchaseId);

                }

            }
            // Insert into sales/order table
            //  1. Import Package purchase IDs into int[] field
            //  2. Grab serialized sale ID;
            int[] arrayOfPackagePurchaseIDs = packagePurchaseIDs.stream().mapToInt(i -> i).toArray();
            Sale sale = new Sale();
            sale.setPackages_purchased_array(arrayOfPackagePurchaseIDs);
            sale.setClient_id(clientCheckoutDTO.getClient_id());
            int saleId = saleDao.createSaleNoBatch(sale);


            // Insert into transactions table last
            //  1. Import serialized sale ID and package description, along with the type of payment that was used.
            Transaction transaction = new Transaction();
            transaction.setSale_id(saleId);
            transaction.setPayment_type("Comp/free");
            transaction.setPayment_amount(0);
            transaction.setClient_id(clientCheckoutDTO.getClient_id());

            transactionDao.createTransaction(transaction);

            String saleDate = LocalDate.now().toString();
            String firstName = clientDetailsDao.findClientByClientId(clientCheckoutDTO.getClient_id()).getFirst_name();
            String paymentType = "Comp/Free";
            String subject = "Receipt for Your Sattva Yoga Center LLC Purchase";
            String subTotal = "$0.00";
            String tax = "$0.00";
            String total = "$0.00";
            String usedPaymentTypes = paymentType + "       " + "$0.00" + "         ";
            sendEmailReceipt(clientCheckoutDTO, packagesBeingBoughtForEmail, saleId, saleDate, firstName, subject, subTotal, tax, total, usedPaymentTypes);

            // Just return
            return "success";
        }
        if (clientCheckoutDTO.getBalance() == 0) {
            List<PackageDetails> listOfPackagesBeingPurchased = clientCheckoutDTO.getSelectedCheckoutPackages();
            List<Integer> packagePurchaseIDs = new ArrayList<>();

            int runningDiscountAmount = clientCheckoutDTO.getDiscount();

            boolean isGiftCardUsed = false;
            double giftAmountUsed = 0;

            if (clientCheckoutDTO.getGiftCard() != null && clientCheckoutDTO.getGiftCard().getCode() != null && clientCheckoutDTO.getGiftCard().getAmount() > 0) {
                isGiftCardUsed = true;
                GiftCard giftCardUsed = clientCheckoutDTO.getGiftCard();
                String giftCode = giftCardUsed.getCode();
                giftAmountUsed = giftCardUsed.getAmount();

                GiftCard originalGiftCard = packagePurchaseDao.retrieveGiftCard(giftCode);

                packagePurchaseDao.updateGiftCard(originalGiftCard, clientCheckoutDTO.getClient_id(), giftAmountUsed);
            }

            String packagesBeingBoughtForEmail = "";
//            for (int i = 0; i < clientCheckoutDTO.getSelectedCheckoutPackages().size(); i++) {
//                PackageDetails currentPackage = clientCheckoutDTO.getSelectedCheckoutPackages().get(i);
//                packagesBeingBoughtForEmail += currentPackage.getDescription() + "\n";
//            }


            // Insert everything that was in the list into package purchase
            //  1. grab package purchase IDs and package description
            for (int i = 0; i < listOfPackagesBeingPurchased.size(); i++) {
                PackageDetails currentPackage = listOfPackagesBeingPurchased.get(i);

                int packagePrice = currentPackage.getPackage_cost().intValue();
                int discountApplied = 0;

                //  2. If there is an email to save or a gift card to process we should still do it
                if (currentPackage.getDescription().contains("Gift")) {
                    String giftEmail = clientCheckoutDTO.getEmailForGift();

                    CheckoutItemDTO checkoutItemDTO = new CheckoutItemDTO();
                    checkoutItemDTO.setClient_id(clientCheckoutDTO.getClient_id());
                    checkoutItemDTO.setPackage_id(currentPackage.getPackage_id());

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

                    checkoutItemDTO.setTotal_amount_paid(BigDecimal.valueOf(packagePrice));

                    checkoutItemDTO.setDiscount(discountApplied);

                    //Create a concatenated string for payment ID cash/check/giftCardcode used

                    String paymentIdString = "";

                    if (clientCheckoutDTO.getCash() > 0) {
                        paymentIdString += "cash/";
                    }

                    if (clientCheckoutDTO.getCheck() > 0) {
                        paymentIdString += "check/";
                    }

                    if (isGiftCardUsed) {
                        paymentIdString += "giftCardCode/";
                    }

                    checkoutItemDTO.setPaymentId(paymentIdString);

                    String code = packagePurchaseDao.generateGiftCardCode();
                    packagePurchaseDao.createGiftCard(code, currentPackage.getPackage_cost().doubleValue());
                    int packagePurchaseId = packagePurchaseDao.createGiftCardPurchase(checkoutItemDTO);

                    packagePurchaseIDs.add(packagePurchaseId);

                    PackagePurchase packagePurchase1 = packagePurchaseDao.getPackagePurchaseObjectByPackagePurchaseId(packagePurchaseId);
                    packagesBeingBoughtForEmail += currentPackage.getDescription() + " - $" + currentPackage.getPackage_cost() + " - " + "Expires on: " + packagePurchase1.getExpiration_date().toString()  + "<br>";


                    try {
                        senderService.sendEmail(giftEmail,"Sattva Yoga Center Gift Card Code", "Your Gift Card code is: " + code + " . Please note: The Gift Card Code can only be redeemed in person. Once redeemed, it cannot be used by anyone else.");
                    } catch (Throwable e) {
                        System.out.println("Error sending gift card email to client id: " + clientCheckoutDTO.getClient_id());
                    }

                } else {
                    PackagePurchase packagePurchase = new PackagePurchase();


                    packagePurchase.setClient_id(clientCheckoutDTO.getClient_id());
                    packagePurchase.setPackage_id(currentPackage.getPackage_id());
                    packagePurchase.setClasses_remaining(currentPackage.getClasses_amount());
                    packagePurchase.setIs_monthly_renew(currentPackage.isIs_recurring());
                    packagePurchase.setPackage_duration(currentPackage.getPackage_duration());
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


                    packagePurchase.setTotal_amount_paid(BigDecimal.valueOf(packagePrice));

                    packagePurchase.setDiscount(BigDecimal.valueOf(discountApplied));

                    //Create a concatenated string for payment ID cash/check/giftCardcode used

                    String paymentIdString = "";

                    if (clientCheckoutDTO.getCash() > 0) {
                        paymentIdString += "cash/";
                    }

                    if (clientCheckoutDTO.getCheck() > 0) {
                        paymentIdString += "check/";
                    }

                    if (isGiftCardUsed) {
                        paymentIdString += "giftCardCode/";
                    }

                    packagePurchase.setPaymentId(paymentIdString);

                    int packagePurchaseId = packagePurchaseDao.createAdminPackagePurchase(packagePurchase);

                    packagePurchaseIDs.add(packagePurchaseId);

                    PackagePurchase packagePurchase1 = packagePurchaseDao.getPackagePurchaseObjectByPackagePurchaseId(packagePurchaseId);
                    packagesBeingBoughtForEmail += currentPackage.getDescription() + " - $" + currentPackage.getPackage_cost() + " - " + "Expires on: " + packagePurchase1.getExpiration_date().toString() + "<br>";

                }

            }

            // Set all package purchase IDS into sale table
            int[] arrayOfPackagePurchaseIDs = packagePurchaseIDs.stream().mapToInt(i -> i).toArray();
            Sale sale = new Sale();
            sale.setPackages_purchased_array(arrayOfPackagePurchaseIDs);
            sale.setClient_id(clientCheckoutDTO.getClient_id());
            int saleId = saleDao.createSaleNoBatch(sale);

            // SET Cash/Check/GiftAmount used TRANSACTION TABLES
            if (clientCheckoutDTO.getCash() > 0) {
                Transaction transaction = new Transaction();
                transaction.setSale_id(saleId);
                transaction.setPayment_type("Cash");
                transaction.setPayment_amount(clientCheckoutDTO.getCash());
                transaction.setClient_id(clientCheckoutDTO.getClient_id());

                transactionDao.createTransaction(transaction);
            }
            if (clientCheckoutDTO.getCheck() > 0 ) {
                Transaction transaction = new Transaction();
                transaction.setSale_id(saleId);
                transaction.setPayment_type("Check");
                transaction.setPayment_amount(clientCheckoutDTO.getCheck());
                transaction.setClient_id(clientCheckoutDTO.getClient_id());

                transactionDao.createTransaction(transaction);
            }
            if (isGiftCardUsed) {
                Transaction transaction = new Transaction();
                transaction.setSale_id(saleId);
                transaction.setPayment_type("Gift Card Code");
                transaction.setPayment_amount(giftAmountUsed);
                transaction.setClient_id(clientCheckoutDTO.getClient_id());

                transactionDao.createTransaction(transaction);
            }

            packagesBeingBoughtForEmail += "<br>";

            if (clientCheckoutDTO.getDiscount() > 0) {
                packagesBeingBoughtForEmail += "Discount: $" + clientCheckoutDTO.getDiscount() + "<br>";
            }

            String saleDate = LocalDate.now().toString();
            String firstName = clientDetailsDao.findClientByClientId(clientCheckoutDTO.getClient_id()).getFirst_name();
            String subject = "Receipt for Your Sattva Yoga Center LLC Purchase";
            String subTotal = "$" + (clientCheckoutDTO.getCash() + clientCheckoutDTO.getCheck() + giftAmountUsed);
            String tax = "$0.00";
            String total = "$" + (clientCheckoutDTO.getCash() + clientCheckoutDTO.getCheck() + giftAmountUsed);
            String usedPaymentTypes = "";
            if (clientCheckoutDTO.getCash() > 0) {
                usedPaymentTypes += "Cash" + "      " + "$" + clientCheckoutDTO.getCash() + "<br>";
            }
            if (clientCheckoutDTO.getCheck() > 0) {
                usedPaymentTypes += "Check" + "      " + "$" + clientCheckoutDTO.getCheck() + "<br>";
            }
            if (isGiftCardUsed) {
                usedPaymentTypes += "Gift Card Code" + "      " + "$" + giftAmountUsed + "<br>";
            }
            //paymentType + "\t" + "$" + runningTotal + "\n";
            sendEmailReceipt(clientCheckoutDTO, packagesBeingBoughtForEmail, saleId, saleDate, firstName, subject, subTotal, tax, total, usedPaymentTypes);


            return "success";
        }
        if (clientCheckoutDTO.getPaymentMethodId() != null && clientCheckoutDTO.getPaymentMethodId().length() > 0) {


            String returnUrl = baseURL + "clientPackageManagement";

            Map<String, String> metaDataMap = new HashMap<>();
            String descriptionForPayment = metaDataMap.put("process", "admin");

            boolean isPaymentKeyedStored = true;
            modifyMap(clientCheckoutDTO, metaDataMap, isPaymentKeyedStored);

            if (clientCheckoutDTO.getEmailForReceipt().length()>0 && clientCheckoutDTO.isSendEmail()) {
                PaymentIntentCreateParams paymentIntentCreateParams =
                        PaymentIntentCreateParams.builder()
                                .setCurrency("usd")
                                .setCustomer(customer_id)
                                .setAmount((long) clientCheckoutDTO.getBalance() * 100)
                                .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
                                .setPaymentMethod(clientCheckoutDTO.getPaymentMethodId())
                                .setReturnUrl(returnUrl)
                                .setReceiptEmail(clientCheckoutDTO.getEmailForReceipt())
                                .setDescription(descriptionForPayment)
                                .setConfirm(true)
                                .putAllMetadata(metaDataMap)
                                .build();

                // This creates a payment intent
                PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentCreateParams);
            } else {

                PaymentIntentCreateParams paymentIntentCreateParams =
                        PaymentIntentCreateParams.builder()
                                .setCurrency("usd")
                                .setCustomer(customer_id)
                                .setAmount((long) clientCheckoutDTO.getBalance() * 100)
                                .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
                                .setPaymentMethod(clientCheckoutDTO.getPaymentMethodId())
                                .setReturnUrl(returnUrl)
                                .setDescription(descriptionForPayment)
                                .setConfirm(true)
                                .putAllMetadata(metaDataMap)
                                .build();

                // This creates a payment intent
                PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentCreateParams);
            }
            return "Success";
        } else {

            Map<String, String> metaDataMap = new HashMap<>();
            metaDataMap.put("process", "admin");

            boolean isPaymentKeyedStored = false;
            String descriptionForPayment = modifyMap(clientCheckoutDTO, metaDataMap, isPaymentKeyedStored);

            PaymentIntentCreateParams paymentIntentCreateParams = getPaymentIntentCreateParams(clientCheckoutDTO, customer_id, metaDataMap, descriptionForPayment);

            // This creates a payment intent
            PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentCreateParams);

            if (clientCheckoutDTO.getEmailForReceipt().length()>0 && clientCheckoutDTO.isSendEmail()) {
                Map<String, Object> params = new HashMap<>();
                params.put("receipt_email", clientCheckoutDTO.getEmailForReceipt());
                paymentIntent = paymentIntent.update(params);
            }

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
//        if (firstPackageDetails.isIs_recurring() && firstPackageDetails.isUnlimited()) {
//
//
//            // Compare the renewal Date to today's date to see if they are the same.
//            // If they are not then follow through changing the subscription scheduling
//            if (!LocalDate.now().toString().equals(clientCheckoutDTO.getRenewalDate())) {
//
//                Customer customer = getCustomerForSubscriptionByClientId(clientCheckoutDTO.getClient_id(),clientCheckoutDTO.getEmailForReceipt());
//
//                LocalDate renewalDate = LocalDate.parse(clientCheckoutDTO.getRenewalDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//
//                ZoneId zone = ZoneId.of("America/New_York");
//                LocalDateTime renewalDateLDT = renewalDate.atStartOfDay();
//                ZoneOffset zoneOffSet = zone.getRules().getOffset(renewalDateLDT);
//
//                if (!discountNeeded) {
//                    // Regular subscription scheduling with no discount
//
//                    // Gift card used logic for metadata
//                    Map<String, String> metaDataMap = new HashMap<>();
//                    usedGiftCardInMetaData(clientCheckoutDTO, metaDataMap);
//
//                    SubscriptionScheduleCreateParams params =
//                            SubscriptionScheduleCreateParams.builder()
//                                    .setCustomer(customer.getId())
//                                    .setStartDate(renewalDate.toEpochSecond(LocalTime.from(renewalDateLDT), zoneOffSet))
//                                    .setEndBehavior(SubscriptionScheduleCreateParams.EndBehavior.CANCEL)
//                                    .setDefaultSettings(SubscriptionScheduleCreateParams.DefaultSettings.builder().setDefaultPaymentMethod(clientCheckoutDTO.getPaymentMethodId()).build())
//                                    .addPhase(
//                                            SubscriptionScheduleCreateParams.Phase.builder()
//                                                    .addItem(
//                                                            SubscriptionScheduleCreateParams.Phase.Item.builder()
//                                                                    .setPrice((firstPackageDetails.getDescription().equals("One Month Subscription")) ? "price_1NieCWBV0tnIJdW6WqIm2dti" : "price_1NkWmnBV0tnIJdW6ZGHtezqw")
//                                                                    .setQuantity(1L)
//                                                                    .build()
//                                                    )
//                                                    .setIterations((long) clientCheckoutDTO.getIterations())
//                                                    .build()
//                                    )
//                                    .setMetadata(metaDataMap)
//                                    .build();
//
//                    SubscriptionSchedule subscriptionSchedule = SubscriptionSchedule.create(params);
//
//
//
//                } else {
//                    // This one is for creating a discount coupon for a subscription schedule
//                    Map<String, Object> couponParams = new HashMap<>();
//                    couponParams.put("amount_off", clientCheckoutDTO.getDiscount()*100);
//                    couponParams.put("currency", "usd");
//                    if (clientCheckoutDTO.isSaveAsRecurringPayment()) {
//                        couponParams.put("duration", "forever");
//                    } else {
//                        couponParams.put("duration", "once");
//                    }
//
//                    couponParams.put("name", "$" + clientCheckoutDTO.getDiscount() + " off");
//                    couponParams.put("max_redemptions", 1);
//
//                    Coupon coupon = Coupon.create(couponParams);
//
//                    // Now we need meta data as well
//                    Map<String, String> metaDataMap = new HashMap<>();
//                    metaDataMap.put("discountAmount", String.valueOf(clientCheckoutDTO.getDiscount()));
//
//                    //Logic for gift card used
//                    usedGiftCardInMetaData(clientCheckoutDTO, metaDataMap);
//
//                    SubscriptionScheduleCreateParams params =
//                            SubscriptionScheduleCreateParams.builder()
//                                    .setCustomer(customer.getId())
//                                    .setStartDate(renewalDate.toEpochSecond(LocalTime.from(renewalDateLDT), zoneOffSet))
//                                    .setEndBehavior(SubscriptionScheduleCreateParams.EndBehavior.CANCEL)
//                                    .setDefaultSettings(SubscriptionScheduleCreateParams.DefaultSettings.builder().setDefaultPaymentMethod(clientCheckoutDTO.getPaymentMethodId()).build())
//                                    .addPhase(
//                                            SubscriptionScheduleCreateParams.Phase.builder()
//                                                    .addItem(
//                                                            SubscriptionScheduleCreateParams.Phase.Item.builder()
//                                                                    .setPrice((firstPackageDetails.getDescription().equals("One Month Subscription")) ? "price_1NieCWBV0tnIJdW6WqIm2dti" : "price_1NkWmnBV0tnIJdW6ZGHtezqw")
//                                                                    .setQuantity(1L)
//                                                                    .build()
//                                                    )
//                                                    .setIterations((long) clientCheckoutDTO.getIterations())
//                                                    .setCoupon(coupon.getId())
//                                                    .build()
//                                    )
//                                    .setMetadata(metaDataMap)
//                                    .build();
//
//                    SubscriptionSchedule subscriptionSchedule = SubscriptionSchedule.create(params);
//                }
//            }
//            else {
//
//                Customer customer = getCustomerForSubscriptionByClientId(clientCheckoutDTO.getClient_id(),clientCheckoutDTO.getEmailForReceipt());
//
//                // By setting the customer ID for creating a subscription. The customer's saved payment method forgoes any need to tap the card
//                List<Object> items = new ArrayList<>();
//                Map<String, Object> item1 = new HashMap<>();
//
//                if (!discountNeeded) {
//                    // Regular subscription no discount no scheduling
//
//                    // Logic for gift card used
//                    Map<String, String> metaDataMap = new HashMap<>();
//                    usedGiftCardInMetaData(clientCheckoutDTO, metaDataMap);
//
//                    item1.put(
//                            "price",
//                            (firstPackageDetails.getDescription().equals("One Month Subscription")) ? "price_1NieCWBV0tnIJdW6WqIm2dti" : "price_1NkWmnBV0tnIJdW6ZGHtezqw"
//                    );
//                    items.add(item1);
//                    Map<String, Object> subscriptionParams = new HashMap<>();
//                    subscriptionParams.put("customer", customer.getId());
//                    subscriptionParams.put("items", items);
//                    subscriptionParams.put("default_payment_method", clientCheckoutDTO.getPaymentMethodId());
//                    subscriptionParams.put("metadata", metaDataMap);
//
//                    //Cancel_at in unix timestamp
//                    createCancelAtTimestampFromIterations(clientCheckoutDTO, firstPackageDetails, subscriptionParams);
//
//                    Subscription subscription =
//                            Subscription.create(subscriptionParams);
//
//                    Invoice invoice = Invoice.retrieve(subscription.getLatestInvoice());
//
//                    PaymentIntent paymentIntent = PaymentIntent.retrieve(invoice.getPaymentIntent());
//                } else {
//                    // This one is for adding a coupon/discount to a subscription that isnt on a schedule
//                    Map<String, Object> couponParams = new HashMap<>();
//                    couponParams.put("amount_off", clientCheckoutDTO.getDiscount()*100);
//                    couponParams.put("currency", "usd");
//                    if (clientCheckoutDTO.isSaveAsRecurringPayment()) {
//                        couponParams.put("duration", "forever");
//                    } else {
//                        couponParams.put("duration", "once");
//                    }
//                    couponParams.put("name", "$" + clientCheckoutDTO.getDiscount() + " off");
//                    couponParams.put("max_redemptions", 1);
//
//                    Coupon coupon = Coupon.create(couponParams);
//
//                    // Now we need meta data as well
//                    Map<String, String> metaDataMap = new HashMap<>();
//                    metaDataMap.put("discountAmount", String.valueOf(clientCheckoutDTO.getDiscount()));
//
//                    // Logic for gift card used
//                    usedGiftCardInMetaData(clientCheckoutDTO, metaDataMap);
//
//                    item1.put(
//                            "price",
//                            (firstPackageDetails.getDescription().equals("One Month Subscription")) ? "price_1NieCWBV0tnIJdW6WqIm2dti" : "price_1NkWmnBV0tnIJdW6ZGHtezqw"
//                    );
//                    items.add(item1);
//                    Map<String, Object> subscriptionParams = new HashMap<>();
//                    subscriptionParams.put("customer", customer.getId());
//                    subscriptionParams.put("items", items);
//                    subscriptionParams.put("default_payment_method", clientCheckoutDTO.getPaymentMethodId());
//                    subscriptionParams.put("coupon", coupon.getId());
//                    // add metaData into params
//                    subscriptionParams.put("metadata", metaDataMap);
//
//                    // Cancel at unix time stamp
//                    createCancelAtTimestampFromIterations(clientCheckoutDTO, firstPackageDetails, subscriptionParams);
//
//                    Subscription subscription =
//                            Subscription.create(subscriptionParams);
//
//                    Invoice invoice = Invoice.retrieve(subscription.getLatestInvoice());
//
//                    PaymentIntent paymentIntent = PaymentIntent.retrieve(invoice.getPaymentIntent());
//                }
//
//            }
//
//
//
//            return "";
//        } else {
//
//
//
//        }
    }

    @Override
    public void sendEmailReceipt(ClientCheckoutDTO clientCheckoutDTO, String packagesBeingBoughtForEmail, int saleId, String saleDate, String firstName, String subject, String subTotal, String tax, String total, String usedPaymentTypes) {
        if (clientCheckoutDTO.getEmailForReceipt().length()>0 && clientCheckoutDTO.isSendEmail()) {

            String paymentDetails = "<b>Payment Method -      </b>" + "<b>Amount</b>" + "<br>" +
                    usedPaymentTypes + "<br>" + "<br>" + "      Customer Copy" + "<br>";
            String body = "Dear, " + firstName + "<br>" +
                    "Thank you for shopping at our store. Below is your purchase receipt; please keep a copy for your records." + "<br>" +
                    "Sale Date:     " + saleDate + "<br>" +
                    "Sale ID:       " + saleId + "<br>" + "<br>" +
                    packagesBeingBoughtForEmail + "<br>" +
                    "Subtotal: " + subTotal + "<br>" +
                    "Tax:      " + tax + "<br>" +
                    "Total:     " + total + "<br>" + "<br>" +
                    paymentDetails + "<br>" +
                    "We appreciate your business! When you in come in for a class, please bring a yoga mat and arrive on time." + "<br>" +
                    "Please retain this receipt for your records. Thank you!" + "<br>" +
                    "If you have any additional questions, then please feel free to contact us using the email or phone number listed below." + "<br>" + "<br>" +
                    "Thank you!" + "<br>" +
                    "Sattva Yoga Center LLC" + "<br>" +
                    "Web: http://www.sattva-yoga-center.com" + "<br>" +
                    "Phone: (313)-274-3995" + "<br>" + "<br>" +
                    "835 Mason Street, Suite B120, Dearborn, MI 48124" + "<br>" +
                    "info@sattva-yoga-center.com";

            // send email
            try {
                senderService.sendEmail(clientCheckoutDTO.getEmailForReceipt(), subject, body);
            } catch (Throwable e) {
                System.out.println("Error sending comp/free email receipt");
            }
        }
    }

    private void createCancelAtTimestampFromIterations(ClientCheckoutDTO clientCheckoutDTO, PackageDetails firstPackageDetails, Map<String, Object> subscriptionParams) {
        long cancelAtUnixTimestamp = 0;

        if (firstPackageDetails.getDescription().equals("One Month Subscription")) {
            LocalDate todayDate = LocalDate.now();
            LocalDate futureDate = todayDate.plusMonths(1* clientCheckoutDTO.getIterations());

            ZoneId zone = ZoneId.of("America/New_York");
            LocalDateTime futureDateLDT = futureDate.atStartOfDay();
            ZoneOffset zoneOffSet = zone.getRules().getOffset(futureDateLDT);

            cancelAtUnixTimestamp = futureDate.toEpochSecond(LocalTime.from(futureDateLDT), zoneOffSet);

            subscriptionParams.put("cancel_at", cancelAtUnixTimestamp);
        } else {
            LocalDate todayDate = LocalDate.now();
            LocalDate futureDate = todayDate.plusMonths(6* clientCheckoutDTO.getIterations());

            ZoneId zone = ZoneId.of("America/New_York");
            LocalDateTime futureDateLDT = futureDate.atStartOfDay();
            ZoneOffset zoneOffSet = zone.getRules().getOffset(futureDateLDT);

            cancelAtUnixTimestamp = futureDate.toEpochSecond(LocalTime.from(futureDateLDT), zoneOffSet);

            subscriptionParams.put("cancel_at", cancelAtUnixTimestamp);
        }
    }

    private boolean determineDiscountNeeded(ClientCheckoutDTO clientCheckoutDTO) {
        int originalRunningTotal = 0;

        List<PackageDetails> listOfPackages = clientCheckoutDTO.getListOfPackages();
        for (int i = 0; i < listOfPackages.size(); i++) {
            PackageDetails currentPackage = listOfPackages.get(i);
            originalRunningTotal += currentPackage.getPackage_cost().intValue();
        }

        clientCheckoutDTO.setDiscount(originalRunningTotal - clientCheckoutDTO.getBalance());

        int discountAmount = clientCheckoutDTO.getDiscount();
        if (discountAmount > 0) {
            return true;
        }
        return false;
    }

    private String modifyMap(ClientCheckoutDTO clientCheckoutDTO, Map<String, String> metaDataMap, Boolean keyedStoredOrSwiped) {
        List<PackageDetails> listOfPackagesBeingPurchased = clientCheckoutDTO.getListOfPackages();


        int runningDiscountAmount = clientCheckoutDTO.getDiscount();

        Map<String, String> mapForDescription = new HashMap<>();
        String descriptionToReturn = "";
        for (int i = 0; i < listOfPackagesBeingPurchased.size(); i++) {
        String currentPackageName = listOfPackagesBeingPurchased.get(i).getDescription();

        if (currentPackageName.contains("Gift")) {
            metaDataMap.put("giftCardEmail", clientCheckoutDTO.getEmailForGift());
//            metaDataMap.put("saveGiftCardEmail", String.valueOf(clientCheckoutDTO.isSaveEmailGiftCardPurchase()));
        }

//        if (clientCheckoutDTO.isSaveEmailReceiptPurchase()) {
//            metaDataMap.put("saveReceiptEmail", clientCheckoutDTO.getEmailForReceipt());
//        }

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
        mapForDescription.put(currentPackageName, mapValueForPackage);
    }

        // keyedStoredOrSwiped = true means keyedStored, if = false then swiped
        if (keyedStoredOrSwiped) {
            metaDataMap.put("stripeKeyedStored", String.valueOf(clientCheckoutDTO.getBalance()));
        } else {
            metaDataMap.put("stripeSwiped", String.valueOf(clientCheckoutDTO.getBalance()));
        }

        if (clientCheckoutDTO.getCash() > 0) {
            metaDataMap.put("cash", String.valueOf(clientCheckoutDTO.getCash()));
        }

        if (clientCheckoutDTO.getCheck() > 0) {
            metaDataMap.put("check", String.valueOf(clientCheckoutDTO.getCheck()));
        }


        List<String> listOfKeys = new ArrayList<>(mapForDescription.keySet());
    for (int i = 0; i < listOfKeys.size(); i++) {
        String nameOfProduct = listOfKeys.get(i);
        String[] valuesSplit = mapForDescription.get(nameOfProduct).split(",");
        String pricePaid = valuesSplit[0];
        String discountApplied = valuesSplit[1];
        if (i == listOfKeys.size()-1) {
            // Don't add a new line here on last one
            descriptionToReturn += nameOfProduct + " - ";
            if (Integer.valueOf(pricePaid) == 0) {
                descriptionToReturn += "PAID/COVERED" ;
            } else {
                descriptionToReturn += "$" + pricePaid;
            }
        } else {
            // Add a new line here
            descriptionToReturn += nameOfProduct + " - ";
            if (Integer.valueOf(pricePaid) == 0) {
                descriptionToReturn += "PAID/COVERED" + "\n";
            } else {
                descriptionToReturn += "$" + pricePaid + "\n";
            }
        }
    }

    usedGiftCardInMetaData(clientCheckoutDTO, metaDataMap);
        return descriptionToReturn;
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

    private PaymentIntentCreateParams getPaymentIntentCreateParams(ClientCheckoutDTO clientCheckoutDTO, String customer_id, Map<String, String> metaDataMap, String description) {
        PaymentIntentCreateParams paymentIntentCreateParams = null;

        if (clientCheckoutDTO.isSaveCard()) {
            paymentIntentCreateParams =
                    PaymentIntentCreateParams.builder()
                            .setCurrency("usd")
                            .setCustomer(customer_id)
                            .setAmount((long) clientCheckoutDTO.getBalance() * 100)
                            .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
                            .addPaymentMethodType("card_present")
                            .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION)
                            .setDescription(description)
                            .putAllMetadata(metaDataMap)
                            .build();
        } else {
            paymentIntentCreateParams =
                    PaymentIntentCreateParams.builder()
                            .setCurrency("usd")
                            .setCustomer(customer_id)
                            .setAmount((long) clientCheckoutDTO.getBalance() * 100)
                            .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
                            .addPaymentMethodType("card_present")
                            .setDescription(description)
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

        SetupIntentCreateParams setupIntentParams =
                SetupIntentCreateParams.builder()
                        .addPaymentMethodType("card_present")
                        .setCustomer(customer_id)
                        .build();;

        SetupIntent setupIntent =
                SetupIntent.create(setupIntentParams);

        String setupIntentId = setupIntent.getId();

        ReaderProcessSetupIntentParams readerParams =
                ReaderProcessSetupIntentParams.builder()
                        .setSetupIntent(setupIntentId)
                        .setCustomerConsentCollected(true)
                        .build();

        Reader updatedReader =
                readerResource.processSetupIntent(readerParams);

        updatedReader.toJson();

    }

    private Reader getReader() throws StripeException {
        //TODO:
        // 1. Replace this simulated reader ID ("tmr_FPKgUQOJ7fdmwi") with physical reader ID
        return Reader.retrieve("tmr_FPKgUQOJ7fdmwi");
//        return Reader.retrieve("tmr_FP6wARXsBdbits");
    }

    @Override
    public void addPaymentMethodManually(int clientId, PaymentMethodOptions paymentMethodOption) throws StripeException {

        SetStripeKey();

        //  Find customer ID for Client, create a customer ID if needed.
        int retrievedClientId = clientId;

        String customer_id = getCustomerIdString(retrievedClientId);

        Map<String, Object> card = new HashMap<>();
        card.put("number", paymentMethodOption.getCardNumber());
        card.put("exp_month", paymentMethodOption.getExpirationMonth());
        card.put("exp_year", paymentMethodOption.getExpirationYear());
        card.put("cvc", paymentMethodOption.getCvc());

        Map<String, Object> paymentMethodParams = new HashMap<>();
        paymentMethodParams.put("type", "card");
        paymentMethodParams.put("card", card);

        PaymentMethod paymentMethod =
                PaymentMethod.create(paymentMethodParams);

        Map<String, Object> automaticPaymentMethods =
                new HashMap<>();
        automaticPaymentMethods.put("enabled", true);
        Map<String, Object> setupIntentParams = new HashMap<>();
        setupIntentParams.put(
                "automatic_payment_methods",
                automaticPaymentMethods
        );
        setupIntentParams.put("customer", customer_id);
        setupIntentParams.put("payment_method", paymentMethod);

        SetupIntent.create(setupIntentParams);

    }

    @Override
    public void updateCustomerEmail(String customerId, String newEmail) {
        SetStripeKey();

        try {
            Customer customer = Customer.retrieve(customerId);

            Map<String, Object> params = new HashMap<>();
            params.put("email", newEmail);

            Customer updatedCustomer = customer.update(params);
        } catch (StripeException e) {
            System.out.println("Error retrieving customer from stripe in updating their email");
        }

    }

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

    private Customer getCustomerForSubscriptionByClientId(int retrievedClientId, String emailForReceipt) throws StripeException {
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

        // save email here
        if (emailForReceipt.length()>0) {

            Customer customer =
                    Customer.retrieve(customer_id);

            Map<String, Object> params = new HashMap<>();
            params.put("email", emailForReceipt);

            Customer updatedCustomer = customer.update(params);
        }

        return Customer.retrieve(customer_id);
    }
}
