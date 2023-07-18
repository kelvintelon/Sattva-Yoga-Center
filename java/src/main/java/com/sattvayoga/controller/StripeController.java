package com.sattvayoga.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.LineItem;
import com.stripe.model.LineItemCollection;
import com.stripe.model.checkout.Session;
import com.sattvayoga.dao.StripeDao;
import com.sattvayoga.dao.UserDao;
import com.sattvayoga.dto.order.CheckoutItemDTO;
import com.sattvayoga.dto.order.StripeResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    private StripeDao stripeDao;

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
        for(LineItem each: list){
            System.out.println(each.getDescription());
            System.out.println(each.getQuantity());
            System.out.println(each.getAmountSubtotal());
            System.out.println(each.getAmountTotal());
        }

        return new ResponseEntity<>(stripeResponse, HttpStatus.OK);
    }



}
