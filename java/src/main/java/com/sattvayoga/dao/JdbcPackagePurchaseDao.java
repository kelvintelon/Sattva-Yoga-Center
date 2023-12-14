package com.sattvayoga.dao;

import com.sattvayoga.dto.order.CheckoutItemDTO;
import com.sattvayoga.dto.order.ClientCheckoutDTO;
import com.sattvayoga.dto.order.ResendEmailDTO;
import com.sattvayoga.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.sql.DriverManager.getConnection;

@Component
public class JdbcPackagePurchaseDao implements PackagePurchaseDao {

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    UserDao userDao;
    @Autowired
    ClientDetailsDao clientDetailsDao;

    @Autowired
    PackageDetailsDao packageDetailsDao;
    @Autowired
    StripeDao stripeDao;
    @Autowired
    SaleDao saleDao;
    @Autowired
    TransactionDao transactionDao;

    @Autowired
    private EmailSenderService senderService;

    public JdbcPackagePurchaseDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<PackagePurchase> getAllUserPackagePurchases(int userId) {
        List<PackagePurchase> allUserPackagePurchase = new ArrayList<>();
        String sql = "SELECT * FROM package_purchase " +
                "JOIN client_details on client_details.client_id = package_purchase.client_id " +
                "WHERE client_details.user_id = ?";
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
            offset = pageSize * (page - 1);
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
            Set<String> capturePaymentTypes = new HashSet<>();
            if (saleId > 0) {
                SqlRowSet result3 = jdbcTemplate.queryForRowSet(sql3, saleId);
                while (result3.next()) {
                    if (!capturePaymentTypes.contains(result3.getString("payment_Type"))) {
                        paymentDescriptions += result3.getString("payment_Type") + " /";
                        capturePaymentTypes.add(result3.getString("payment_Type"));
                    }
                }
            }
            if (paymentDescriptions.length() > 0) {
                paymentDescriptions = paymentDescriptions.substring(0, paymentDescriptions.length() - 1);
            }
            packagePurchase.setPayment_description(paymentDescriptions);
            packagePurchase.setPackage_description(description);
            packagePurchase.setUnlimited(IsSubscriptionOrNot(packagePurchase.getPackage_id()));
            allUserPackagePurchase.add(packagePurchase);
        }

        String countSql = "SELECT COUNT(*) FROM package_purchase " +
                "JOIN client_details on client_details.client_id = package_purchase.client_id " +
                "WHERE client_details.user_id = ?";

        int count = jdbcTemplate.queryForObject(countSql, Integer.class, userId);

        PaginatedListOfPurchasedPackages paginatedListOfPurchasedPackages = new PaginatedListOfPurchasedPackages(allUserPackagePurchase, count);

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

            packagesBeingBoughtForEmail += count + ". " + getPackageDescriptionByPackageId(currentPackage.getPackage_id()) + " - $" + amountPaid + " - " + "Expires on: " + currentPackage.getExpiration_date().toString() + "<br>";
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

    @Override
    public void uploadSalesCsv(MultipartFile multipartFile) {
        int count = 0;

        long startTimeForEntireUpload = System.nanoTime();

        List<String> listOfStringsFromBufferedReader = new ArrayList<>();

        Set<PackagePurchase> packagePurchaseSet = new HashSet<>();
        HashMap<Integer, Sale> mapOfSalesFromFile = new HashMap<>();
        Set<Transaction> setOfTransactionsFromFile = new HashSet<>();

        HashMap<String,Integer> mapColumns = new HashMap<>();


        try (BufferedReader fileReader = new BufferedReader(new
                InputStreamReader(multipartFile.getInputStream(), "UTF-8"))) {

            String line;
            while ((line = fileReader.readLine()) != null) {

                if (count > 0) {

                    listOfStringsFromBufferedReader.add(line);

                } else {
                    String[] firstLine =  line.split(",");
                    mapColumns = populateColumnsForSalesMap(firstLine);
                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        readLinesFromListAndPopulatePackagesSalesTransactions(listOfStringsFromBufferedReader, packagePurchaseSet, mapOfSalesFromFile, setOfTransactionsFromFile, mapColumns);


        if (!packagePurchaseSet.isEmpty()) {
            batchCreatePackagePurchases(packagePurchaseSet);
        }

        Set<Sale> setOfSales = new HashSet<>(mapOfSalesFromFile.values());

        if (!setOfSales.isEmpty()) {
            batchCreateSales(setOfSales);
        }

        if (!setOfTransactionsFromFile.isEmpty()) {
            batchCreateTransactions(setOfTransactionsFromFile);
        }

    }

    public static HashMap<String, Integer> populateColumnsForSalesMap(String[] array) {
        HashMap<String, Integer> columnMap = new HashMap<>();

        for (int i = 0; i < array.length; i++) {
            String currentString = array[i];
            if (currentString.contains("SaleDate")) {
                columnMap.put("SaleDate", i);
            } else if (currentString.contains("Client ID")) {
                columnMap.put("Client ID", i);
            } else if (currentString.contains("Sale ID")) {
                columnMap.put("Sale ID", i);
            } else if (currentString.contains("Activation")) {
                columnMap.put("Activation", i);
            } else if (currentString.contains("Expiration")) {
                columnMap.put("Expiration", i);
            } else if (currentString.contains("package_id")) {
                columnMap.put("package_id", i);
            } else if (currentString.contains("Batch #")) {
                columnMap.put("Batch #", i);
            } else if (currentString.contains("Quantity")) {
                columnMap.put("Quantity", i);
            } else if (currentString.contains("Discount amount")) {
                columnMap.put("Discount amount", i);
            } else if (currentString.contains("Item Total")) {
                columnMap.put("Item Total", i);
            } else if (currentString.contains("Total Paid w/ Payment Method")) {
                columnMap.put("Total Paid w/ Payment Method", i);
            } else if (currentString.contains("Payment Method")) {
                columnMap.put("Payment Method", i);
            }

        }

        return columnMap;
    }

    private void readLinesFromListAndPopulatePackagesSalesTransactions(List<String> listOfStringsFromBufferedReader,
                                                                       Set<PackagePurchase> packagePurchaseSet,
                                                                       HashMap<Integer, Sale> mapOfSale,
                                                                       Set<Transaction> setOfTransactions,
                                                                       HashMap<String, Integer> columnMap) {



        List<PackageDetails> listOfAllPackages = packageDetailsDao.getAllPackages();

        Map<Integer,PackageDetails> mapOfPackages = new HashMap<>();

        for (int i = 0; i < listOfAllPackages.size(); i++) {
            PackageDetails currentPackage = listOfAllPackages.get(i);
            mapOfPackages.put(currentPackage.getPackage_id(), currentPackage);
        }

        int maxId = findHighestPackagePurchaseId();
        maxId += 100001;

        //retrieve a set of integer sale_id's already present in the database
        Set<Integer> setOfSaleIds = saleDao.getAllSaleIds();

        for (int i = 0; i < listOfStringsFromBufferedReader.size(); i++) {
            String thisLine = listOfStringsFromBufferedReader.get(i);
            String[] splitLine = thisLine.split(",");


            PackagePurchase packagePurchase = new PackagePurchase();

            int clientId = Integer.valueOf(splitLine[columnMap.get("Client ID")]);
            packagePurchase.setClient_id(clientId);

            int saleId = Integer.valueOf(splitLine[columnMap.get("Sale ID")]);

            // Handle duplicates here (look for if we already have the sale ID)
            if (!setOfSaleIds.isEmpty() && setOfSaleIds.contains(saleId)) {

                continue;
            }

            Timestamp datePurchased = convertDateStringToTimestamp(splitLine[columnMap.get("SaleDate")]);
            packagePurchase.setDate_purchased(datePurchased);

            Date activationDate = convertDateStringToDate(splitLine[columnMap.get("Activation")]);
            packagePurchase.setActivation_date(activationDate);

            Date expirationDate = convertDateStringToDate(splitLine[columnMap.get("Expiration")]);
            packagePurchase.setExpiration_date(expirationDate);

            int packageId = Integer.valueOf(splitLine[columnMap.get("package_id")]);
            packagePurchase.setPackage_id(packageId);

            PackageDetails currentPackage = mapOfPackages.get(packageId);
            packagePurchase.setClasses_remaining(currentPackage.getClasses_amount());
            packagePurchase.setIs_monthly_renew(currentPackage.isIs_recurring());

            String discountString = splitLine[columnMap.get("Discount amount")].replaceAll("[^\\d.]", "");
            discountString = discountString.replaceAll("\\.{2,}", ".");
            BigDecimal discount = new BigDecimal(discountString);
            packagePurchase.setDiscount(discount);

            String totalAmountPaidString = splitLine[columnMap.get("Item Total")].replaceAll("[^\\d.]", "");
            totalAmountPaidString = totalAmountPaidString.replaceAll("\\.{2,}", ".");
            BigDecimal totalAmountPaid = new BigDecimal(totalAmountPaidString);
            packagePurchase.setTotal_amount_paid(totalAmountPaid);

            String paymentType = "";
            if (splitLine.length > 14 && columnMap.containsKey("Payment Method")) {
                // Access the element at index 14
                paymentType = formatCardType(splitLine[columnMap.get("Payment Method")]);

            }

            packagePurchase.setPaymentId(paymentType);

            Set<Integer> packagePurchaseIds = new HashSet<>();
            packagePurchaseIds.add(maxId);
            packagePurchase.setPackage_purchase_id(maxId);

            packagePurchaseSet.add(packagePurchase);

            int quantity = Integer.valueOf(splitLine[columnMap.get("Quantity")]);

            if (quantity > 1) {

                for (int j = 1; j < quantity; j++) {
                    maxId++;

                    PackagePurchase newPurchase = new PackagePurchase();
                    newPurchase.setPackage_purchase_id(maxId);
                    newPurchase.setClient_id(clientId);
                    newPurchase.setDate_purchased(packagePurchase.getDate_purchased());
                    newPurchase.setActivation_date(packagePurchase.getActivation_date());
                    newPurchase.setExpiration_date(packagePurchase.getExpiration_date());
                    newPurchase.setPackage_id(packageId);
                    newPurchase.setClasses_remaining(packagePurchase.getClasses_remaining());
                    newPurchase.setIs_monthly_renew(packagePurchase.isIs_monthly_renew());
                    newPurchase.setDiscount(packagePurchase.getDiscount());
                    newPurchase.setTotal_amount_paid(packagePurchase.getTotal_amount_paid());
                    newPurchase.setPaymentId("Gift Card Code");

                    packagePurchaseSet.add(newPurchase);
                    packagePurchaseIds.add(maxId);
                }

            }

            // Plug in Sale ID Here and build
            if (mapOfSale.containsKey(saleId)) {
                Sale sale = mapOfSale.get(saleId);
                if (splitLine[columnMap.get("Batch #")].length()>0 && !splitLine[columnMap.get("Batch #")].contains("-")) {
                    int batchNumber = Integer.valueOf(splitLine[columnMap.get("Batch #")]);
                    sale.setBatch_number(batchNumber);
                }

                sale = mapOfSale.get(saleId);
                List<Integer> tempList = new ArrayList<>(sale.getPackages_purchased_list());

                // ADD In the new package purchase and update the sale object
                for (Integer id : packagePurchaseIds) {
                    tempList.add(id);
                    sale.setPackages_purchased_list(tempList);
                }

                // Replace sale in Map
                mapOfSale.put(saleId, sale);
            } else {
                Sale sale =  new Sale();
                if (splitLine[columnMap.get("Batch #")].length()>0 && !splitLine[columnMap.get("Batch #")].contains("-")) {
                    int batchNumber = Integer.valueOf(splitLine[columnMap.get("Batch #")]);
                    sale.setBatch_number(batchNumber);
                }

                List<Integer> tempList = new ArrayList<>();
                sale.setSale_id(saleId);
                sale.setClient_id(clientId);

                for (Integer id : packagePurchaseIds) {
                    tempList.add(id);
                    sale.setPackages_purchased_list(tempList);
                }

                // Replace sale in Map
                mapOfSale.put(saleId, sale);
            }



            // Build transaction
            Transaction transaction = new Transaction();
            transaction.setClient_id(clientId);
            transaction.setSale_id(saleId);

            String paymentAmountString = splitLine[columnMap.get("Total Paid w/ Payment Method")].replaceAll("[^\\d.]", "");
            paymentAmountString = paymentAmountString.replaceAll("\\.{2,}", ".");
            BigDecimal payment_amount = new BigDecimal(paymentAmountString);
            transaction.setPayment_amount(payment_amount.doubleValue());
            transaction.setPayment_type(paymentType);

            setOfTransactions.add(transaction);

            maxId++;
        }
    }



    public void batchCreatePackagePurchases(final Collection<PackagePurchase> packagePurchases) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO package_purchase (package_purchase_id, client_id,package_id,date_purchased, " +
                        "classes_remaining, activation_date, " +
                        "expiration_date, is_monthly_renew, " +
                        "total_amount_paid, discount, paymentid) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                packagePurchases,100,
                (PreparedStatement ps, PackagePurchase packagePurchase) -> {
                    ps.setInt(1, packagePurchase.getPackage_purchase_id());
                    ps.setInt(2, packagePurchase.getClient_id());
                    ps.setInt(3, packagePurchase.getPackage_id());
                    ps.setTimestamp(4, packagePurchase.getDate_purchased());
                    ps.setInt(5, packagePurchase.getClasses_remaining());
                    ps.setDate(6, packagePurchase.getActivation_date());
                    ps.setDate(7, packagePurchase.getExpiration_date());
                    ps.setBoolean(8, packagePurchase.isIs_monthly_renew());
                    ps.setBigDecimal(9, packagePurchase.getTotal_amount_paid());
                    ps.setBigDecimal(10, packagePurchase.getDiscount());
                    ps.setString(11, packagePurchase.getPaymentId());
                }
        );
    }

    public void batchCreateSales(final Collection<Sale> sales) {
        Connection finalConn =null;
        try {
            finalConn = jdbcTemplate.getDataSource().getConnection("postgres","postgres1");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        Connection finalConn1 = finalConn;
        jdbcTemplate.batchUpdate(
                "INSERT INTO sales (sale_id, " +
                        "packages_purchased_array, batch_number, client_id) " +
                        "VALUES (?,?,?,?)",
                sales,100,
                (PreparedStatement ps, Sale sale) -> {
                    ps.setInt(1, sale.getSale_id());
//                    Integer[] idArray = sale.getPackages_purchased_list().toArray(new Integer[0]);
//                    Integer[] idArray = sale.getPackages_purchased_list().stream().mapToInt(Integer::intValue).toArray();
//                    Array idSqlArray = jdbcTemplate.execute(
//                            (Connection c) -> c.createArrayOf(JDBCType.INTEGER.getName(), idArray)
//                    );
                    Array sqlArray = finalConn1.createArrayOf("INTEGER", sale.getPackages_purchased_list().toArray());
                    ps.setObject(2, (sqlArray),Types.ARRAY);
//                    ps.setArray(2,idSqlArray);
                    ps.setInt(3, sale.getBatch_number());
                    ps.setInt(4, sale.getClient_id());

                }
        );
    }

    public void batchCreateTransactions(final Collection<Transaction> transactions) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO transactions (sale_id, client_id,payment_type, " +
                        "payment_amount, gift_code) " +
                        "VALUES (?,?,?,?,?)",
                transactions,100,
                (PreparedStatement ps, Transaction transaction) -> {
                    ps.setInt(1, transaction.getSale_id());
                    ps.setInt(2, transaction.getClient_id());
                    ps.setString(3, transaction.getPayment_type());
                    ps.setDouble(4, transaction.getPayment_amount());
                    ps.setString(5, transaction.getGift_code());
                }
        );
    }

    @Override
    public void uploadGiftCardReport(MultipartFile multipartFile) {
        int count = 0;

        long startTimeForEntireUpload = System.nanoTime();

        List<String> listOfStringsFromBufferedReader = new ArrayList<>();

        HashMap<String, Integer> mapColumns = new HashMap<>();

        try (BufferedReader fileReader = new BufferedReader(new
                InputStreamReader(multipartFile.getInputStream(), "UTF-8"))) {

            String line;
            while ((line = fileReader.readLine()) != null) {

                if (count > 0) {

                    listOfStringsFromBufferedReader.add(line);

                } else {
                    String[] firstLine =  line.split(",");
                    mapColumns = populateColumnsForGiftCardMap(firstLine);
                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String, GiftCard> mapOfGiftCardsFromFile = new HashMap<>();
        Set<Transaction> setOfTransactionsFromFile = new HashSet<>();

        HashMap<String,GiftCard> giftCardsToUpdateInDb = readLinesFromListAndPopulateTransactionsGiftCards(listOfStringsFromBufferedReader,mapOfGiftCardsFromFile,setOfTransactionsFromFile, mapColumns);

        if (!mapOfGiftCardsFromFile.isEmpty()) {
            // batch create gift cards no client ID yet
            Set<GiftCard> setOfGiftCardsFromFile = new HashSet<>(mapOfGiftCardsFromFile.values());
            batchCreateGiftCards(setOfGiftCardsFromFile);

        }
        if (!setOfTransactionsFromFile.isEmpty()) {
            // batch create transactions
            batchCreateTransactions(setOfTransactionsFromFile);
        }
        if (!giftCardsToUpdateInDb.isEmpty()) {
            // batch update gift cards in DB with client ID
             Set<GiftCard> setOfGiftCardsFromDB = new HashSet<>(giftCardsToUpdateInDb.values());
            batchUpdateGiftCards(setOfGiftCardsFromDB);
        }


    }

    public static HashMap<String, Integer> populateColumnsForGiftCardMap(String[] array) {
        HashMap<String, Integer> columnMap = new HashMap<>();

        for (int i = 0; i < array.length; i++) {
            String currentString = array[i];
            if (currentString.contains("SaleDate")) {
                columnMap.put("SaleDate", i);
            } else if (currentString.contains("ClientID")) {
                columnMap.put("ClientID", i);
            } else if (currentString.contains("Sale ID")) {
                columnMap.put("Sale ID", i);
            } else if (currentString.contains("Gift Card ID")) {
                columnMap.put("Gift Card ID", i);
            } else if (currentString.contains("Amount")) {
                columnMap.put("Amount", i);
            } else if (currentString.contains("Package_id")) {
                columnMap.put("Package_id", i);
            } else if (currentString.contains("Batch #")) {
                columnMap.put("Batch #", i);
            } else if (currentString.contains("Quantity")) {
                columnMap.put("Quantity", i);
            } else if (currentString.contains("Discount amount")) {
                columnMap.put("Discount amount", i);
            } else if (currentString.contains("Item Total")) {
                columnMap.put("Item Total", i);
            } else if (currentString.contains("Total Paid w/ Payment Method")) {
                columnMap.put("Total Paid w/ Payment Method", i);
            } else if (currentString.contains("Payment Method")) {
                columnMap.put("Payment Method", i);
            }

        }

        return columnMap;
    }

    public void batchUpdateGiftCards(final Collection<GiftCard> giftCards) {
        jdbcTemplate.batchUpdate("UPDATE gift_card SET client_id = ?, amount = ? WHERE code = ?",
                giftCards, 100,
                (PreparedStatement ps, GiftCard giftCard) -> {
                    ps.setInt(1, (int) giftCard.getClient_id());
                    ps.setDouble(2,giftCard.getAmount());
                    ps.setString(3, giftCard.getCode());
                });
    }

    public void batchCreateGiftCards(final Collection<GiftCard> giftCards) {
        jdbcTemplate.batchUpdate("INSERT INTO gift_card (code, amount, client_id) VALUES (?, ?, ?)",
                giftCards, 100,
                (PreparedStatement ps, GiftCard giftCard) -> {
                    ps.setString(1,giftCard.getCode());
                    ps.setDouble(2,giftCard.getAmount());
                    ps.setInt(3, giftCard.getClient_id());
                });
    }



    private HashMap<String,GiftCard> readLinesFromListAndPopulateTransactionsGiftCards(List<String> listOfStringsFromBufferedReader,
                                                                   HashMap<String,GiftCard> giftCardMapFromFile,
                                                                   Set<Transaction> setOfTransactions,
                                                                   HashMap<String, Integer> columnMap) {

        // A list of gift card IDs that already exist in the database
        // if its a brand new gift card ID add it into the database

        List<GiftCard> listOfGiftCards = retrieveAllGiftCards();

        //logic for duplicates
        HashMap<String,GiftCard> mapOfExistingGiftCards = new HashMap<>();

        for (int i = 0; i < listOfGiftCards.size(); i++) {
            GiftCard currentGiftCard = listOfGiftCards.get(i);
            mapOfExistingGiftCards.put(currentGiftCard.getCode(),currentGiftCard);
        }

        Set<String> giftCardCodesToUpdate = new HashSet<>();

        // Is there a way to find duplicate transactions vs new ones?
        // If the amount being subtracted makes the gift card amount go less than zero than it probably shouldn't go through?

        for (int i = 0; i < listOfStringsFromBufferedReader.size(); i++) {
            String thisLine = listOfStringsFromBufferedReader.get(i);
            String[] splitLine = thisLine.split(",");



            int saleId = Integer.valueOf(splitLine[columnMap.get("Sale ID")]);
            Sale getSaleObject = saleDao.getSaleBySaleId(saleId);

            for (int j = 0; getSaleObject.getPackages_purchased_array() != null && j < getSaleObject.getPackages_purchased_array().length; j++) {
                int[] packagePurchaseArray = getSaleObject.getPackages_purchased_array();
                    updatePaymentIdForGiftCardPurchase(packagePurchaseArray[j]);
            }
            int clientId = Integer.valueOf(splitLine[columnMap.get("ClientID")]);

            String giftCardCode = splitLine[columnMap.get("Gift Card ID")];

            String stringAmount = splitLine[columnMap.get("Amount")];

            boolean isTransaction = parseAmount(stringAmount);

            // Remove dollar sign and parentheses, if present
            String parsedString = stringAmount.replaceAll("[\\$()]", "");

            double amount = Double.valueOf(parsedString);

            // If it's the gift card code is used in a transaction
            if (isTransaction) {

                // Check if gift card ID exists in our database:
                if (mapOfExistingGiftCards.containsKey(giftCardCode)) {
                    GiftCard currentGiftCard = mapOfExistingGiftCards.get(giftCardCode);

                    // If exists: Check if subtraction goes below zero:
                    // If it goes below zero: don't follow through.
                    // Else follow through and add transaction to set
                    if (currentGiftCard.getAmount() - amount >= 0) {

                        Transaction transaction = new Transaction();

                        transaction.setSale_id(saleId);

                        transaction.setClient_id(clientId);
                        transaction.setPayment_amount(amount);
                        transaction.setPayment_type("Gift Card Code");
                        transaction.setGift_code(giftCardCode);
                        setOfTransactions.add(transaction);

                        double amountToSetTo = currentGiftCard.getAmount() - amount;
                        currentGiftCard.setAmount(amountToSetTo);
                        currentGiftCard.setClient_id(clientId);
                        // update object in map of existing dbs
                        mapOfExistingGiftCards.put(giftCardCode,currentGiftCard);

                        // this will help us filter which gift cards to update that exist in our db
                        giftCardCodesToUpdate.add(currentGiftCard.getCode());
                    }
                } else if (giftCardMapFromFile.containsKey(giftCardCode)) {
                    // Check if gift card ID exists in our map of new Gift cards:
                    // If exists: Check if subtraction goes below zero:
                    // If it goes below zero: don't follow through.
                    // Else follow through and add Transaction to set
                    GiftCard currentGiftCard = giftCardMapFromFile.get(giftCardCode);
                    currentGiftCard.setClient_id(clientId);
                    Transaction transaction = new Transaction();

                    transaction.setSale_id(saleId);
                    transaction.setClient_id(clientId);
                    transaction.setPayment_amount(amount);
                    transaction.setPayment_type("Gift Card Code");
                    transaction.setGift_code(giftCardCode);
                    setOfTransactions.add(transaction);

                    double amountToSetTo = currentGiftCard.getAmount() - amount;

                    currentGiftCard.setAmount(amountToSetTo);
                    currentGiftCard.setClient_id(clientId);

                    giftCardMapFromFile.put(giftCardCode,currentGiftCard);

                } else {
                    // If it doesn't exist at all in our DB or new gift cards it has no history:
                    // Create a Gift Card with amount set to zero and a Transaction with amount used
                    // add Gift Card to map, and transaction to set

                    GiftCard giftCard = new GiftCard();

                    giftCard.setCode(giftCardCode);
                    giftCard.setAmount(0.0);
                    giftCard.setClient_id(clientId);

                    giftCardMapFromFile.put(giftCardCode,giftCard);

                    Transaction transaction = new Transaction();

                    transaction.setSale_id(saleId);
                    transaction.setClient_id(clientId);
                    transaction.setPayment_amount(amount);
                    transaction.setPayment_type("Gift Card Code");
                    transaction.setGift_code(giftCardCode);

                    setOfTransactions.add(transaction);

                }

            } else {
                // Else If it's a gift card purchase
                // Check if the gift card ID is already in our database or in our map of new Gift cards:
                // If exists: don't follow through.
                // If it doesn't exist: Store into gift card object in our new map of Gift Cards
                if (!giftCardMapFromFile.containsKey(giftCardCode) && !mapOfExistingGiftCards.containsKey(giftCardCode)) {

                    GiftCard giftCard = new GiftCard();

                    giftCard.setCode(giftCardCode);
                    giftCard.setAmount(amount);
                    giftCard.setClient_id(0);
                    giftCardMapFromFile.put(giftCardCode,giftCard);
                }
            }
        }

        // Returns a separate collection of existing gift cards whose amount we need to update in our DB
        return filterGiftCards(mapOfExistingGiftCards,giftCardCodesToUpdate);
    }

    public static HashMap<String, GiftCard> filterGiftCards(
            HashMap<String, GiftCard> mapOfExistingGiftCards,
            Set<String> giftCardCodesToUpdate) {

        HashMap<String, GiftCard> filteredGiftCards = new HashMap<>();

        for (String code : giftCardCodesToUpdate) {
            if (mapOfExistingGiftCards.containsKey(code)) {
                filteredGiftCards.put(code, mapOfExistingGiftCards.get(code));
            }
        }

        return filteredGiftCards;
    }

    public static boolean parseAmount(String amount) {
        // Check if the amount is wrapped in parentheses
        boolean isWrapped = amount.startsWith("(") && amount.endsWith(")");

        return isWrapped;
    }

    public static String formatCardType(String inputString) {
        if (inputString.matches("(?i).*\\bCredit card\\b.*\\bSwiped\\b.*")) {
            return "Credit Card Swiped";
        } else if (inputString.matches("(?i).*\\bCredit card\\b.*\\bKeyed\\b.*")) {
            return "Credit Card Keyed/Stored";
        } else {
            return inputString;
        }
    }

    @Override
    public void uploadGiftCardSalesReport(MultipartFile multipartFile) {
        int count = 0;

        long startTimeForEntireUpload = System.nanoTime();

        List<String> listOfStringsFromBufferedReader = new ArrayList<>();

        HashMap<String,Integer> mapColumns = new HashMap<>();


        try (BufferedReader fileReader = new BufferedReader(new
                InputStreamReader(multipartFile.getInputStream(), "UTF-8"))) {

            String line;
            while ((line = fileReader.readLine()) != null) {

                if (count > 0) {

                    listOfStringsFromBufferedReader.add(line);

                } else {
                    String[] firstLine =  line.split(",");
                    mapColumns = populateColumnsForGiftCardSalesMap(firstLine);
                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<PackagePurchase> packagePurchaseSet = new HashSet<>();
        HashMap<Integer, Sale> mapOfSalesFromFile = new HashMap<>();

        readLinesFromListForGiftCardSalesReport(listOfStringsFromBufferedReader,packagePurchaseSet,mapOfSalesFromFile, mapColumns);

        if (!packagePurchaseSet.isEmpty()) {
            batchCreatePackagePurchases(packagePurchaseSet);
        }

        Set<Sale> setOfSales = new HashSet<>(mapOfSalesFromFile.values());

        if (!setOfSales.isEmpty()) {
            batchCreateSales(setOfSales);
        }
    }

    public static HashMap<String, Integer> populateColumnsForGiftCardSalesMap(String[] array) {
        HashMap<String, Integer> columnMap = new HashMap<>();

        for (int i = 0; i < array.length; i++) {
            String currentString = array[i];
            if (currentString.contains("SaleDate")) {
                columnMap.put("SaleDate", i);
            } else if (currentString.contains("Client ID")) {
                columnMap.put("Client ID", i);
            } else if (currentString.contains("Sale ID")) {
                columnMap.put("Sale ID", i);
            } else if (currentString.contains("ActivationDate")) {
                columnMap.put("ActivationDate", i);
            } else if (currentString.contains("ExpDate")) {
                columnMap.put("ExpDate", i);
            } else if (currentString.contains("Package_id")) {
                columnMap.put("Package_id", i);
            } else if (currentString.contains("Batch #")) {
                columnMap.put("Batch #", i);
            } else if (currentString.contains("Quantity")) {
                columnMap.put("Quantity", i);
            } else if (currentString.contains("Discount amount")) {
                columnMap.put("Discount amount", i);
            } else if (currentString.contains("Item Total")) {
                columnMap.put("Item Total", i);
            } else if (currentString.contains("Total Paid w/ Payment Method")) {
                columnMap.put("Total Paid w/ Payment Method", i);
            } else if (currentString.contains("Payment Method")) {
                columnMap.put("Payment Method", i);
            }

        }

        return columnMap;
    }

    private void readLinesFromListForGiftCardSalesReport(List<String> listOfStringsFromBufferedReader,
                                                                       Set<PackagePurchase> packagePurchaseSet,
                                                                       HashMap<Integer, Sale> mapOfSale,
                                                                        HashMap<String,Integer> columnMap) {

        List<PackageDetails> listOfAllPackages = packageDetailsDao.getAllPackages();

        Map<Integer,PackageDetails> mapOfPackages = new HashMap<>();

        for (int i = 0; i < listOfAllPackages.size(); i++) {
            PackageDetails currentPackage = listOfAllPackages.get(i);
            mapOfPackages.put(currentPackage.getPackage_id(), currentPackage);
        }

        int maxId = findHighestPackagePurchaseId();
        maxId += 100001;

        //retrieve a set of integer sale_id's already present in the database
        Set<Integer> setOfSaleIds = saleDao.getAllSaleIds();

        for (int i = 0; i < listOfStringsFromBufferedReader.size(); i++) {
            String thisLine = listOfStringsFromBufferedReader.get(i);
            String[] splitLine = thisLine.split(",");


            PackagePurchase packagePurchase = new PackagePurchase();

            int clientId = Integer.valueOf(splitLine[columnMap.get("Client ID")]);
            packagePurchase.setClient_id(clientId);

            int saleId = Integer.valueOf(splitLine[columnMap.get("Sale ID")]);

            // Handle duplicates here (look for if we already have the sale ID)
            if (!setOfSaleIds.isEmpty() && setOfSaleIds.contains(saleId)) {
                continue;
            }


            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
                java.util.Date parsedDate = dateFormat.parse(splitLine[columnMap.get("SaleDate")]);
                Timestamp datePurchased = new Timestamp(parsedDate.getTime());
                packagePurchase.setDate_purchased(datePurchased);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat dateFormatForSql = new SimpleDateFormat("M/d/yyyy");

            try {
                if (splitLine[columnMap.get("ActivationDate")].length() > 0) {
                    java.util.Date parsedActivationDateSql = dateFormatForSql.parse((splitLine[columnMap.get("ActivationDate")]));
                    Date activationDateSql = new Date(parsedActivationDateSql.getTime());

                    packagePurchase.setActivation_date(activationDateSql);
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
                    java.util.Date parsedDate = dateFormat.parse(splitLine[columnMap.get("SaleDate")]);
                    Date activationDateSql = new Date(parsedDate.getTime());

                    packagePurchase.setActivation_date(activationDateSql);
                }

            } catch (ParseException e) {
                System.out.println("Error parsing activation date for sale id: " + saleId);
            }



            try {
                if (splitLine[columnMap.get("ExpDate")].length() > 0) {
                    java.util.Date parsedExpirationDateSql = dateFormatForSql.parse((splitLine[columnMap.get("ExpDate")]));
                    Date expirationDateSql = new Date(parsedExpirationDateSql.getTime());

                    packagePurchase.setExpiration_date(expirationDateSql);
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
                    java.util.Date parsedDate = dateFormat.parse(splitLine[columnMap.get("SaleDate")]);
                    Date expirationDateSql = new Date(parsedDate.getTime());

                    packagePurchase.setExpiration_date(expirationDateSql);

                }

            } catch (ParseException e) {
                System.out.println("Error parsing expiration date for sale id: " + saleId);
            }


            int packageId = Integer.valueOf(splitLine[columnMap.get("Package_id")]);
            packagePurchase.setPackage_id(packageId);

            PackageDetails currentPackage = mapOfPackages.get(packageId);
            packagePurchase.setClasses_remaining(currentPackage.getClasses_amount());
            packagePurchase.setIs_monthly_renew(currentPackage.isIs_recurring());

            String discountString = splitLine[columnMap.get("Discount amount")].replaceAll("[^\\d.]", "");
            discountString = discountString.replaceAll("\\.{2,}", ".");
            BigDecimal discount = new BigDecimal(discountString);
            packagePurchase.setDiscount(discount);

            String totalAmountPaidString = splitLine[columnMap.get("Item Total")].replaceAll("[^\\d.]", "");
            totalAmountPaidString = totalAmountPaidString.replaceAll("\\.{2,}", ".");
            BigDecimal totalAmountPaid = new BigDecimal(totalAmountPaidString);
            packagePurchase.setTotal_amount_paid(totalAmountPaid);

            packagePurchase.setPaymentId("Gift Card Code");

            Set<Integer> packagePurchaseIds = new HashSet<>();
            packagePurchaseIds.add(maxId);
            packagePurchase.setPackage_purchase_id(maxId);

            packagePurchaseSet.add(packagePurchase);

            int quantity = Integer.valueOf(splitLine[columnMap.get("Quantity")]);

            if (quantity > 1) {

                for (int j = 1; j < quantity; j++) {
                    maxId++;

                    PackagePurchase newPurchase = new PackagePurchase();
                    newPurchase.setPackage_purchase_id(maxId);
                    newPurchase.setClient_id(clientId);
                    newPurchase.setDate_purchased(packagePurchase.getDate_purchased());
                    newPurchase.setActivation_date(packagePurchase.getActivation_date());
                    newPurchase.setExpiration_date(packagePurchase.getExpiration_date());
                    newPurchase.setPackage_id(packageId);
                    newPurchase.setClasses_remaining(packagePurchase.getClasses_remaining());
                    newPurchase.setIs_monthly_renew(packagePurchase.isIs_monthly_renew());
                    newPurchase.setDiscount(packagePurchase.getDiscount());
                    newPurchase.setTotal_amount_paid(packagePurchase.getTotal_amount_paid());
                    newPurchase.setPaymentId("Gift Card Code");

                    packagePurchaseSet.add(newPurchase);
                    packagePurchaseIds.add(maxId);
                }

            }


            // Plug in Sale ID Here and build
            if (mapOfSale.containsKey(saleId)) {
                Sale sale = mapOfSale.get(saleId);
                if (splitLine[columnMap.get("Batch #")].length()>0 && !splitLine[columnMap.get("Batch #")].contains("-")) {
                    int batchNumber = Integer.valueOf(splitLine[columnMap.get("Batch #")]);
                    sale.setBatch_number(batchNumber);
                }

                sale = mapOfSale.get(saleId);
                List<Integer> tempList = new ArrayList<>(sale.getPackages_purchased_list());

                // ADD In the new package purchase and update the sale object
                for (Integer id : packagePurchaseIds) {
                    tempList.add(id);
                    sale.setPackages_purchased_list(tempList);
                }

                // Replace sale in Map
                mapOfSale.put(saleId, sale);
            } else {
                Sale sale =  new Sale();
                if (splitLine[columnMap.get("Batch #")].length()>0 && !splitLine[columnMap.get("Batch #")].contains("-")) {
                    int batchNumber = Integer.valueOf(splitLine[columnMap.get("Batch #")]);
                    sale.setBatch_number(batchNumber);
                }

                List<Integer> tempList = new ArrayList<>();
                sale.setSale_id(saleId);
                sale.setClient_id(clientId);

                for (Integer id : packagePurchaseIds) {
                    tempList.add(id);
                    // use id and Update the Payment Id to include " 'Gift Card Code' at the end"
                    updatePaymentIdForGiftCardPurchase(id);
                    sale.setPackages_purchased_list(tempList);
                }

                // Replace sale in Map
                mapOfSale.put(saleId, sale);
            }


            maxId++;
        }

    }

    @Override
    public void updatePaymentIdForGiftCardPurchase(int packagePurchaseId) {
        // Update PaymentId to have 'Gift Card Code' using the packagePurchaseId to locate it.
        String sql = "UPDATE package_purchase SET paymentId = paymentId || ' /Gift Card Code' WHERE package_purchase_id = ?";
        jdbcTemplate.update(sql, packagePurchaseId);
    }

    @Override
    public void swapPackages(int oldId, int newId, int eventId) {
        PackagePurchase oldPackage = getPackagePurchaseObjectByPackagePurchaseId(oldId);
        PackagePurchase newPackage = getPackagePurchaseObjectByPackagePurchaseId(newId);

        //For Old Package, check:
        // If it's 0 -> Continue
        // If it's ID is 22 -> Continue
        // If it's unlimited -> Continue
        if (oldId != 22 && oldId != 0 && !oldPackage.isUnlimited()) {
            // Else -> Increment
            incrementByOne(oldId);
        }

        if (newId != 22 & !newPackage.isUnlimited()) {
                // Decrement new package
            decrementByOne(newId);
        }

        // Lastly, swap the package.
        String sql = "UPDATE client_event SET package_purchase_id = ? WHERE event_id = ?";
        jdbcTemplate.update(sql, newId, eventId);

    }

    public static Timestamp convertDateStringToTimestamp(String dateString) {
        // Define the date formats
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MMM-yy");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yyyy");

        // Attempt to parse the input date string with the first format
        java.util.Date date = null;
        try {
            date = dateFormat1.parse(dateString);
        } catch (ParseException e1) {
            // If parsing fails with the first format, try the second format
            try {
                date = dateFormat2.parse(dateString);
            } catch (ParseException e2) {
                System.out.println("Error parsing date in both formats: " + dateString);
            }
        }

        // If date is still null, both formats failed, handle the error as needed
        if (date == null) {
            System.out.println("Error parsing date in both formats: " + dateString);
            return null; // Or throw an exception, depending on your requirements
        }

        // Convert java.util.Date to java.sql.Timestamp
        return new Timestamp(date.getTime());
    }

    public static java.sql.Date convertDateStringToDate(String dateString) {
        // Define the date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");

        // Parse the input date string
        java.util.Date utilDate = null;
        try {
            utilDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            System.out.println("Error parsing date in for Sales");
        }

        // Convert java.util.Date to java.sql.Date
        return new java.sql.Date(utilDate.getTime());
    }

    public void sendEmailReceipt(ClientCheckoutDTO clientCheckoutDTO, String packagesBeingBoughtForEmail, int saleId, String saleDate, String firstName, String subject, String subTotal, String tax, String total, String usedPaymentTypes) {
        if (clientCheckoutDTO.getEmailForReceipt().length() > 0 && clientCheckoutDTO.isSendEmail()) {

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
            offset = pageSize * (page - 1);
            offsetString = " OFFSET ? LIMIT " + pageSize;
        }

        List<PackagePurchase> allUserPackagePurchase = new ArrayList<>();

        String sql = "SELECT package_purchase_id, pp.client_id, pp.package_id, date_purchased, classes_remaining, activation_date, expiration_date, is_monthly_renew, total_amount_paid, discount FROM package_purchase as pp " +
                "JOIN package_details on pp.package_id = package_details.package_id " +
                "JOIN client_details on client_details.client_id = pp.client_id " +
                "WHERE client_details.user_id = ? " +
                "AND ( ( pp.classes_remaining > 0 AND pp.expiration_date > NOW()) " +
                "OR (package_details.unlimited = true AND pp.expiration_date > NOW()) " +
                "OR (package_details.package_id = 22 AND pp.expiration_date > NOW()) ) " +
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
            Set<String> capturePaymentTypes = new HashSet<>();
            if (saleId > 0) {
                SqlRowSet result3 = jdbcTemplate.queryForRowSet(sql3, saleId);
                while (result3.next()) {
                    if (!capturePaymentTypes.contains(result3.getString("payment_Type"))) {
                        paymentDescriptions += result3.getString("payment_Type") + " /";
                        capturePaymentTypes.add(result3.getString("payment_Type"));
                    }

                }
            }
            if (paymentDescriptions.length() > 0) {
                paymentDescriptions = paymentDescriptions.substring(0, paymentDescriptions.length() - 1);
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
                "OR (package_details.unlimited = true AND pp.expiration_date > NOW()) " +
                "OR (package_details.package_id = 22 AND pp.expiration_date > NOW()) ) ";

        int count = jdbcTemplate.queryForObject(countSql, Integer.class, userId);

        PaginatedListOfPurchasedPackages paginatedListOfPurchasedPackages = new PaginatedListOfPurchasedPackages(allUserPackagePurchase, count);

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
                "OR (package_details.unlimited = true AND pp.expiration_date > NOW()) " +
                "OR (package_details.package_id = 22 AND pp.expiration_date > NOW()) ) ";


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
    public int createPackagePurchase(PackagePurchase packagePurchase) {
        String sql = "INSERT INTO package_purchase (client_id, date_purchased, package_id, " +
                "classes_remaining, activation_date, expiration_date, " +
                "total_amount_paid, is_monthly_renew,  discount, paymentId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING package_purchase_id";
        return jdbcTemplate.queryForObject(sql, Integer.class, packagePurchase.getClient_id(), packagePurchase.getDate_purchased(),
                packagePurchase.getPackage_id(), packagePurchase.getClasses_remaining(),
                packagePurchase.getActivation_date(), packagePurchase.getExpiration_date(),
                packagePurchase.getTotal_amount_paid(),
                packagePurchase.isIs_monthly_renew(), packagePurchase.getDiscount(), packagePurchase.getPaymentId());
    }

    @Override
    public PackagePurchase getPackagePurchaseObjectByPackagePurchaseId(int packagePurchaseId) {
        PackagePurchase packagePurchase = null;
        String sql = "SELECT * FROM package_purchase " +
                "WHERE package_purchase_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, packagePurchaseId);
        if (result.next()) {
            packagePurchase = mapRowToPackagePurchase(result);

            // set package description
            packagePurchase.setPackage_description(getPackageDescriptionByPackageId(packagePurchase.getPackage_id()));
            packagePurchase.setUnlimited(IsSubscriptionOrNot(packagePurchase.getPackage_id()));

        }
        return packagePurchase;
    }

    public boolean IsSubscriptionOrNot(int packageId) {
        boolean isSubscription = false;
        String sql = "SELECT unlimited from package_details WHERE package_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, packageId);
        if (result.next()) {
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
        return jdbcTemplate.update(sql, packagePurchase.getPackage_purchase_id()) == 1;
    }

    @Override
    public boolean updatePackage(PackagePurchase packagePurchase) {
        String sql = "UPDATE package_purchase SET date_purchased = ?, classes_remaining = ?, " +
                "activation_date = ?, expiration_date = ?, is_monthly_renew = ? , discount = ? WHERE package_purchase_id = ?;";
        return jdbcTemplate.update(sql, packagePurchase.getDate_purchased(), packagePurchase.getClasses_remaining(), packagePurchase.getActivation_date(), packagePurchase.getExpiration_date(), packagePurchase.isIs_monthly_renew(), packagePurchase.getDiscount(), packagePurchase.getPackage_purchase_id()) == 1;
    }

    @Override
    public boolean decrementByOne(int packagePurchaseId) {
        String sql = "UPDATE package_purchase SET classes_remaining = classes_remaining - 1 WHERE package_purchase_id = ?";
        return jdbcTemplate.update(sql, packagePurchaseId) == 1;
    }

    @Override
    public boolean incrementByOne(int packagePurchaseId) {
        String sql = "UPDATE package_purchase SET classes_remaining = classes_remaining + 1 WHERE package_purchase_id = ?";
        return jdbcTemplate.update(sql, packagePurchaseId) == 1;
    }

    @Override
    public boolean updateGiftCard(GiftCard originalGiftCard, int clientId, double amountUsed) {
        int newAmount = (int) (originalGiftCard.getAmount() - amountUsed);
        String sql = "UPDATE gift_card SET amount = ? , client_id = ? WHERE code ILIKE ?";
        return jdbcTemplate.update(sql, newAmount, clientId, originalGiftCard.getCode()) == 1;
    }


    //helper
    @Override
    public PackagePurchase filterPackageList(List<PackagePurchase> packagePurchaseList, ClassEvent classEvent) {
        // set up the one packagePurchase Object to return
        PackagePurchase packagePurchaseQuantity = new PackagePurchase();
        PackagePurchase packagePurchaseSubscription = new PackagePurchase();
        PackagePurchase packagePurchaseConsolidation = new PackagePurchase();
        int classAmount = 100;
        boolean hasQuantity = false;
        boolean hasConsolidation = false;
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

            } else if (!currentPackage.isUnlimited() && currentPackage.getClasses_remaining() > 0) {
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

            } else  if (currentPackage.getPackage_id() == 22) {
                hasConsolidation = true;
                packagePurchaseConsolidation = currentPackage;
            }
        }
        if (hasQuantity) {
            return packagePurchaseQuantity;
        }

        if (packagePurchaseQuantity.getPackage_purchase_id() > 0) {
            return packagePurchaseQuantity;
        }

        if (hasConsolidation) {
            return packagePurchaseConsolidation;
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
                "AND ( (classes_remaining > 0 AND package_purchase.expiration_date > NOW()) ) \n" +
                "ORDER BY expiration_date;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, client_id, client_id);
        while (result.next()) {
            packages.add(mapRowToPackagePurchase(result));
        }
        return packages;
    }

    @Override
    public List<PackagePurchase> getAllActivatePackagesToSwap(int client_id) {
        List<PackagePurchase> activePackages = new ArrayList<>();


        ClientDetails clientDetails = clientDetailsDao.findClientByClientId(client_id);
        activePackages = getAllActiveUserPackagePurchases(clientDetails.getUser_id());
        for (int i = 0; i < activePackages.size(); i++) {
            PackagePurchase packagePurchase = activePackages.get(i);
            packagePurchase.setPackage_description(getPackageDescriptionByPackageId(packagePurchase.getPackage_id()));

        }
        List<PackagePurchase> sharedPackages = getAllSharedActiveQuantityPackages(client_id);
        for (int i = 0; i < sharedPackages.size(); i++) {
            PackagePurchase packagePurchase = sharedPackages.get(i);
            packagePurchase.setPackage_description("(Shared) " + getPackageDescriptionByPackageId(packagePurchase.getPackage_id()));
        }
        activePackages.addAll(sharedPackages);

        for (int i = 0; i < activePackages.size(); i++) {
            PackagePurchase packagePurchase = activePackages.get(i);

            //TODO: Make sure to add quick_details
            String packageDescription = packagePurchase.getPackage_description();
            String expirationString = "";
            Date expirationDate = packagePurchase.getExpiration_date();
            // Format pattern for the date
            String pattern = "yyyy-MM-dd"; // You can change the pattern based on your requirements

            // Create a SimpleDateFormat object with the specified pattern
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            expirationString = sdf.format(expirationDate);
            packagePurchase.setQuick_details(packageDescription + " Exp. " + expirationString);

        }
        return activePackages;
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

    public void createGiftCard(String code, double amount) {
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

    @Override
    public List<GiftCard> retrieveAllGiftCards() {
        List<GiftCard> listOfGiftCards = new ArrayList<>();

        String sql = "SELECT * FROM gift_card";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);

        while (result.next()) {
            GiftCard giftCard = mapRowToGiftCard(result);
            listOfGiftCards.add(giftCard);

        }

        return  listOfGiftCards;
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

        if (checkoutItemDTO.getPackageDuration() > 0 && checkoutItemDTO.isUnlimited()) {
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
        if (packagePurchase.getPackage_duration() > 0 && packagePurchase.isUnlimited()) {
            String sql = "SELECT expiration_date FROM package_purchase JOIN package_details ON package_details.package_id = package_purchase.package_id WHERE package_details.unlimited = true AND client_id = ? ORDER BY expiration_date DESC LIMIT 1;";
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, packagePurchase.getClient_id());
            if (results.next()) {
                LocalDate localDate = results.getDate("expiration_date").toLocalDate();
                return localDate.plusDays(1);
            }
        }
        return LocalDate.now();
    }

    private int findHighestPackagePurchaseId() {
        String sql = "SELECT MAX(package_purchase_id) FROM package_purchase";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        if (result.next()) {
            int maxId = result.getInt("max");
            if (maxId > 0) {
                return maxId;
            }

        }
        return 0;
    }

    private LocalDate returnCorrectPackageExpirationDateForPackagePurchase(PackagePurchase packagePurchase, LocalDate activationDate) {
        if (packagePurchase.getPackage_duration() > 0) {


            return activationDate.plusMonths(packagePurchase.getPackage_duration()).plusDays(1);
        }
        return LocalDate.now().plusYears(1).plusDays(1);
    }

    public int createOneMonthAutoRenewPurchase(CheckoutItemDTO checkoutItemDTO) {
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

    public int createSixMonthAutoRenewPurchase(CheckoutItemDTO checkoutItemDTO) {
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

        for (Transaction transaction : transactions) {
            transaction.setSale_id(saleId);

            transactionDao.createTransaction(transaction);
        }

    }


}
