package com.sattvayoga.dao;

import com.sattvayoga.dto.order.CheckoutItemDTO;
import com.sattvayoga.dto.order.ClientCheckoutDTO;
import com.sattvayoga.dto.order.ResendEmailDTO;
import com.sattvayoga.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
public class JdbcPackagePurchaseDao implements PackagePurchaseDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPackagePurchaseDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    UserDao userDao;

    @Autowired
    ClientDetailsDao clientDetailsDao;

    @Autowired
    StripeDao stripeDao;

    @Autowired
    private EmailSenderService senderService;

    @Autowired
    SaleDao saleDao;

    @Autowired
    TransactionDao transactionDao;

    @Override
    public List<PackagePurchase> getAllUserPackagePurchases(int userId) {
        List<PackagePurchase> allUserPackagePurchase = new ArrayList<>();
        String sql = "SELECT * FROM package_purchase " +
                "JOIN client_details on client_details.client_id = package_purchase.client_id " +
                "WHERE client_details.user_id = ?" ;
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId);
        while (result.next()) {
            PackagePurchase packagePurchase = mapRowToPackagePurchase(result);

            // set package description
            packagePurchase.setPackage_description(getPackageDescriptionByPackageId(packagePurchase.getPackage_id()));
            packagePurchase.setUnlimited(IsSubscriptionOrNot(packagePurchase.getPackage_id()));
            allUserPackagePurchase.add(packagePurchase);
        }
        return allUserPackagePurchase;
    }

    @Override
    public PaginatedListOfPurchasedPackages getAllUserPaginatedPackagePurchases(int userId, int page, int pageSize, String sortBy, boolean sortDesc) {

        int offset = 0;
        String sortDirection = (sortDesc ? "DESC" : "ASC");

        String offsetString = "";
        if (page == 1) {
            offset = pageSize * (page);
            offsetString = " LIMIT ?";
        } else {
            offset = pageSize * (page-1);
            offsetString = " OFFSET ? LIMIT " + pageSize;
        }

        List<PackagePurchase> allUserPackagePurchase = new ArrayList<>();

        String sql = "SELECT * FROM package_purchase " +
                "JOIN client_details on client_details.client_id = package_purchase.client_id " +
                "WHERE client_details.user_id = ? ORDER BY package_purchase." + sortBy + " " + sortDirection + offsetString;
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId, offset);
        while (result.next()) {
            PackagePurchase packagePurchase = mapRowToPackagePurchase(result);

            // set package description
            String description = "";
            if (getPackageDescriptionByPackageId(packagePurchase.getPackage_id()) != null) {
                description = getPackageDescriptionByPackageId(packagePurchase.getPackage_id());
            }

            int saleId = 0;
            String sql2 = "SELECT sale_id FROM sales WHERE ? = ANY (packages_purchased_array);";
            SqlRowSet result2 = jdbcTemplate.queryForRowSet(sql2, packagePurchase.getPackage_purchase_id());
            if (result2.next()) {
                saleId = result2.getInt("sale_id");
            }

            String paymentDescriptions = "";
            String sql3 = "SELECT payment_type FROM transactions WHERE sale_id = ?";
            if (saleId > 0) {
                SqlRowSet result3 = jdbcTemplate.queryForRowSet(sql3, saleId);
                while (result3.next()) {
                    paymentDescriptions += result3.getString("payment_Type") + "/";
                }
            }
            if (paymentDescriptions.length() > 0) {
                paymentDescriptions = paymentDescriptions.substring(0,paymentDescriptions.length()-1);
            }
            packagePurchase.setPayment_description(paymentDescriptions);
            packagePurchase.setPackage_description(description);
            packagePurchase.setUnlimited(IsSubscriptionOrNot(packagePurchase.getPackage_id()));
            allUserPackagePurchase.add(packagePurchase);
        }

        String countSql = "SELECT COUNT(*) FROM package_purchase " +
                "JOIN client_details on client_details.client_id = package_purchase.client_id " +
                "WHERE client_details.user_id = ?" ;

        int count = jdbcTemplate.queryForObject(countSql, Integer.class, userId);

        PaginatedListOfPurchasedPackages paginatedListOfPurchasedPackages = new PaginatedListOfPurchasedPackages(allUserPackagePurchase,count);

        return paginatedListOfPurchasedPackages;
    }

    @Override
    public void resendEmail(ResendEmailDTO resendEmailDTO) {

        // You need all objects that were purchased within the same order which is in the Sale table.
        // Use the package_purchase_id for that.
        PackagePurchase packagePurchase = getPackagePurchaseObjectByPackagePurchaseId(resendEmailDTO.getPackage_purchase_id());
        ClientDetails clientDetails = clientDetailsDao.findClientByClientId(packagePurchase.getClient_id());
        String saleDate = packagePurchase.getDate_purchased().toString().split(" ")[0];
        String firstName = clientDetails.getFirst_name();
        String subject = "Receipt for Your Sattva Yoga Center LLC Purchase";

        int saleId = 0;

        List<PackagePurchase> listOfPackagesPurchased = new ArrayList<>();
        saleId = retrieveSaleAndPackagesPurchased(packagePurchase, saleId, listOfPackagesPurchased);

        double runningTotal = 0;
        double runningDiscount = 0;


        for (int i = 0; i < listOfPackagesPurchased.size(); i++) {
            PackagePurchase currentPackage = listOfPackagesPurchased.get(i);
            runningTotal += currentPackage.getTotal_amount_paid().doubleValue();
            runningDiscount += currentPackage.getDiscount().doubleValue();

        }

        String subTotal = "$" + runningTotal;
        String tax = "$0.00";
        String total = "$" + runningTotal;

        boolean compFree = false;
        String usedPaymentTypesPrototype = "";
        String usedPaymentTypes = setUsedPaymentTypes(usedPaymentTypesPrototype, compFree, runningTotal, saleId);

        String packagesBeingBoughtForEmail = "";
        int count = 1;
        for (int i = 0; i < listOfPackagesPurchased.size(); i++) {
            PackagePurchase currentPackage = listOfPackagesPurchased.get(i);

            double amountPaid = 0;

            if (!compFree) {
                amountPaid = currentPackage.getTotal_amount_paid().doubleValue() + currentPackage.getDiscount().doubleValue();
            }

            packagesBeingBoughtForEmail += count + ". " +  getPackageDescriptionByPackageId(currentPackage.getPackage_id()) + " - $" + amountPaid + " - " + "Expires on: " + currentPackage.getExpiration_date().toString() + "<br>";
            count++;
        }

        packagesBeingBoughtForEmail += "<br>";

        if (runningDiscount > 0) {
            packagesBeingBoughtForEmail += "Discount: $" + runningDiscount + "<br>";
        }


        //paymentType + "\t" + "$" + runningTotal + "\n";
        ClientCheckoutDTO clientCheckoutDTO = new ClientCheckoutDTO();
        clientCheckoutDTO.setSendEmail(true);
        clientCheckoutDTO.setEmailForReceipt(resendEmailDTO.getEmail());
        try {
            sendEmailReceipt(clientCheckoutDTO, packagesBeingBoughtForEmail, saleId, saleDate, firstName, subject, subTotal, tax, total, usedPaymentTypes);
        } catch (Throwable e) {
            System.out.println("Error sending gift card email to client id: " + clientCheckoutDTO.getClient_id());
        }
        // You need all the payment methods that were used, plug in the sale id into the transaction table to find that out.
    }

    public void sendEmailReceipt(ClientCheckoutDTO clientCheckoutDTO, String packagesBeingBoughtForEmail, int saleId, String saleDate, String firstName, String subject, String subTotal, String tax, String total, String usedPaymentTypes) {
        if (clientCheckoutDTO.getEmailForReceipt().length()>0 && clientCheckoutDTO.isSendEmail()) {

            String paymentDetails = "<b>Payment Method</b> -     " + "<b>Amount</b>" + "<br>" +
                    usedPaymentTypes + "<br>" + "<br>" + "      " + "Customer Copy" + "<br>";
            String body = "Dear, " + firstName + "<br>" +
                    "Thank you for shopping at our store. Below is your purchase receipt; please keep a copy for your records." + "<br>" +
                    "Sale Date:      " + saleDate + "<br>" +
                    "Sale ID:      " + saleId + "<br>" + "<br>" +
                    packagesBeingBoughtForEmail + "<br>" +
                    "Subtotal:      " + subTotal + "<br>" +
                    "Tax:      " + tax + "<br>" +
                    "Total:      " + total + "<br>" + "<br>" +
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

    private String setUsedPaymentTypes(String usedPaymentTypes, boolean compFree, double runningTotal, int saleId) {
        double cash = 0;
        double check = 0;
        double giftAmountUsed = 0;
        double creditCardSwiped = 0;
        double creditCardKeyedStored = 0;
        double onlinePayment = 0;

        // Get all Transactions by this point
        String sql = "SELECT * FROM transactions WHERE sale_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, saleId);

        while (result.next()) {
            if (result.getString("payment_type").equalsIgnoreCase("Comp/Free")) {
                compFree = true;
            }
            if (result.getString("payment_type").equalsIgnoreCase("Cash")) {
                cash = result.getDouble("payment_amount");
            }
            if (result.getString("payment_type").equalsIgnoreCase("Check")) {
                check = result.getDouble("payment_amount");
            }
            if (result.getString("payment_type").equalsIgnoreCase("Gift Card Code")) {
                giftAmountUsed = result.getDouble("payment_amount");
            }
            if (result.getString("payment_type").equalsIgnoreCase("Credit Card Swiped")) {
                creditCardSwiped = result.getDouble("payment_amount");
            }
            if (result.getString("payment_type").equalsIgnoreCase("Credit Card Keyed/Stored")) {
                creditCardKeyedStored = result.getDouble("payment_amount");
            }
            if (result.getString("payment_type").equalsIgnoreCase("Online Payment")) {
                onlinePayment = result.getDouble("payment_amount");
            }
        }

        if (compFree) {
            usedPaymentTypes += "Comp/Free" + "      " + "$" + runningTotal + "<br>";
        }

        if (cash > 0) {
            usedPaymentTypes += "Cash" + "      " + "$" + cash + "<br>";
        }
        if (check > 0) {
            usedPaymentTypes += "Check" + "      " + "$" + check + "<br>";
        }
        if (giftAmountUsed > 0) {
            usedPaymentTypes += "Gift Card Code" + "      " + "$" + giftAmountUsed + "<br>";
        }
        if (creditCardSwiped > 0) {
            usedPaymentTypes += "Credit Card Swiped" + "      " + "$" + creditCardSwiped + "<br>";
        }
        if (creditCardKeyedStored > 0) {
            usedPaymentTypes += "Credit Card Keyed/Stored" + "      " + "$" + creditCardKeyedStored + "<br>";
        }
        if (onlinePayment > 0) {
            usedPaymentTypes += "Online Payment" + "      " + "$" + onlinePayment + "<br>";
        }
        return usedPaymentTypes;
    }

    private int retrieveSaleAndPackagesPurchased(PackagePurchase packagePurchase, int saleId, List<PackagePurchase> listOfPackagesPurchased) {
        String sql = "SELECT * FROM sales WHERE ? = ANY (packages_purchased_array);";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, packagePurchase.getPackage_purchase_id());
        if (result.next()) {
            saleId = result.getInt("sale_id");

            Object newObject = result.getObject("packages_purchased_array");

            if (newObject instanceof Array) {
                Array tempArray = (Array) newObject;
                Object[] tempObjectArray = new Object[0];
                try {
                    tempObjectArray = (Object[]) tempArray.getArray();
                } catch (SQLException e) {
                    System.out.println("Error trying to retrieve array of packages purchased");
                }
                int[] packagePurchaseArray = new int[tempObjectArray.length];
                for (int i = 0; i < tempObjectArray.length; i++) {
                    packagePurchaseArray[i] = Integer.valueOf(tempObjectArray[i].toString());
                }
                for (int i = 0; i < packagePurchaseArray.length; i++) {
                    PackagePurchase packagePurchaseFromSale = getPackagePurchaseObjectByPackagePurchaseId(packagePurchaseArray[i]);
                    listOfPackagesPurchased.add(packagePurchaseFromSale);
                }
            }
        }
        return saleId;
    }

    @Override
    public PaginatedListOfPurchasedPackages getAllActiveUserPaginatedPackagePurchases(int userId, int page, int pageSize, String sortBy, boolean sortDesc) {
        int offset = 0;
        String sortDirection = (sortDesc ? "DESC" : "ASC");

        String offsetString = "";
        if (page == 1) {
            offset = pageSize * (page);
            offsetString = " LIMIT ?";
        } else {
            offset = pageSize * (page-1);
            offsetString = " OFFSET ? LIMIT " + pageSize;
        }

        List<PackagePurchase> allUserPackagePurchase = new ArrayList<>();

        String sql = "SELECT package_purchase_id, pp.client_id, pp.package_id, date_purchased, classes_remaining, activation_date, expiration_date, is_monthly_renew, total_amount_paid, discount FROM package_purchase as pp " +
                "JOIN package_details on pp.package_id = package_details.package_id " +
                "JOIN client_details on client_details.client_id = pp.client_id " +
                "WHERE client_details.user_id = ? " +
                "AND ( ( pp.classes_remaining > 0 AND pp.expiration_date > NOW()) " +
                "OR (package_details.unlimited = true AND pp.expiration_date > NOW()) ) " +
                "ORDER BY " + sortBy + " " + sortDirection + offsetString;

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId, offset);
        while (result.next()) {
            PackagePurchase packagePurchase = mapRowToPackagePurchase(result);

            int saleId = 0;
            String sql2 = "SELECT sale_id FROM sales WHERE ? = ANY (packages_purchased_array);";
            SqlRowSet result2 = jdbcTemplate.queryForRowSet(sql2, packagePurchase.getPackage_purchase_id());
            if (result2.next()) {
                saleId = result2.getInt("sale_id");
            }

            String paymentDescriptions = "";
            String sql3 = "SELECT payment_type FROM transactions WHERE sale_id = ?";
            if (saleId > 0) {
                SqlRowSet result3 = jdbcTemplate.queryForRowSet(sql3, saleId);
                while (result3.next()) {
                    paymentDescriptions += result3.getString("payment_Type") + " /";
                }
            }
            if (paymentDescriptions.length() > 0) {
                paymentDescriptions = paymentDescriptions.substring(0,paymentDescriptions.length()-1);
            }
            packagePurchase.setPayment_description(paymentDescriptions);

            // set package description
            packagePurchase.setPackage_description(getPackageDescriptionByPackageId(packagePurchase.getPackage_id()));
            packagePurchase.setUnlimited(IsSubscriptionOrNot(packagePurchase.getPackage_id()));
            allUserPackagePurchase.add(packagePurchase);
        }


        String countSql = "SELECT COUNT(*) FROM package_purchase as pp " +
                "JOIN package_details on pp.package_id = package_details.package_id " +
                "JOIN client_details on client_details.client_id = pp.client_id " +
                "WHERE client_details.user_id = ? " +
                "AND ( ( pp.classes_remaining > 0 AND pp.expiration_date > NOW()) " +
                "OR (package_details.unlimited = true AND pp.expiration_date > NOW()) ) ";

        int count = jdbcTemplate.queryForObject(countSql, Integer.class, userId);

        PaginatedListOfPurchasedPackages paginatedListOfPurchasedPackages = new PaginatedListOfPurchasedPackages(allUserPackagePurchase,count);

        return paginatedListOfPurchasedPackages;

    }

    @Override
    public List<PackagePurchase> getAllActiveUserPackagePurchases(int userId) {

        List<PackagePurchase> allUserPackagePurchase = new ArrayList<>();

        String sql = "SELECT package_purchase_id, pp.client_id, pp.package_id, date_purchased, classes_remaining, activation_date, expiration_date, is_monthly_renew, total_amount_paid, discount FROM package_purchase as pp " +
                "JOIN package_details on pp.package_id = package_details.package_id " +
                "JOIN client_details on client_details.client_id = pp.client_id " +
                "WHERE client_details.user_id = ? " +
                "AND ( ( pp.classes_remaining > 0 AND pp.expiration_date > NOW()) " +
                "OR (package_details.unlimited = true AND pp.expiration_date > NOW()) ) ";


        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId);
        while (result.next()) {
            PackagePurchase packagePurchase = mapRowToPackagePurchase(result);

            // set package description
            packagePurchase.setPackage_description(getPackageDescriptionByPackageId(packagePurchase.getPackage_id()));
            packagePurchase.setUnlimited(IsSubscriptionOrNot(packagePurchase.getPackage_id()));

            allUserPackagePurchase.add(packagePurchase);
        }

        return allUserPackagePurchase;
    }


    @Override
    public void createPackagePurchase(PackagePurchase packagePurchase) {
        String sql = "INSERT INTO package_purchase (client_id, date_purchased, package_id, " +
                "classes_remaining, activation_date, expiration_date, " +
                "total_amount_paid, is_monthly_renew,  discount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, packagePurchase.getClient_id(), packagePurchase.getDate_purchased(),
                packagePurchase.getPackage_id(), packagePurchase.getClasses_remaining(),
                packagePurchase.getActivation_date(), packagePurchase.getExpiration_date(),
                packagePurchase.getTotal_amount_paid(),
                packagePurchase.isIs_monthly_renew(), packagePurchase.getDiscount());
    }

    @Override
    public PackagePurchase getPackagePurchaseObjectByPackagePurchaseId(int packagePurchaseId) {
        PackagePurchase packagePurchase = null;
        String sql = "SELECT * FROM package_purchase " +
                "WHERE package_purchase_id = ?" ;
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, packagePurchaseId);
        if (result.next()) {
            packagePurchase = mapRowToPackagePurchase(result);

            // set package description
            packagePurchase.setPackage_description(getPackageDescriptionByPackageId(packagePurchase.getPackage_id()));
            packagePurchase.setUnlimited(IsSubscriptionOrNot(packagePurchase.getPackage_id()));

        }
        return packagePurchase;
    }

    public boolean IsSubscriptionOrNot(int packageId){
        boolean isSubscription = false;
        String sql = "SELECT unlimited from package_details WHERE package_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, packageId);
        if(result.next()){
            return result.getBoolean("unlimited");
        }
        return isSubscription;
    }

    @Override
    public boolean expirePackage(PackagePurchase packagePurchase) {
        String sql = "UPDATE package_purchase SET classes_remaining = 0 " +
                "WHERE package_purchase_id = ?;";
        if (packagePurchase.isUnlimited()) {
            sql = "UPDATE package_purchase SET expiration_date = current_date - INTEGER '1', activation_date = current_date - INTEGER '1' " +
                    "WHERE package_purchase_id = ?;";
        }
        return jdbcTemplate.update(sql, packagePurchase.getPackage_purchase_id())==1;
    }

    @Override
    public boolean updatePackage(PackagePurchase packagePurchase) {
        String sql = "UPDATE package_purchase SET date_purchased = ?, classes_remaining = ?, " +
                "activation_date = ?, expiration_date = ?, is_monthly_renew = ? , discount = ? WHERE package_purchase_id = ?;";
    return jdbcTemplate.update(sql, packagePurchase.getDate_purchased(), packagePurchase.getClasses_remaining(), packagePurchase.getActivation_date(), packagePurchase.getExpiration_date(), packagePurchase.isIs_monthly_renew(), packagePurchase.getDiscount(), packagePurchase.getPackage_purchase_id() )==1;
    }

    @Override
    public boolean decrementByOne(int packagePurchaseId) {
        String sql= "UPDATE package_purchase SET classes_remaining = classes_remaining - 1 WHERE package_purchase_id = ?";
        return jdbcTemplate.update(sql, packagePurchaseId)==1;
    }

    @Override
    public boolean incrementByOne(int packagePurchaseId) {
        String sql= "UPDATE package_purchase SET classes_remaining = classes_remaining + 1 WHERE package_purchase_id = ?";
        return jdbcTemplate.update(sql, packagePurchaseId)==1;
    }

    @Override
    public boolean updateGiftCard(GiftCard originalGiftCard, int clientId, double amountUsed) {
        int newAmount = (int) (originalGiftCard.getAmount() - amountUsed);
        String sql = "UPDATE gift_card SET amount = ? , client_id = ? WHERE code ILIKE ?";
        return jdbcTemplate.update(sql, newAmount, clientId, originalGiftCard.getCode())==1;
    }



    //helper
    @Override
    public PackagePurchase filterPackageList(List<PackagePurchase> packagePurchaseList, ClassEvent classEvent) {
        // set up the one packagePurchase Object to return
        PackagePurchase packagePurchaseQuantity = new PackagePurchase();
        PackagePurchase packagePurchaseSubscription = new PackagePurchase();
        int classAmount = 100;
        boolean hasQuantity = false;
        for (int i = 0; i < packagePurchaseList.size(); i++) {
            PackagePurchase currentPackage = packagePurchaseList.get(i);

            // if they have a subscription make sure the class they are being signed up for is not after their expiration date
            if (currentPackage.isUnlimited()) {

                // could check for the expiration date right here as well/
                // compare the expiration date to the starting time of the event
                Timestamp eventTime = classEvent.getStart_time();
                Date expirationDate = currentPackage.getExpiration_date();
                Timestamp packageExpiration = new Timestamp(currentPackage.getExpiration_date().getTime());

                // if they have a subscription make sure the class they are being signed up for is not after their expiration date

                //        Integer value 0 if this Timestamp object is equal to given Timestamp object.
                //        A value less than 0 if this Timestamp object is before the given argument.
                //        A value greater than 0 if this Timestamp object is after the given argument.

                int numberValueFromComparison = packageExpiration.compareTo(eventTime);
                if (numberValueFromComparison > 0) {
                    packagePurchaseSubscription = currentPackage;

                    return packagePurchaseSubscription;
                }

            }
            else if (!currentPackage.isUnlimited() && currentPackage.getClasses_remaining() > 0) {
                // compare the expiration date to the starting time of the event
                Timestamp eventTime = classEvent.getStart_time();
                Date expirationDate = currentPackage.getExpiration_date();
                Timestamp packageExpiration = new Timestamp(currentPackage.getExpiration_date().getTime());

                int numberValueFromComparison = packageExpiration.compareTo(eventTime);
                if (numberValueFromComparison > 0) {
                    if (currentPackage.getClasses_remaining() < classAmount) {
                        classAmount = currentPackage.getClasses_remaining();
                        packagePurchaseQuantity = currentPackage;
                        hasQuantity = true;
                    }


                }

            }
        }
        if (hasQuantity) {
            return packagePurchaseQuantity;
        }

        if (packagePurchaseQuantity.getPackage_purchase_id() > 0) {
            return packagePurchaseQuantity;
        }
        packagePurchaseSubscription.setPackage_purchase_id(0);
        return packagePurchaseSubscription;
    }

    @Override
    public List<PackagePurchase> getAllSharedActiveQuantityPackages(int client_id) {
        List<PackagePurchase> packages = new ArrayList<>();
        String sql = "SELECT package_purchase.*\n" +
                "from package_purchase\n" +
                "JOIN client_family ON package_purchase.client_id = client_family.client_id\n" +
                "WHERE client_family.family_id = \n" +
                "(select family_id from client_family where client_family.client_id = ?) \n" +
                "AND client_family.client_id != ?\n" +
                "AND classes_remaining > 0 \n" +
                "ORDER BY expiration_date;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, client_id,client_id);
        while(result.next()){
            packages.add(mapRowToPackagePurchase(result));
        }
        return packages;
    }


    // helper
    public String getPackageDescriptionByPackageId(int PackageId) {
        PackageDetails packageDetails = null;
        String sql = "SELECT * FROM package_details WHERE package_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, PackageId);
        if (result.next()) {
            packageDetails = mapRowToPackage(result);
        }
        if (packageDetails != null) {
            return packageDetails.getDescription();
        }
        return "";
    }

    // helper
    private PackageDetails mapRowToPackage(SqlRowSet rs) {
        PackageDetails packageDetails = new PackageDetails();
        packageDetails.setPackage_id(rs.getInt("package_id"));
        packageDetails.setDescription(rs.getString("description"));
        packageDetails.setPackage_cost(rs.getBigDecimal("package_cost"));
        if (rs.getInt("classes_amount") != 0) {
            packageDetails.setClasses_amount(rs.getInt("classes_amount"));
        }
        if (rs.getInt("package_duration") != 0) {
            packageDetails.setPackage_duration(rs.getInt("package_duration"));
        }
        packageDetails.setUnlimited(rs.getBoolean("unlimited"));
        packageDetails.setIs_visible_online((rs.getBoolean("is_visible_online")));
        packageDetails.setIs_recurring(rs.getBoolean("is_recurring"));
        packageDetails.setActive(rs.getBoolean("active"));
        return packageDetails;
    }

    public PackagePurchase mapRowToPackagePurchase(SqlRowSet rs) {
        PackagePurchase packagePurchase = new PackagePurchase();
        packagePurchase.setPackage_purchase_id(rs.getInt("package_purchase_id"));
        packagePurchase.setClient_id(rs.getInt("client_id"));
        packagePurchase.setDate_purchased(rs.getTimestamp("date_purchased"));
        packagePurchase.setPackage_id(rs.getInt("package_id"));
        if (rs.getInt("classes_remaining") > 0) {
            packagePurchase.setClasses_remaining(rs.getInt("classes_remaining"));
        }
        if (rs.getDate("activation_date") != null) {
            packagePurchase.setActivation_date(rs.getDate("activation_date"));
        }
        if (rs.getDate("expiration_date") != null) {
            packagePurchase.setExpiration_date(rs.getDate("expiration_date"));
        }
        if (rs.getBigDecimal("total_amount_paid") != null) {
            packagePurchase.setTotal_amount_paid(rs.getBigDecimal("total_amount_paid"));
        }
        packagePurchase.setIs_monthly_renew(rs.getBoolean("is_monthly_renew"));
        if (rs.getBigDecimal("discount") != null) {
            packagePurchase.setDiscount(rs.getBigDecimal("discount"));
        }

        return packagePurchase;
    }

    public GiftCard mapRowToGiftCard(SqlRowSet rs) {
        GiftCard giftCard = new GiftCard();
        giftCard.setCode(rs.getString("code"));
        giftCard.setAmount(rs.getDouble("amount"));
        if (rs.getInt("client_id") > 0) {
            giftCard.setClient_id(rs.getInt("client_id"));
        }
        return giftCard;
    }

    public void createGiftCard(String code, double amount){
        String sql = "INSERT INTO gift_card (code, amount) VALUES (?,?)";
        jdbcTemplate.update(sql, code, amount);

    }

    @Override
    public GiftCard retrieveGiftCard(String code) {
        GiftCard giftCard = null;
        String sql = "SELECT * FROM gift_card WHERE code ILIKE ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, code);
        if (result.next()) {
            giftCard = mapRowToGiftCard(result);
        }
        return giftCard;
    }

    public String generateGiftCardCode() {
        int length = 7;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder coupon = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            coupon.append(randomChar);
        }
        return coupon.toString().toUpperCase();
    }

    public int createStripePackagePurchase(CheckoutItemDTO checkoutItemDTO) {
        LocalDate activationDate = LocalDate.now();

        if (checkoutItemDTO.getPackageDuration() > 0) {
            String sql = "SELECT expiration_date FROM package_purchase JOIN package_details ON package_details.package_id = package_purchase.package_id WHERE package_details.unlimited = true AND client_id = ? AND expiration_date > NOW() ORDER BY expiration_date DESC LIMIT 1;";
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, checkoutItemDTO.getClient_id());
            if (results.next()) {
                activationDate = results.getDate("expiration_date").toLocalDate();
                activationDate = activationDate.plusDays(1);
            } else {
                activationDate = LocalDate.now();
            }
        }

        String sql = "INSERT INTO package_purchase (client_id, date_purchased, package_id, " +
                "classes_remaining, activation_date, expiration_date, " +
                "is_monthly_renew, total_amount_paid, discount, paymentId ) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING package_purchase_id";
        return jdbcTemplate.queryForObject(sql, Integer.class, checkoutItemDTO.getClient_id(), LocalDateTime.now(),
                checkoutItemDTO.getPackage_id(), checkoutItemDTO.getClasses_remaining(),
                activationDate, returnCorrectPackageExpirationDateForCheckoutItem(checkoutItemDTO, activationDate), checkoutItemDTO.isIs_monthly_renew(),
                checkoutItemDTO.getTotal_amount_paid(), checkoutItemDTO.getDiscount(), checkoutItemDTO.getPaymentId());
    }

    private LocalDate returnCorrectPackageExpirationDateForCheckoutItem(CheckoutItemDTO checkoutItemDTO, LocalDate activationDate) {
        if (checkoutItemDTO.getPackageDuration() > 0) {
            return activationDate.plusMonths(checkoutItemDTO.getPackageDuration()).plusDays(1);
        }
        return LocalDate.now().plusYears(1).plusDays(1);
    }

    public int createAdminPackagePurchase(PackagePurchase packagePurchase) {

        LocalDate activationDate = findActivationDateForClient(packagePurchase);

        String sql = "INSERT INTO package_purchase (client_id, date_purchased, package_id, " +
                "classes_remaining, activation_date, expiration_date, " +
                "is_monthly_renew, total_amount_paid, discount, paymentId ) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING package_purchase_id";
        return jdbcTemplate.queryForObject(sql, Integer.class, packagePurchase.getClient_id(), LocalDateTime.now(),
                packagePurchase.getPackage_id(), packagePurchase.getClasses_remaining(),
                activationDate, returnCorrectPackageExpirationDateForPackagePurchase(packagePurchase, activationDate), packagePurchase.isIs_monthly_renew(),
                packagePurchase.getTotal_amount_paid(), packagePurchase.getDiscount(), packagePurchase.getPaymentId());
    }

    private LocalDate findActivationDateForClient(PackagePurchase packagePurchase) {
        if (packagePurchase.getPackage_duration() > 0) {
        String sql = "SELECT expiration_date FROM package_purchase JOIN package_details ON package_details.package_id = package_purchase.package_id WHERE package_details.unlimited = true AND client_id = ? ORDER BY expiration_date DESC LIMIT 1;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, packagePurchase.getClient_id());
            if (results.next()) {
                LocalDate localDate = results.getDate("expiration_date").toLocalDate();
                return localDate.plusDays(1);
            }
        }
        return LocalDate.now();
    }

    private LocalDate returnCorrectPackageExpirationDateForPackagePurchase(PackagePurchase packagePurchase, LocalDate activationDate) {
        if (packagePurchase.getPackage_duration() > 0) {


            return activationDate.plusMonths(packagePurchase.getPackage_duration()).plusDays(1);
        }
        return LocalDate.now().plusYears(1).plusDays(1);
    }

    public int createOneMonthAutoRenewPurchase(CheckoutItemDTO checkoutItemDTO){
        LocalDate activationDate;
        String sql = "SELECT expiration_date FROM package_purchase JOIN package_details ON package_details.package_id = package_purchase.package_id WHERE package_details.unlimited = true AND client_id = ? AND expiration_date > NOW() ORDER BY expiration_date DESC LIMIT 1;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, checkoutItemDTO.getClient_id());
        if (results.next()) {
            activationDate = results.getDate("expiration_date").toLocalDate();
            activationDate.plusDays(1);
        } else {
            activationDate = LocalDate.now();
        }
        String sql2 = "INSERT INTO package_purchase (client_id, date_purchased, package_id, classes_remaining, activation_date, expiration_date, is_monthly_renew, total_amount_paid, discount, paymentId ) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING package_purchase_id";
        return jdbcTemplate.queryForObject(sql2, Integer.class, checkoutItemDTO.getClient_id(), LocalDateTime.now(),
                checkoutItemDTO.getPackage_id(), 0, activationDate,
                activationDate.plusMonths(1).plusDays(1), true,
                checkoutItemDTO.getTotal_amount_paid(), checkoutItemDTO.getDiscount(), checkoutItemDTO.getPaymentId());
    }

    public int createSixMonthAutoRenewPurchase(CheckoutItemDTO checkoutItemDTO){
        LocalDate activationDate;
        String sql = "SELECT expiration_date FROM package_purchase JOIN package_details ON package_details.package_id = package_purchase.package_id WHERE package_details.unlimited = true AND client_id = ? AND expiration_date > NOW() ORDER BY expiration_date DESC LIMIT 1;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, checkoutItemDTO.getClient_id());
        if (results.next()) {
            activationDate = results.getDate("expiration_date").toLocalDate();
            activationDate.plusDays(1);
        } else {
            activationDate = LocalDate.now();
        }

        String sql2 = "INSERT INTO package_purchase (client_id, date_purchased, package_id, classes_remaining, activation_date, expiration_date, is_monthly_renew, total_amount_paid, discount, paymentId ) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING package_purchase_id";
        return jdbcTemplate.queryForObject(sql2, Integer.class, checkoutItemDTO.getClient_id(), LocalDateTime.now(),
                checkoutItemDTO.getPackage_id(), 0, activationDate,
                activationDate.plusMonths(6).plusDays(1), true,
                checkoutItemDTO.getTotal_amount_paid(), checkoutItemDTO.getDiscount(), checkoutItemDTO.getPaymentId());
    }

    public int createGiftCardPurchase(CheckoutItemDTO checkoutItemDTO) {
        String sql = "INSERT INTO package_purchase (client_id, date_purchased, package_id, classes_remaining, activation_date, expiration_date, is_monthly_renew, total_amount_paid, discount, paymentId ) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING package_purchase_id";
        return jdbcTemplate.queryForObject(sql, Integer.class, checkoutItemDTO.getClient_id(), LocalDateTime.now(),
                checkoutItemDTO.getPackage_id(), 0, LocalDate.now(),
                LocalDate.now().plusMonths(60), false,
                checkoutItemDTO.getTotal_amount_paid(), checkoutItemDTO.getDiscount(), checkoutItemDTO.getPaymentId());
    }

    @Override
    public void purchaseLineItems(List<CheckoutItemDTO> itemList, List<Transaction> transactions) {
        List<Integer> packagePurchaseIDs = new ArrayList<>();
        for (CheckoutItemDTO eachPackage : itemList) {
            if (eachPackage.getProductName().contains("Gift")) {
                String code = generateGiftCardCode();
                createGiftCard(code, eachPackage.getPrice());
                int packagePurchaseId = createGiftCardPurchase(eachPackage);
                packagePurchaseIDs.add(packagePurchaseId);
                ClientDetails clientDetails =
                        clientDetailsDao.findClientByClientId(eachPackage.getClient_id());

                if (clientDetails.getEmail().equals(eachPackage.getGiftCardEmail())) {
                    // call the email service here and shoot off the gift code.
                    try {

                        senderService.sendEmail(clientDetails.getEmail(), "Sattva Yoga Center Gift Card Code", "Your Gift Card code is: " + code + " . Please note: The Gift Card Code can only be redeemed in person. Once redeemed, it cannot be used by anyone else.");
                    } catch (Throwable e) {
                        System.out.println("Error sending gift card email to client id: " + clientDetails.getClient_id());
                    }
                } else {
                    try {

                        senderService.sendEmail(eachPackage.getGiftCardEmail(), "Sattva Yoga Center Gift Card Code", "Your Gift Card code is: " + code + " . Please note: The Gift Card Code can only be redeemed in person. Once redeemed, it cannot be used by anyone else.");
                    } catch (Throwable e) {
                        System.out.println("Error sending gift card email to client id: " + clientDetails.getClient_id());
                    }

                    if (eachPackage.isSaveGiftCardEmail()) {

                        // SAVE EMAIL TO CLIENT OBJECT IN OUR DB
                        clientDetailsDao.saveNewClientEmail(clientDetails.getClient_id(), eachPackage.getGiftCardEmail());

                        // SAVE EMAIL TO CUSTOMER OBJECT IN STRIPE
                        stripeDao.updateCustomerEmail(clientDetails.getCustomer_id(), eachPackage.getGiftCardEmail());
                    }

                }
                continue;
            }
            if (eachPackage.isSaveReceiptEmail()) {
                ClientDetails clientDetails =
                        clientDetailsDao.findClientByClientId(eachPackage.getClient_id());

                // SAVE EMAIL TO CLIENT OBJECT IN OUR DB
                clientDetailsDao.saveNewClientEmail(eachPackage.getClient_id(), eachPackage.getReceiptEmail());

                // SAVE EMAIL TO CUSTOMER OBJECT IN STRIPE
                stripeDao.updateCustomerEmail(clientDetails.getCustomer_id(), eachPackage.getReceiptEmail());
//            } else if (eachPackage.getProductName().contains("One") && eachPackage.isIs_monthly_renew()) {
//                int packagePurchaseId = createOneMonthAutoRenewPurchase(eachPackage);
//                packagePurchaseIDs.add(packagePurchaseId);
//            } else if (eachPackage.getProductName().contains("Six") && eachPackage.isIs_monthly_renew()) {
//                int packagePurchaseId = createSixMonthAutoRenewPurchase(eachPackage);
//                packagePurchaseIDs.add(packagePurchaseId);
            } else {
                int packagePurchaseId = createStripePackagePurchase(eachPackage);
                packagePurchaseIDs.add(packagePurchaseId);
            }
        }
        int[] arrayOfPackagePurchaseIDs = packagePurchaseIDs.stream().mapToInt(i -> i).toArray();
        Sale sale = new Sale();
        sale.setPackages_purchased_array(arrayOfPackagePurchaseIDs);
        sale.setClient_id(itemList.get(0).getClient_id());
        int saleId = saleDao.createSaleNoBatch(sale);

        for(Transaction transaction : transactions){
            transaction.setSale_id(saleId);

            transactionDao.createTransaction(transaction);
        }

    }



}
