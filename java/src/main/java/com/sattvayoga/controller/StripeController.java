package com.sattvayoga.controller;

import com.sattvayoga.dao.*;
import com.sattvayoga.dto.order.ClientCheckoutDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.sattvayoga.dto.order.CheckoutItemDTO;
import com.sattvayoga.dto.order.StripeResponse;
//import com.stripe.param.terminal.ReaderPresentPaymentMethodParams;
//import com.stripe.param.terminal.ReaderProcessPaymentIntentParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/stripe")
@PreAuthorize("isAuthenticated()")
public class StripeController {


    @Autowired
    private StripeDao stripeDao;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<StripeResponse> checkoutList(@RequestBody List<CheckoutItemDTO> checkoutItemDTOList) throws StripeException {

        Session session = stripeDao.createSession(checkoutItemDTOList);
        StripeResponse stripeResponse = new StripeResponse(session.getId(), session.getPaymentIntent());
        Session retrieved = Session.retrieve(session.getId());
        Map<String, Object> params = new HashMap<>();
        LineItemCollection lineItems = retrieved.listLineItems(params);
        List<LineItem> list = new ArrayList<>();
        list.addAll(lineItems.getData());

        return new ResponseEntity<>(stripeResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/purchaseTerminal")
    @ResponseStatus(HttpStatus.CREATED)
    public String purchaseTotalThroughTerminal(@RequestBody ClientCheckoutDTO clientCheckoutDTO) throws StripeException, IOException {


       return stripeDao.processClientPurchaseThroughAdmin(clientCheckoutDTO);

    }


}
