package com.sattvayoga.controller;

import com.sattvayoga.dao.*;
import com.sattvayoga.model.ClientDetails;
import com.stripe.Stripe;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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



    // current usage
    @PostMapping("/purchaseLocalStorageItems")
    @ResponseStatus(HttpStatus.CREATED)
    public void purchaseLocalStorageItems(@RequestBody List<CheckoutItemDTO> itemList, Principal principal) {
        for (CheckoutItemDTO eachPackage : itemList) {
            if (eachPackage.getProductName().contains("Gift")) {
                String code = packagePurchaseDao.generateGiftCardCode();
                packagePurchaseDao.createGiftCard(code, eachPackage.getPrice());
                packagePurchaseDao.createGiftCardPurchase(eachPackage);
                ClientDetails clientDetails =
                        clientDetailsDao.findClientByUserId(
                                userDao.findIdByUsername(
                                        principal.getName()));

                // call the email service here and shoot off the gift code.
                try {
                    senderService.sendEmail(clientDetails.getEmail(), "Sattva Yoga Center Gift Card Code", "Your Gift Card code is: " + code + " . Please note: The Gift Card Code can only be redeemed in person. Once redeemed, it cannot be used by anyone else.");
                } catch (Throwable e) {
                    System.out.println("Error sending gift card email to client id: " + clientDetails.getClient_id());
                }
            } else if (eachPackage.getProductName().contains("One") && eachPackage.isIs_monthly_renew()) {
                packagePurchaseDao.createOneMonthAutoRenewPurchase(eachPackage);
            } else if (eachPackage.getProductName().contains("Six") && eachPackage.isIs_monthly_renew()) {
                packagePurchaseDao.createSixMonthAutoRenewPurchase(eachPackage);
            } else {
                packagePurchaseDao.createStripePackagePurchase(eachPackage);
            }
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/purchaseTerminal")
    @ResponseStatus(HttpStatus.CREATED)
    public String purchaseTotalThroughTerminal(@RequestBody int total) throws StripeException {
        Stripe.apiKey = apiKey;

        Reader readerResource = Reader.retrieve("tmr_FPKgUQOJ7fdmwi");

        PaymentIntentCreateParams paymentIntentCreateParams =
                PaymentIntentCreateParams.builder()
                        .setCurrency("usd")
                        .addPaymentMethodType("card_present")
                        .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
                        .setAmount((long)total*100)
                        .build();

        // creates payment intent
        PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentCreateParams);

        ReaderProcessPaymentIntentParams params =
                ReaderProcessPaymentIntentParams.builder().setPaymentIntent(paymentIntent.getId()).build();

        readerResource.processPaymentIntent(params);

        Reader reader = readerResource.getTestHelpers().presentPaymentMethod();

        return reader.getStatus();
    }

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
