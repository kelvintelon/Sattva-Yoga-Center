package com.sattvayoga.dao;

import com.sattvayoga.dto.order.CheckoutItemDTO;
import com.sattvayoga.model.Event;
import com.sattvayoga.model.PackageDetails;
import com.sattvayoga.model.PackagePurchase;
import com.sattvayoga.model.PaginatedListOfPurchasedPackages;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
            packagePurchase.setIs_subscription(IsSubscriptionOrNot(packagePurchase.getPackage_id()));
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
            packagePurchase.setPackage_description(getPackageDescriptionByPackageId(packagePurchase.getPackage_id()));
            packagePurchase.setIs_subscription(IsSubscriptionOrNot(packagePurchase.getPackage_id()));
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
                "OR (package_details.is_subscription = true AND pp.expiration_date > NOW()) ) " +
                "ORDER BY " + sortBy + " " + sortDirection + offsetString;

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId, offset);
        while (result.next()) {
            PackagePurchase packagePurchase = mapRowToPackagePurchase(result);

            // set package description
            packagePurchase.setPackage_description(getPackageDescriptionByPackageId(packagePurchase.getPackage_id()));
            packagePurchase.setIs_subscription(IsSubscriptionOrNot(packagePurchase.getPackage_id()));
            allUserPackagePurchase.add(packagePurchase);
        }


        String countSql = "SELECT COUNT(*) FROM package_purchase as pp " +
                "JOIN package_details on pp.package_id = package_details.package_id " +
                "JOIN client_details on client_details.client_id = pp.client_id " +
                "WHERE client_details.user_id = ? " +
                "AND ( ( pp.classes_remaining > 0 AND pp.expiration_date > NOW()) " +
                "OR (package_details.is_subscription = true AND pp.expiration_date > NOW()) ) ";

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
                "OR (package_details.is_subscription = true AND pp.expiration_date > NOW()) ) ";


        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId);
        while (result.next()) {
            PackagePurchase packagePurchase = mapRowToPackagePurchase(result);

            // set package description
            packagePurchase.setPackage_description(getPackageDescriptionByPackageId(packagePurchase.getPackage_id()));
            packagePurchase.setIs_subscription(IsSubscriptionOrNot(packagePurchase.getPackage_id()));

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
            packagePurchase.setIs_subscription(IsSubscriptionOrNot(packagePurchase.getPackage_id()));

        }
        return packagePurchase;
    }

    public boolean IsSubscriptionOrNot(int packageId){
        boolean isSubscription = false;
        String sql = "SELECT is_subscription from package_details WHERE package_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, packageId);
        if(result.next()){
            return result.getBoolean("is_subscription");
        }
        return isSubscription;
    }

    @Override
    public boolean expirePackage(PackagePurchase packagePurchase) {
        String sql = "UPDATE package_purchase SET classes_remaining = 0 " +
                "WHERE package_purchase_id = ?;";
        if (packagePurchase.isIs_subscription()) {
            sql = "UPDATE package_purchase SET expiration_date = current_date - INTEGER '1' " +
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

    //helper
    @Override
    public PackagePurchase filterPackageList(List<PackagePurchase> packagePurchaseList, Event event) {
        // set up the one packagePurchase Object to return
        PackagePurchase packagePurchaseQuantity = new PackagePurchase();
        PackagePurchase packagePurchaseSubscription = new PackagePurchase();
        int classAmount = 100;
        boolean hasQuantity = false;
        for (int i = 0; i < packagePurchaseList.size(); i++) {
            PackagePurchase currentPackage = packagePurchaseList.get(i);

            // if they have a subscription make sure the class they are being signed up for is not after their expiration date
            if (currentPackage.isIs_subscription()) {

                // could check for the expiration date right here as well/
                // compare the expiration date to the starting time of the event
                Timestamp eventTime = event.getStart_time();
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
            else if (!currentPackage.isIs_subscription() && currentPackage.getClasses_remaining() > 0) {
                // compare the expiration date to the starting time of the event
                Timestamp eventTime = event.getStart_time();
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
        return packageDetails.getDescription();
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
        if (rs.getInt("subscription_duration") != 0) {
            packageDetails.setSubscription_duration(rs.getInt("subscription_duration"));
        }
        packageDetails.setIs_subscription(rs.getBoolean("is_subscription"));
        packageDetails.setIs_visible_online((rs.getBoolean("is_visible_online")));

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

    public void createGiftCard(String code, double amount){
        String sql = "INSERT INTO gift_card (code, amount) VALUES (?,?)";
        jdbcTemplate.update(sql, code, amount);
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
        return coupon.toString();
    }

    public void createPackagePurchase2(CheckoutItemDTO checkoutItemDTO) {
        String sql = "INSERT INTO package_purchase (client_id, date_purchased, package_id, classes_remaining, activation_date, expiration_date, is_monthly_renew, total_amount_paid, discount ) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, checkoutItemDTO.getClient_id(), LocalDateTime.now(),
                checkoutItemDTO.getPackage_id(), checkoutItemDTO.getClasses_remaining(),
                LocalDate.now(), LocalDate.now().plusYears(1), checkoutItemDTO.isIs_monthly_renew(),
                checkoutItemDTO.getTotal_amount_paid(), 0);
    }

    public void createOneMonthPurchase(CheckoutItemDTO checkoutItemDTO){
        String sql = "INSERT INTO package_purchase (client_id, date_purchased, package_id, classes_remaining, activation_date, expiration_date, is_monthly_renew, total_amount_paid, discount ) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, checkoutItemDTO.getClient_id(), LocalDateTime.now(),
                checkoutItemDTO.getPackage_id(), 0, LocalDate.now(),
                LocalDate.now().plusMonths(1), true,
                checkoutItemDTO.getTotal_amount_paid(), 0);
    }

    public void createSixMonthPurchase(CheckoutItemDTO checkoutItemDTO){
        String sql = "INSERT INTO package_purchase (client_id, date_purchased, package_id, classes_remaining, activation_date, expiration_date, is_monthly_renew, total_amount_paid, discount ) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, checkoutItemDTO.getClient_id(), LocalDateTime.now(),
                checkoutItemDTO.getPackage_id(), 0, LocalDate.now(),
                LocalDate.now().plusMonths(6), true,
                checkoutItemDTO.getTotal_amount_paid(), 0);
    }


}
