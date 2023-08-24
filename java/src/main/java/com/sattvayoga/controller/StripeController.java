package com.sattvayoga.controller;

import com.sattvayoga.dao.JdbcPackagePurchaseDao;
import com.sattvayoga.dao.PackagePurchaseDao;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.LineItem;
import com.stripe.model.LineItemCollection;
import com.stripe.model.checkout.Session;
import com.sattvayoga.dao.StripeDao;
import com.sattvayoga.dao.UserDao;
import com.sattvayoga.dto.order.CheckoutItemDTO;
import com.sattvayoga.dto.order.StripeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/stripe")
public class StripeController {
    @Autowired
    UserDao userDao;
    @Autowired
    PackagePurchaseDao packagePurchaseDao;
    @Autowired
    private StripeDao stripeDao;

    @Value("${STRIPE_SECRET_KEY}")
    private String apiKey;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<StripeResponse> checkoutList(@RequestBody List<CheckoutItemDTO> checkoutItemDTOList) throws StripeException {
        Session session = stripeDao.createSession(checkoutItemDTOList);
        StripeResponse stripeResponse = new StripeResponse(session.getId());
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

    // just testing with sessionid. not using it yet.
    @GetMapping("/updateDbAfterPurchase/{sessionId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateDatabase(@PathVariable String sessionId) throws StripeException {
        Stripe.apiKey = apiKey;

        Session retrieved = Session.retrieve(sessionId);
        Map<String, Object> params = new HashMap<>();
        LineItemCollection lineItems = retrieved.listLineItems(params);
//        System.out.println("line items : "+ lineItems);
        List<LineItem> list = new ArrayList<>();
        list.addAll(lineItems.getData());
        for(LineItem each: list){
            System.out.println(each.getDescription());
            System.out.println(each.getQuantity());
            System.out.println(each.getAmountSubtotal());
            System.out.println(each.getAmountTotal());
        }
    }

    // current usage
    @PostMapping("/purchaseLocalStorageItems")
    @ResponseStatus(HttpStatus.CREATED)
    public void purchaseLocalStorageItems(@RequestBody List<CheckoutItemDTO> itemList){
        for (CheckoutItemDTO each: itemList) {
            if(each.getProductName().contains("Gift")){
                String code = packagePurchaseDao.generateGiftCardCode();
                packagePurchaseDao.createGiftCard(code, each.getPrice());
            }else{
                packagePurchaseDao.createPackagePurchase2(each);
            }
        }
    }

    @PostMapping("/purchaseOneMonth")
    @ResponseStatus(HttpStatus.CREATED)
    public void purchaseOneMonth(@RequestBody CheckoutItemDTO oneMonth){
        packagePurchaseDao.createOneMonthPurchase(oneMonth);
    }

    @PostMapping("/purchaseSixMonth")
    @ResponseStatus(HttpStatus.CREATED)
    public void purchaseSixMonth(@RequestBody CheckoutItemDTO sixMonth){
        packagePurchaseDao.createOneMonthPurchase(sixMonth);
    }



}
