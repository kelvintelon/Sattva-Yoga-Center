package com.sattvayoga.controller;

import com.sattvayoga.dao.*;
import com.sattvayoga.dto.order.CheckoutItemDTO;
import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.PackageDetails;
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
import java.util.ArrayList;
import java.util.List;

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
//            System.out.println(substringWebHook);
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
            System.out.println(event.getApiVersion());

            // Deserialization failed, probably due to an API version mismatch.
            // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
            // instructions on how to handle this case, or return an error here.
        }

        switch (event.getType()) {
            case "checkout.session.completed":

                // If the mode isn't subscription
                if (((Session) stripeObject).getMode().equals("payment")) {


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

                    // After you grab line Items pass them in through a method that saves them into the DB
                    packagePurchaseDao.purchaseLineItems(checkoutItemDTOList);

                    break;
                }
                break;
            case "invoice.paid":

                List<CheckoutItemDTO> checkoutItemDTOList = getSubscriptionCheckoutItemDTOList((Invoice) stripeObject);

                packagePurchaseDao.purchaseLineItems(checkoutItemDTOList);

                break;
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
            checkoutItemDTO.setPrice(currentInvoiceLineItem.getPrice().getUnitAmount()/100.00);

            clientId = getClientId(clientId, customerId);

            checkoutItemDTO.setClient_id(clientId);
            checkoutItemDTO.setPackage_id(packageId);
            checkoutItemDTO.setClasses_remaining(packageDetails.getClasses_amount());
            checkoutItemDTO.setTotal_amount_paid(BigDecimal.valueOf(checkoutItemDTO.getPrice()));
            checkoutItemDTO.setIs_monthly_renew(true);
            checkoutItemDTO.setPaymentId(paymentIntentId);

            checkoutItemDTOList.add(checkoutItemDTO);

        }
        return checkoutItemDTOList;
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
