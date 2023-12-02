package com.sattvayoga.controller;

import com.sattvayoga.dao.*;
import com.sattvayoga.dto.order.CheckoutItemDTO;
import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.GiftCard;
import com.sattvayoga.model.PackageDetails;
import com.sattvayoga.model.Transaction;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionListLineItemsParams;
import com.stripe.param.checkout.SessionRetrieveParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@RestController
public class WebHookController {

    @Autowired
    ClientDetailsDao clientDetailsDao;

    @Autowired
    PackageDetailsDao packageDetailsDao;


    @Autowired
    PackagePurchaseDao packagePurchaseDao;

    // TODO: If you want to deploy uncomment below
//    @Autowired
//    private SecretManagerService secretManagerService;

    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    public ResponseEntity<String> webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws Throwable {
        Event event = null;

        // TODO: Use The Secrets Manager to retrieve the secret instead of using this hardcoded value
        try {
            event = Webhook.constructEvent(payload, sigHeader, "whsec_cff6694ca11a70540e6c09f04d2495ae35f8a8cb3e63a8cb280ff270f522dac2");
//            String webHookSecret = secretManagerService.getWebHookSecret();
//            String substringWebHook = webHookSecret.substring(18, webHookSecret.length()-2);
//            event = Webhook.constructEvent(payload,sigHeader,substringWebHook);

        } catch (SignatureVerificationException e) {
            System.out.println("Failed signature verification");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;

        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            System.out.println("Deserialization failed");
//            System.out.println(event.getApiVersion());

            // Deserialization failed, probably due to an API version mismatch.
            // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
            // instructions on how to handle this case, or return an error here.
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                if (stripeObject != null && ((PaymentIntent) stripeObject).getMetadata().get("process") != null && ((PaymentIntent) stripeObject).getMetadata().get("process").equals("admin")) {
                    int clientId = 0;

                    // Get Payment ID and Customer ID
                    String paymentIntentId = ((PaymentIntent) stripeObject).getId();
                    String customerId = ((PaymentIntent) stripeObject).getCustomer();
                    clientId = getClientId(clientId, customerId);

                    Map<String, String> metaDataMap = ((PaymentIntent) stripeObject).getMetadata();


                    metaDataMap.remove("process");

                    List<Transaction> listOfTransactions = new ArrayList<>();

                    // Gift Card Code being used logic
                    if (metaDataMap.get("giftCodeUsed") != null) {

                        Transaction transaction = new Transaction();

                        String giftCodeUsed = metaDataMap.get("giftCodeUsed");
                        double doubleGiftAmountUsed = Double.valueOf(metaDataMap.get("giftAmountUsed"));
                        int giftAmountUsed = (int) doubleGiftAmountUsed;

                        transaction.setPayment_amount(doubleGiftAmountUsed);
                        transaction.setPayment_type("Gift Card Code");
                        transaction.setGift_code(giftCodeUsed);

                        metaDataMap.remove("giftCodeUsed");
                        metaDataMap.remove("giftAmountUsed");



                        transaction.setClient_id(clientId);

                        GiftCard originalGiftCard = packagePurchaseDao.retrieveGiftCard(giftCodeUsed);

                        packagePurchaseDao.updateGiftCard(originalGiftCard,clientId,doubleGiftAmountUsed);
                        listOfTransactions.add(transaction);
                    }

                    // Stripe Swipe or Keyed/Stored logic out the way

                    if (metaDataMap.get("stripeKeyedStored") != null) {
                        Transaction transaction = new Transaction();

                        transaction.setClient_id(clientId);
                        transaction.setPayment_type("Credit Card Keyed/Stored");
                        transaction.setPayment_amount(Double.valueOf(metaDataMap.get("stripeKeyedStored")));
                        metaDataMap.remove("stripeKeyedStored");
                        listOfTransactions.add(transaction);
                    }
                    if (metaDataMap.get("stripeSwiped") != null){
                        Transaction transaction = new Transaction();

                        transaction.setClient_id(clientId);
                        transaction.setPayment_type("Credit Card Swiped");
                        transaction.setPayment_amount(Double.valueOf(metaDataMap.get("stripeSwiped")));
                        metaDataMap.remove("stripeSwiped");
                        listOfTransactions.add(transaction);
                    }
                    if (metaDataMap.get("cash") != null) {
                        Transaction transaction = new Transaction();

                        transaction.setClient_id(clientId);
                        transaction.setPayment_type("Cash");
                        transaction.setPayment_amount(Double.valueOf(metaDataMap.get("cash")));
                        metaDataMap.remove("cash");
                        listOfTransactions.add(transaction);
                    }
                    if (metaDataMap.get("check") != null) {
                        Transaction transaction = new Transaction();

                        transaction.setClient_id(clientId);
                        transaction.setPayment_type("Check");
                        transaction.setPayment_amount(Double.valueOf(metaDataMap.get("check")));
                        metaDataMap.remove("check");
                        listOfTransactions.add(transaction);
                    }



                    List<CheckoutItemDTO> listOfItemsToCheckout = new ArrayList<>();

                    // Get Gift Card Purchase logic out the way

                    String giftCardEmail = "";
                    boolean saveGiftCard = false;


                    if (metaDataMap.get("Gift Card") != null) {
                        giftCardEmail = metaDataMap.get("giftCardEmail");
//                        saveGiftCard = Boolean.parseBoolean(metaDataMap.get("saveGiftCardEmail"));
                        metaDataMap.remove("giftCardEmail");
//                        metaDataMap.remove("saveGiftCardEmail");
                    }

                    // Get receipt logic out the way

                    String receiptEmail = "";
                    boolean saveReceiptEmail = false;
                    if (metaDataMap.get("saveReceiptEmail") != null) {
                        saveReceiptEmail = true;
                        receiptEmail = metaDataMap.get("saveReceiptEmail");
                        metaDataMap.remove("saveReceiptEmail");
                    }



                    // By this point the map should only have packages
                    for (Map.Entry<String,String> mapElement : metaDataMap.entrySet()) {

                        String keyDescription = mapElement.getKey();
                        String value = mapElement.getValue();

                        String[] valueArray = value.split(",");
                        int totalPaid = Integer.valueOf(valueArray[0]);
                        int discount = Integer.valueOf(valueArray[1]);

                        // TODO: Check for multiple quantities here
                        if (keyDescription.contains("#")) {
                            keyDescription = keyDescription.substring(0,keyDescription.length()-3);
                        }

                        // Find original package details
                        PackageDetails currentPackageDetails = packageDetailsDao.findPackageByPackageName(keyDescription);

                        CheckoutItemDTO checkoutItemDTO = new CheckoutItemDTO();

                        // Gift Card Purchase Logic
                        if (currentPackageDetails.getDescription().contains("Gift")) {
                            checkoutItemDTO.setGiftCardEmail(giftCardEmail);
                            checkoutItemDTO.setSaveGiftCardEmail(saveGiftCard);
                        }

                        // Email receipt logic
                        checkoutItemDTO.setSaveReceiptEmail(saveReceiptEmail);
                        checkoutItemDTO.setReceiptEmail(receiptEmail);

                        checkoutItemDTO.setProductName(keyDescription);
                        checkoutItemDTO.setPrice(totalPaid);
                        checkoutItemDTO.setTotal_amount_paid(BigDecimal.valueOf(totalPaid));
                        checkoutItemDTO.setClient_id(clientId);
                        checkoutItemDTO.setPackage_id(currentPackageDetails.getPackage_id());
                        checkoutItemDTO.setClasses_remaining(currentPackageDetails.getClasses_amount());
                        checkoutItemDTO.setIs_monthly_renew(false);
                        checkoutItemDTO.setDiscount(discount);
                        checkoutItemDTO.setPaymentId(paymentIntentId);
                        checkoutItemDTO.setUnlimited(currentPackageDetails.isUnlimited());
                        checkoutItemDTO.setPackageDuration(currentPackageDetails.getPackage_duration());
                        listOfItemsToCheckout.add(checkoutItemDTO);

                    }

                    packagePurchaseDao.purchaseLineItems(listOfItemsToCheckout, listOfTransactions);

                }
            case "checkout.session.completed":

                // If the mode isn't subscription
                if (stripeObject instanceof Session && ((Session) stripeObject).getMode().equals("payment")) {


                    SessionRetrieveParams params =
                            SessionRetrieveParams.builder()
                                    .addExpand("line_items")
                                    .build();

                    Session session = Session.retrieve(((Session) stripeObject).getId(), params, null);

                    SessionListLineItemsParams listLineItemsParams =
                            SessionListLineItemsParams.builder()
                                    .build();

                    LineItemCollection lineItems = session.listLineItems(listLineItemsParams);

                    List<CheckoutItemDTO> checkoutItemDTOList = getCheckoutItemDTOList((Session) stripeObject, lineItems);

                    double amountPaid = 0;

                    for (CheckoutItemDTO checkoutItemDTO : checkoutItemDTOList) {
                        amountPaid += checkoutItemDTO.getPrice();
                    }

                    List<Transaction> transactions = new ArrayList<>();
                    Transaction transaction = new Transaction();
                    transaction.setPayment_type("Online Payment");
                    transaction.setClient_id(checkoutItemDTOList.get(0).getClient_id());
                    transaction.setPayment_amount(amountPaid);
                    transactions.add(transaction);
                    // After you grab line Items pass them in through a method that saves them into the DB
                    packagePurchaseDao.purchaseLineItems(checkoutItemDTOList, transactions);

                    break;
                }
                break;
//            case "invoice.paid":
//
//                List<CheckoutItemDTO> checkoutItemDTOList = getSubscriptionCheckoutItemDTOList((Invoice) stripeObject);
//
//                packagePurchaseDao.purchaseLineItems(checkoutItemDTOList);
//
//                break;
            default:

                // Unexpected event type
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    private List<CheckoutItemDTO> getSubscriptionCheckoutItemDTOList(Invoice stripeObject) {
        int clientId = 0;

        // Get Payment ID and Customer ID
        String paymentIntentId = stripeObject.getPaymentIntent();
        String customerId = stripeObject.getCustomer();

        // Get Line Items

        InvoiceLineItemCollection invoiceLineItemCollection = stripeObject.getLines();

        List<InvoiceLineItem> invoiceLineItemList = invoiceLineItemCollection.getData();

        List<CheckoutItemDTO> checkoutItemDTOList = new ArrayList<>();

        for (int i = 0; i < invoiceLineItemList.size(); i++) {
            InvoiceLineItem currentInvoiceLineItem = invoiceLineItemList.get(i);

            CheckoutItemDTO checkoutItemDTO = new CheckoutItemDTO();
            int packageId = 0;
            PackageDetails packageDetails
                    = packageDetailsDao.findPackageBySubscriptionDuration(currentInvoiceLineItem.getPlan().getIntervalCount().intValue());
            packageId = packageDetails.getPackage_id();

            checkoutItemDTO.setProductName(packageDetails.getDescription());

            setContractLengthForSubscriptionInCheckoutSession(currentInvoiceLineItem, checkoutItemDTO);

            checkoutItemDTO.setPrice(stripeObject.getAmountPaid()/100.00);

            clientId = getClientId(clientId, customerId);

            checkoutItemDTO.setClient_id(clientId);
            checkoutItemDTO.setPackage_id(packageId);


            Map<String, String> metaDataMap = new HashMap<>();
            if (currentInvoiceLineItem.getMetadata() != null) {
                metaDataMap = currentInvoiceLineItem.getMetadata();
            }


            int discountAmount = 0;
            if (metaDataMap.get("discountAmount") != null) {
                discountAmount = Integer.valueOf(metaDataMap.get("discountAmount"));
            }

            if (metaDataMap.get("giftCodeUsed") != null) {
                String giftCodeUsed = metaDataMap.get("giftCodeUsed");
                double doubleGiftAmountUsed = Double.valueOf(metaDataMap.get("giftAmountUsed"));
                double giftAmountUsed = doubleGiftAmountUsed;

                metaDataMap.remove("giftCodeUsed");
                metaDataMap.remove("giftAmountUsed");

                clientId = getClientId(clientId, customerId);

                GiftCard originalGiftCard = packagePurchaseDao.retrieveGiftCard(giftCodeUsed);

                packagePurchaseDao.updateGiftCard(originalGiftCard,clientId,giftAmountUsed);
            }

            checkoutItemDTO.setClasses_remaining(packageDetails.getClasses_amount());
            if (discountAmount > 0 ) {
                checkoutItemDTO.setDiscount(discountAmount);
            }
            checkoutItemDTO.setTotal_amount_paid(BigDecimal.valueOf(checkoutItemDTO.getPrice()));


            checkoutItemDTO.setIs_monthly_renew(true);
            checkoutItemDTO.setPaymentId(paymentIntentId);

            checkoutItemDTOList.add(checkoutItemDTO);

        }
        return checkoutItemDTOList;
    }

    private void setContractLengthForSubscriptionInCheckoutSession(InvoiceLineItem currentInvoiceLineItem, CheckoutItemDTO checkoutItemDTO) {
        // Get subscription;
        try {
            Subscription subscription = Subscription.retrieve(currentInvoiceLineItem.getSubscription());

            Map<String, Object> params = new HashMap<>();
            long cancelAtUnixTimestamp = 0;

            // If it doesn't have a cancel at timestamp yet
            if (subscription.getCancelAt() == null || !(subscription.getCancelAt() > 0)) {
                if (checkoutItemDTO.getProductName().equals("One Month Subscription")) {
                    LocalDate todayDate = LocalDate.now();
                    LocalDate futureDate = todayDate.plusMonths(1 * 12);

                    ZoneId zone = ZoneId.of("America/New_York");
                    LocalDateTime futureDateLDT = futureDate.atStartOfDay();
                    ZoneOffset zoneOffSet = zone.getRules().getOffset(futureDateLDT);

                    cancelAtUnixTimestamp = futureDate.toEpochSecond(LocalTime.from(futureDateLDT), zoneOffSet);


                } else {
                    LocalDate todayDate = LocalDate.now();
                    LocalDate futureDate = todayDate.plusMonths(6 * 2);

                    ZoneId zone = ZoneId.of("America/New_York");
                    LocalDateTime futureDateLDT = futureDate.atStartOfDay();
                    ZoneOffset zoneOffSet = zone.getRules().getOffset(futureDateLDT);

                    cancelAtUnixTimestamp = futureDate.toEpochSecond(LocalTime.from(futureDateLDT), zoneOffSet);


                }

                params.put("cancel_at", cancelAtUnixTimestamp);
                Subscription updatedSubscription =
                        subscription.update(params);
            }

        } catch (StripeException e) {
            System.out.println("Error updating subscription's contract length in checkout session.");
        }
    }

    private List<CheckoutItemDTO> getCheckoutItemDTOList(Session stripeObject, LineItemCollection lineItems) {
        List<LineItem> lineItemList = lineItems.getData();

        List<CheckoutItemDTO> checkoutItemDTOList = new ArrayList<>();

        int clientId = 0;

        // Find paymentIntent ID
        String paymentIntentId = stripeObject.getPaymentIntent();
        String customerId = stripeObject.getCustomer();

        for (int i = 0; i < lineItemList.size(); i++) {
            LineItem currentItem = lineItemList.get(i);
            CheckoutItemDTO checkoutItemDTO = new CheckoutItemDTO();
            checkoutItemDTO.setProductName(currentItem.getDescription());
            checkoutItemDTO.setQuantity(currentItem.getQuantity().intValue());
            checkoutItemDTO.setPrice(currentItem.getPrice().getUnitAmount()/100.00);


            ClientDetails clientDetails = clientDetailsDao.findClientByClientId(clientId);

            checkoutItemDTO.setGiftCardEmail(clientDetails.getEmail());
            checkoutItemDTO.setSaveGiftCardEmail(false);
            clientId = getClientId(clientId, customerId);
            checkoutItemDTO.setClient_id(clientId);

            int packageId = 0;
            PackageDetails packageDetails = packageDetailsDao.findPackageByPackageName(currentItem.getDescription());

            packageId = packageDetails.getPackage_id();
            checkoutItemDTO.setPackage_id(packageId);
            checkoutItemDTO.setClasses_remaining(packageDetails.getClasses_amount());
            checkoutItemDTO.setTotal_amount_paid(BigDecimal.valueOf(checkoutItemDTO.getPrice()));
            checkoutItemDTO.setIs_monthly_renew(packageDetails.isIs_recurring());
            checkoutItemDTO.setPaymentId(paymentIntentId);
            checkoutItemDTO.setPackageDuration(packageDetails.getPackage_duration());
            checkoutItemDTOList.add(checkoutItemDTO);
        }
        return checkoutItemDTOList;
    }

    private int getClientId(int clientId, String customerId) {
        if (clientId == 0) {
            // Find Client ID by Customer ID
            ClientDetails clientDetails = clientDetailsDao.findClientByCustomerId(customerId);
            clientId = clientDetails.getClient_id();
        }
        return clientId;
    }



}
