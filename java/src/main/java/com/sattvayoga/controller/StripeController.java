package com.sattvayoga.controller;

import com.google.gson.Gson;
import com.sattvayoga.dao.*;
import com.sattvayoga.dto.order.ClientCheckoutDTO;
import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.PackageDetails;
import com.stripe.Stripe;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.LineItem;
import com.stripe.model.LineItemCollection;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.sattvayoga.dto.order.CheckoutItemDTO;
import com.sattvayoga.dto.order.StripeResponse;
import com.stripe.model.terminal.Reader;
import com.stripe.param.PaymentIntentCreateParams;
//import com.stripe.param.terminal.ReaderPresentPaymentMethodParams;
//import com.stripe.param.terminal.ReaderProcessPaymentIntentParams;
import com.stripe.param.terminal.ReaderPresentPaymentMethodParams;
import com.stripe.param.terminal.ReaderProcessPaymentIntentParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/stripe")
@PreAuthorize("isAuthenticated()")
public class StripeController {

    @Autowired
    UserDao userDao;
    @Autowired
    PackagePurchaseDao packagePurchaseDao;
    @Autowired
    ClientDetailsDao clientDetailsDao;
    @Autowired
    private EmailSenderService senderService;
    @Autowired
    private StripeDao stripeDao;

    @Value("${STRIPE_SECRET_KEY}")
    private String apiKey;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<StripeResponse> checkoutList(@RequestBody List<CheckoutItemDTO> checkoutItemDTOList) throws StripeException {
        Session session = stripeDao.createSession(checkoutItemDTOList);
        StripeResponse stripeResponse = new StripeResponse(session.getId(), session.getPaymentIntent());
        Session retrieved = Session.retrieve(session.getId());
        Map<String, Object> params = new HashMap<>();
        LineItemCollection lineItems = retrieved.listLineItems(params);
//        System.out.println("line items : "+ lineItems);
        List<LineItem> list = new ArrayList<>();
        list.addAll(lineItems.getData());
//        for(LineItem each: list){
//            System.out.println(each.getDescription());
//            System.out.println(each.getQuantity());
//            System.out.println(each.getAmountSubtotal());
//            System.out.println(each.getAmountTotal());
//        }

        return new ResponseEntity<>(stripeResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/purchaseTerminal")
    @ResponseStatus(HttpStatus.CREATED)
    public String purchaseTotalThroughTerminal(@RequestBody ClientCheckoutDTO clientCheckoutDTO) throws StripeException {
        Stripe.apiKey = apiKey;

        Reader readerResource = Reader.retrieve("tmr_FPKgUQOJ7fdmwi");

        //TODO:
        // 1. Pull in all data
        // 2. Set the email for the receipt?

        PaymentIntentCreateParams paymentIntentCreateParams =
                PaymentIntentCreateParams.builder()
                        .setCurrency("usd")
                        .addPaymentMethodType("card_present")
                        .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
                        .setAmount((long)clientCheckoutDTO.getTotal()*100)
                        //                 .setReceiptEmail(clientCheckoutDTO.getEmail())
                        .build();

        // creates payment intent
        PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentCreateParams);

        ReaderProcessPaymentIntentParams params =
                ReaderProcessPaymentIntentParams.builder().setPaymentIntent(paymentIntent.getId()).build();


        int attempt = 0;
        int tries = 3;
        while (true) {
            attempt++;
            try {
                readerResource.processPaymentIntent(params);

                //TODO: Comment out the following line for a physical reader
                Reader reader = readerResource.getTestHelpers().presentPaymentMethod();

                // TODO: You can return any string
                return reader.toJson();
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
        }
        // reader.getLastResponse().code() returns 200 successful

    }

    // current usage
//    @PostMapping("/purchaseLocalStorageItems")
//    @ResponseStatus(HttpStatus.CREATED)
//    public void purchaseLocalStorageItems(@RequestBody List<CheckoutItemDTO> itemList, Principal principal) {
//        for (CheckoutItemDTO eachPackage : itemList) {
//            if (eachPackage.getProductName().contains("Gift")) {
//                String code = packagePurchaseDao.generateGiftCardCode();
//                packagePurchaseDao.createGiftCard(code, eachPackage.getPrice());
//                packagePurchaseDao.createGiftCardPurchase(eachPackage);
//                ClientDetails clientDetails =
//                        clientDetailsDao.findClientByUserId(
//                                userDao.findIdByUsername(
//                                        principal.getName()));
//
//                // call the email service here and shoot off the gift code.
//                try {
//                    senderService.sendEmail(clientDetails.getEmail(), "Sattva Yoga Center Gift Card Code", "Your Gift Card code is: " + code + " . Please note: The Gift Card Code can only be redeemed in person. Once redeemed, it cannot be used by anyone else.");
//                } catch (Throwable e) {
//                    System.out.println("Error sending gift card email to client id: " + clientDetails.getClient_id());
//                }
//            } else if (eachPackage.getProductName().contains("One") && eachPackage.isIs_monthly_renew()) {
//                packagePurchaseDao.createOneMonthAutoRenewPurchase(eachPackage);
//            } else if (eachPackage.getProductName().contains("Six") && eachPackage.isIs_monthly_renew()) {
//                packagePurchaseDao.createSixMonthAutoRenewPurchase(eachPackage);
//            } else {
//                packagePurchaseDao.createStripePackagePurchase(eachPackage);
//            }
//        }
//    }

//    @GetMapping("/getInformation")
//    public String getSessionInformation() throws StripeException {
//        Stripe.apiKey = apiKey;
//
//        List<String> expandList = new ArrayList<>();
//        expandList.add("customer");
//        expandList.add("subscription");
//
//        Map<String, Object> params = new HashMap<>();
//        params.put("expand", expandList);
//        Session session = Session.retrieve("cs_test_a1K8qTFBdtg2pp2MSNAUXk79O1E3TK1B4kRodonA1AYmJAYtBTXn7JBUiW");
//
//        return session.getId();
//    }





//

//    // just testing with sessionid. not using it yet.
//    @GetMapping("/updateDbAfterPurchase/{sessionId}")
//    @ResponseStatus(HttpStatus.CREATED)
//    public void updateDatabase(@PathVariable String sessionId) throws StripeException {
//        Stripe.apiKey = apiKey;
//
//        Session retrieved = Session.retrieve(sessionId);
//        Map<String, Object> params = new HashMap<>();
//        LineItemCollection lineItems = retrieved.listLineItems(params);
////        System.out.println("line items : "+ lineItems);
//        List<LineItem> list = new ArrayList<>();
//        list.addAll(lineItems.getData());
//        for (LineItem each : list) {
//            System.out.println(each.getDescription());
//            System.out.println(each.getQuantity());
//            System.out.println(each.getAmountSubtotal());
//            System.out.println(each.getAmountTotal());
//        }
 //   }


//    @PostMapping("/purchaseOneMonth")
//    @ResponseStatus(HttpStatus.CREATED)
//    public void purchaseOneMonth(@RequestBody CheckoutItemDTO oneMonth) {
//        packagePurchaseDao.createOneMonthAutoRenewPurchase(oneMonth);
//    }
//
//    @PostMapping("/purchaseSixMonth")
//    @ResponseStatus(HttpStatus.CREATED)
//    public void purchaseSixMonth(@RequestBody CheckoutItemDTO sixMonth) {
//        packagePurchaseDao.createSixMonthAutoRenewPurchase(sixMonth);
//    }

//    @PostMapping("/create-subscription-checkout-session")
//    public ResponseEntity<StripeResponse> checkoutSubscriptionList(@RequestBody List<CheckoutSubscriptionItemDTO> checkoutItemDTOList) throws StripeException {
//        Session session = stripeDao.createSubscriptionSession(checkoutItemDTOList);
//        StripeResponse stripeResponse = new StripeResponse(session.getId(), session.getPaymentIntent());
//        Session retrieved = Session.retrieve(session.getId());
//        Map<String, Object> params = new HashMap<>();
//        LineItemCollection lineItems = retrieved.listLineItems(params);
////        System.out.println("line items : "+ lineItems);
//        List<LineItem> list = new ArrayList<>();
//        list.addAll(lineItems.getData());
////        for(LineItem each: list){
////            System.out.println(each.getDescription());
////            System.out.println(each.getQuantity());
////            System.out.println(each.getAmountSubtotal());
////            System.out.println(each.getAmountTotal());
////        }
//
//        return new ResponseEntity<>(stripeResponse, HttpStatus.OK);
//    }
}
