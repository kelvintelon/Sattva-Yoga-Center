package com.sattvayoga.controller;

import com.sattvayoga.dao.ClientDetailsDao;
import com.sattvayoga.dao.PackagePurchaseDao;
import com.sattvayoga.dao.UserDao;
import com.sattvayoga.dto.order.ResendEmailDTO;
import com.sattvayoga.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@CrossOrigin
public class PackagePurchaseController {

    private PackagePurchaseDao packagePurchaseDao;
    private UserDao userDao;
    private ClientDetailsDao clientDetailsDao;

    public PackagePurchaseController(PackagePurchaseDao packagePurchaseDao, UserDao userDao, ClientDetailsDao clientDetailsDao) {
        this.packagePurchaseDao = packagePurchaseDao;
        this.userDao = userDao;
        this.clientDetailsDao = clientDetailsDao;
    }

    @RequestMapping(value= "/userPackagePurchaseList", method = RequestMethod.GET)
    public List<PackagePurchase> getAllUserPackagePurchase(Principal principal) throws SQLException {
        return packagePurchaseDao.getAllUserPackagePurchases(userDao.findIdByUsername(principal.getName()));
    }

    @RequestMapping(value= "/activeUserPackagePurchaseList", method = RequestMethod.GET)
    public List<PackagePurchase> getAllActiveUserPackagePurchase(Principal principal) {
        return packagePurchaseDao.getAllActiveUserPackagePurchases(userDao.findIdByUsername(principal.getName()));
    }

    @RequestMapping(value= "/userPackagePurchaseListByClientId/{clientId}", method = RequestMethod.GET)
    public List<PackagePurchase> getAllUserPackagePurchaseByClientId(@PathVariable int clientId) throws SQLException {
        return packagePurchaseDao.getAllUserPackagePurchases(clientDetailsDao.findClientByClientId(clientId).getUser_id());
    }

    @RequestMapping(value= "/userPaginatedPackagePurchaseList", method = RequestMethod.GET)
    public PaginatedListOfPurchasedPackages getAllPaginatedUserPackagePurchase(Principal principal,
                                                                               @RequestParam(defaultValue = "1")  int page,
                                                                               @RequestParam(defaultValue = "10") int pageSize,
                                                                               @RequestParam(defaultValue = "date_purchased") String sortBy,
                                                                               @RequestParam(defaultValue = "false") boolean sortDesc) throws SQLException {
        int userId = userDao.findIdByUsername(principal.getName());
        return packagePurchaseDao.getAllUserPaginatedPackagePurchases(userId, page, pageSize, sortBy, sortDesc);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value= "/userPaginatedPackagePurchaseListByClientId/{clientId}", method = RequestMethod.GET)
    public PaginatedListOfPurchasedPackages getAllPaginatedUserPackagePurchaseByClientId(@PathVariable int clientId,
                                                                                         @RequestParam(defaultValue = "1")  int page,
                                                                                         @RequestParam(defaultValue = "10") int pageSize,
                                                                                         @RequestParam(defaultValue = "date_purchased") String sortBy,
                                                                                         @RequestParam(defaultValue = "true") boolean sortDesc) throws SQLException {
        int userId = clientDetailsDao.findClientByClientId(clientId).getUser_id();
        return packagePurchaseDao.getAllUserPaginatedPackagePurchases(userId, page, pageSize, sortBy, sortDesc);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value= "/userActivePaginatedPackagePurchaseListByClientId/{clientId}", method = RequestMethod.GET)
    public PaginatedListOfPurchasedPackages getAllActivePaginatedUserPackagePurchaseByClientId(@PathVariable int clientId,
                                                                                         @RequestParam(defaultValue = "1")  int page,
                                                                                         @RequestParam(defaultValue = "10") int pageSize,
                                                                                         @RequestParam(defaultValue = "package_purchase_id") String sortBy,
                                                                                         @RequestParam(defaultValue = "false") boolean sortDesc) throws SQLException {
        int userId = clientDetailsDao.findClientByClientId(clientId).getUser_id();
        return packagePurchaseDao.getAllActiveUserPaginatedPackagePurchases(userId, page, pageSize, sortBy, sortDesc);
    }

    @RequestMapping(value= "/userActivePaginatedPackagePurchaseListForLoggedInUser", method = RequestMethod.GET)
    public PaginatedListOfPurchasedPackages getAllActivePaginatedUserPackagePurchaseForLoggedInUser(Principal principal,
                                                                                               @RequestParam(defaultValue = "1")  int page,
                                                                                               @RequestParam(defaultValue = "10") int pageSize,
                                                                                               @RequestParam(defaultValue = "package_purchase_id") String sortBy,
                                                                                               @RequestParam(defaultValue = "false") boolean sortDesc) throws SQLException {
        int userId = userDao.findIdByUsername(principal.getName());
        return packagePurchaseDao.getAllActiveUserPaginatedPackagePurchases(userId, page, pageSize, sortBy, sortDesc);
    }

//    @ResponseStatus(HttpStatus.CREATED)
//    @RequestMapping(value = "/createPackagePurchase", method = RequestMethod.POST)
//    public void createPackagePurchase(@RequestBody PackagePurchase packagePurchase) {
//
//        packagePurchaseDao.createPackagePurchase(packagePurchase);
//    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/resendEmail")
    public void resendEmail(@RequestBody ResendEmailDTO resendEmailDTO) {
        packagePurchaseDao.resendEmail(resendEmailDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value= "/userPackagePurchaseListByUserId/{userId}", method = RequestMethod.GET)
    public List<PackagePurchase> getAllUserPackagePurchaseByUserId(@PathVariable int userId) throws SQLException {
        return packagePurchaseDao.getAllUserPackagePurchases(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/getAllActivePackagesToSwap/{clientId}", method = RequestMethod.GET)
    public List<PackagePurchase> getAllActivePackagesToSwap(@PathVariable int clientId) {
        return packagePurchaseDao.getAllActivatePackagesToSwap(clientId);
    }



    @GetMapping(path="/getAllSharedActiveQuantityPackages")
    public List<PackagePurchase> getAllSharedActiveQuantityPackages(Principal principal){
        int userId = userDao.findIdByUsername(principal.getName());
        int clientId = clientDetailsDao.findClientByUserId(userId).getClient_id();
        //TODO: Account for consolidation
        return packagePurchaseDao.getAllSharedActivePackages(clientId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path="/retrieveGiftCard/{clientId}/{code}")
    public GiftCardResponse retrieveGiftCard(@PathVariable int clientId, @PathVariable String code) {

        GiftCard giftCard = null;

        GiftCardResponse giftCardResponse = new GiftCardResponse();
        try {
            giftCard = packagePurchaseDao.retrieveGiftCard(code);

            if (giftCard.getClient_id() > 0 && giftCard.getClient_id() != clientId) {
                giftCardResponse.setMessage("Code already in use by client ID: " + giftCard.getClient_id());
                giftCardResponse.setAmountAvailable(0);
            } else {
                giftCardResponse.setMessage("Available: " + giftCard.getAmount());
                giftCardResponse.setAmountAvailable(giftCard.getAmount());
            }

        } catch (Exception e) {
            System.out.println("Error Message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new GiftCardNotFoundException();
        }


        return giftCardResponse;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path="/getAllSharedActiveQuantityPackagesByClientId/{clientId}")
    public List<PackagePurchase> getAllSharedActiveQuantityPackagesByClientId(@PathVariable int clientId){
        return packagePurchaseDao.getAllSharedActivePackages(clientId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value= "/expirePackage", method = RequestMethod.PUT)
    public boolean expirePackage(@RequestBody PackagePurchase packagePurchase) {
        return packagePurchaseDao.expirePackage(packagePurchase);
    }

    @RequestMapping(value= "/decrement/{packagePurchaseId}", method = RequestMethod.PUT)
    public boolean decrementByOne(@PathVariable int packagePurchaseId){
        return packagePurchaseDao.decrementByOne(packagePurchaseId);
    }

    @RequestMapping(value= "/updatePackagePurchase", method = RequestMethod.PUT)
    public boolean updatePackage(@RequestBody PackagePurchase packagePurchase) {
        return packagePurchaseDao.updatePackage(packagePurchase);
    }

//    @RequestMapping(value= "/increment/{packagePurchaseId}", method = RequestMethod.PUT)
//    public boolean incrementByOne(@PathVariable int packagePurchaseId){
//        return packagePurchaseDao.incrementByOne(packagePurchaseId);
//    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/swapPackages", method = RequestMethod.POST)
    public void swapPackages(@RequestBody SwapPackagesDTO packageDTO) {

        int oldId = packageDTO.getOld_package_purchase_id();
        int newId = packageDTO.getNew_package_purchase_id();
        int eventId = packageDTO.getEvent_id();
        packagePurchaseDao.swapPackages(oldId,newId, eventId);
    }

    static class GiftCardResponse {
        private double amountAvailable;
        private String message;

        public GiftCardResponse() {
        }

        public double getAmountAvailable() {
            return amountAvailable;
        }

        public void setAmountAvailable(double amountAvailable) {
            this.amountAvailable = amountAvailable;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    static class SwapPackagesDTO {
        private int new_package_purchase_id;
        private int old_package_purchase_id;
        private int event_id;

        public int getEvent_id() {
            return event_id;
        }

        public void setEvent_id(int event_id) {
            this.event_id = event_id;
        }

        public int getNew_package_purchase_id() {
            return new_package_purchase_id;
        }

        public void setNew_package_purchase_id(int new_package_purchase_id) {
            this.new_package_purchase_id = new_package_purchase_id;
        }

        public int getOld_package_purchase_id() {
            return old_package_purchase_id;
        }

        public void setOld_package_purchase_id(int old_package_purchase_id) {
            this.old_package_purchase_id = old_package_purchase_id;
        }
    }

}
