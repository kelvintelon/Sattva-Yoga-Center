package com.sattvayoga.dao;

import com.sattvayoga.model.ClassEvent;
import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.PackageDetails;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.search.SearchTerm;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.*;

@Component
public class JdbcPackageDetailsDao implements PackageDetailsDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPackageDetailsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void createPackage(PackageDetails packageDetails) {
        String sql = "INSERT INTO package_details (description, package_cost, " +
                "classes_amount, package_duration, unlimited, is_visible_online, is_recurring, active) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?) RETURNING package_id;";

        int packageId = jdbcTemplate.queryForObject(sql, Integer.class, packageDetails.getDescription(), packageDetails.getPackage_cost(),
                packageDetails.getClasses_amount(), packageDetails.getPackage_duration(),
                packageDetails.isUnlimited(), packageDetails.isIs_visible_online(), packageDetails.isIs_recurring(), packageDetails.isActive());
        int desiredPackageOrder = packageDetails.getPackage_order();
        packageDetails.setPackage_id(packageId);

        setPackageOrderInCreation(packageDetails, packageId, desiredPackageOrder);

    }

    private void setPackageOrderInCreation(PackageDetails packageDetails, int packageId, int desiredPackageOrder) {
        if (packageId == desiredPackageOrder || desiredPackageOrder > packageId) {
            String sql2 = "UPDATE package_details SET package_order = ? WHERE package_id = ?";
            jdbcTemplate.update(sql2, packageId, packageId);
        } else {

            List<PackageDetails> allPackages = getAllPackages();
            List<PackageDetails> listToLoopThrough = new ArrayList<>(allPackages);

            // First loop is just finding the exact index to insert our object
            int indexToStartModifying = 0;

            for (int i = 0; i < listToLoopThrough.size(); i++) {
                PackageDetails currentPackage = listToLoopThrough.get(i);

                if (currentPackage.getPackage_order() == desiredPackageOrder) {
                    // Replace the spot
                    allPackages.add(i, packageDetails);
                }
            }
            listToLoopThrough = new ArrayList<>(allPackages);
            // This loop is to delete it
            for (int i = 0; i < listToLoopThrough.size(); i++) {
                PackageDetails currentPackage = listToLoopThrough.get(i);
                // If I find the duplicate with undesired package order, I remove it
                if (currentPackage.getPackage_order() != desiredPackageOrder && currentPackage.getDescription().equalsIgnoreCase(packageDetails.getDescription())) {
                    allPackages.remove(i);
                }
            }

            // Third loop goes through and adjusts all the packages that come after the one that was inserted
            List<PackageDetails> packageListToUpdate = new ArrayList<>();

            for (int i = 0; i < allPackages.size(); i++) {
                PackageDetails currentPackage = allPackages.get(i);
                currentPackage.setPackage_order(i+1);
                packageListToUpdate.add(currentPackage);
            }

            // Last loop Updates all the packages
            for (PackageDetails eachPackage: packageListToUpdate) {
                updateSinglePackage(eachPackage);
            }
        }
    }


    @Override
    public List<PackageDetails> getAllPackages() {
        List<PackageDetails> allPackages = new ArrayList<>();

        String sql = "SELECT * FROM package_details ORDER BY package_order;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while (result.next()) {
            PackageDetails packageDetails = mapRowToPackage(result);


            allPackages.add(packageDetails);
        }

        return allPackages;
    }

    @Override
    public PackageDetails findPackageByPackageName(String packageName) {
        String sql = "SELECT * FROM package_details WHERE description = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, packageName);
        PackageDetails packageDetails = new PackageDetails();
        if (result.next()) {
            packageDetails = mapRowToPackage(result);
        }
        return packageDetails;
    }

    @Override
    public PackageDetails findPackageByPackageId(int packageId) {
        String sql = "SELECT * FROM package_details WHERE package_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, packageId);
        PackageDetails packageDetails = new PackageDetails();
        if (result.next()) {
            packageDetails = mapRowToPackage(result);
        }
        return packageDetails;
    }

    @Override
    public PackageDetails findPackageBySubscriptionDuration(int subscriptionDuration) {
        String sql = "SELECT * FROM package_details WHERE is_recurring = true AND unlimited = true AND package_duration = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, subscriptionDuration);
        PackageDetails packageDetails = new PackageDetails();
        if (result.next()) {
            packageDetails = mapRowToPackage(result);
        }
        return packageDetails;
    }

    @Override
    public void uploadPackageCsv(MultipartFile multipartFile) {

        int count = 0;

        long startTimeForEntireUpload = System.nanoTime();

        List<String> listOfStringsFromBufferedReader = new ArrayList<>();

        HashSet<PackageDetails> setOfPackageDetailsFromFile = new HashSet<>();

        try (BufferedReader fileReader = new BufferedReader(new
                InputStreamReader(multipartFile.getInputStream(), "UTF-8"))) {

            String line;
            while ((line = fileReader.readLine()) != null) {

                if (count > 0) {

                    listOfStringsFromBufferedReader.add(line);

                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        readLinesFromListAndPopulateSets(listOfStringsFromBufferedReader, setOfPackageDetailsFromFile);

        // We turn it into a list in order to modify as we iterate
        List<PackageDetails> packageDetailsList = new ArrayList<>(setOfPackageDetailsFromFile);

        Set<String> packageNamesSet = new HashSet<>();
        Set<Integer> packageIdSet = new HashSet<>();
        List<PackageDetails> getAllPackages = getAllPackages();


        for (PackageDetails packageObj:
             getAllPackages) {
            packageNamesSet.add(packageObj.getDescription());
            packageIdSet.add(packageObj.getPackage_id());
        }

        //TODO: Check duplicates with A Set<String> of existing package names,  then remove that packageDetails from setOfPackageDetails
        // Loop through the packageDetailsList
        for (int i = 0; i < packageDetailsList.size(); i++) {
            PackageDetails currentPackage = packageDetailsList.get(i);
            boolean foundExistingName = false;
            boolean foundExistingId = false;
            if (packageNamesSet.contains(currentPackage.getDescription())) {
                foundExistingName = true;
            }
            if (packageIdSet.contains(currentPackage.getPackage_id())) {
                foundExistingId = true;
            }

            if (!foundExistingName && !foundExistingId) {
                packageNamesSet.add(currentPackage.getDescription());
                packageIdSet.add(currentPackage.getPackage_id());
            }

            if (foundExistingName || foundExistingId) {
                setOfPackageDetailsFromFile.remove(currentPackage);
            }
        }

        if (!setOfPackageDetailsFromFile.isEmpty()) {
            batchCreatePackages(setOfPackageDetailsFromFile);
        }

    }

    private void readLinesFromListAndPopulateSets(List<String> listOfStringsFromBufferedReader, HashSet<PackageDetails> setOfPackageDetailsFromFile) {
        for(int i = 0; i < listOfStringsFromBufferedReader.size(); i++) {
            PackageDetails packageDetails = new PackageDetails();

            String thisLine = listOfStringsFromBufferedReader.get(i);
            String[] splitLine = thisLine.split(",");
            int packageId = 0;
            if (!splitLine[0].isEmpty()) {
                packageId = Integer.valueOf(splitLine[0]);
            }

            packageDetails.setPackage_id(packageId);

            String description = splitLine[1];

            packageDetails.setDescription(description);

            String packageCostString = splitLine[2];

            BigDecimal packageCost = new BigDecimal(packageCostString);

            packageDetails.setPackage_cost(packageCost);

            String classAmountString = splitLine[3];

            int classAmount = Integer.valueOf(classAmountString);

            packageDetails.setClasses_amount(classAmount);

            String packageDurationString = splitLine[4];

            int packageDuration = Integer.valueOf(packageDurationString);

            packageDetails.setPackage_duration(packageDuration);

            String unlimitedString = splitLine[5];

            boolean unlimited = Boolean.valueOf(unlimitedString.toLowerCase());

            packageDetails.setUnlimited(unlimited);

            String visibleOnlineString = splitLine[6];

            boolean isVisibleOnline = Boolean.valueOf(visibleOnlineString.toLowerCase());

            packageDetails.setIs_visible_online(isVisibleOnline);

            String isRecurringString = splitLine[7];

            boolean isRecurring = Boolean.valueOf(isRecurringString.toLowerCase());

            packageDetails.setIs_recurring(isRecurring);

            String isActiveString = splitLine[8];

            boolean isActive = Boolean.valueOf(isActiveString.toLowerCase());

            packageDetails.setActive(isActive);

            String orderString = splitLine[9];

            int order = Integer.valueOf(orderString);

            packageDetails.setPackage_order(order);

            // Set the packageDetails object into the set
            setOfPackageDetailsFromFile.add(packageDetails);
        }
    }

    public void batchCreatePackages(final Collection<PackageDetails> packages) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO package_details (package_id, description, package_cost, " +
                        "classes_amount, package_duration, unlimited, is_visible_online, " +
                        "is_recurring, active, package_order) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                packages,
                100,
                (PreparedStatement ps, PackageDetails packageDetails) -> {
                    ps.setInt(1, packageDetails.getPackage_id());
                    ps.setString(2, packageDetails.getDescription());
                    ps.setBigDecimal(3, packageDetails.getPackage_cost());
                    ps.setInt(4, packageDetails.getClasses_amount());
                    ps.setInt(5, packageDetails.getPackage_duration());
                    ps.setBoolean(6, packageDetails.isUnlimited());
                    ps.setBoolean(7, packageDetails.isIs_visible_online());
                    ps.setBoolean(8, packageDetails.isIs_recurring());
                    ps.setBoolean(9, packageDetails.isActive());
                    ps.setInt(10, packageDetails.getPackage_order());
                });
    }

    @Override
   public void updateSinglePackage(PackageDetails packageDetails) {
        String sql = "UPDATE package_details SET package_id = ? , " +
                "description = ? , " +
                "package_cost = ? , " +
                "classes_amount = ? , " +
                "package_duration = ? , " +
                "unlimited = ? , " +
                "is_visible_online = ? , " +
                "active = ? , " +
                "package_order = ? , " +
                "is_recurring = ? " +
                "WHERE package_id = ?";
        jdbcTemplate.update(sql, packageDetails.getPackage_id(), packageDetails.getDescription(),
                packageDetails.getPackage_cost(), packageDetails.getClasses_amount(),
                packageDetails.getPackage_duration(), packageDetails.isUnlimited(),
                packageDetails.isIs_visible_online(), packageDetails.isActive(), packageDetails.getPackage_order(),
                packageDetails.isIs_recurring(), packageDetails.getPackage_id());
   }

    @Override
    public void updatePackages(PackageDetails packageDetails) {
        int desiredPackageOrder = packageDetails.getPackage_order();
        PackageDetails originalPackage = findPackageByPackageId(packageDetails.getPackage_id());

        boolean movingDown = originalPackage.getPackage_order() < desiredPackageOrder;

        if (desiredPackageOrder == originalPackage.getPackage_order()) {
            String sql = "UPDATE package_details SET package_id = ? , " +
                    "description = ? , " +
                    "package_cost = ? , " +
                    "classes_amount = ? , " +
                    "package_duration = ? , " +
                    "unlimited = ? , " +
                    "is_visible_online = ? , " +
                    "active = ? , " +
                    "is_recurring = ? " +
                    "WHERE package_id = ?";
            jdbcTemplate.update(sql, packageDetails.getPackage_id(), packageDetails.getDescription(),
                    packageDetails.getPackage_cost(), packageDetails.getClasses_amount(),
                    packageDetails.getPackage_duration(), packageDetails.isUnlimited(),
                    packageDetails.isIs_visible_online(), packageDetails.isActive(),
                    packageDetails.isIs_recurring(), packageDetails.getPackage_id());

            return;
        }



        if (desiredPackageOrder == 1) {
            packageDetails.setPackage_order(0);
            updateSinglePackage(packageDetails);

            packageDetails.setPackage_order(desiredPackageOrder);

            List<PackageDetails> allPackages = getAllPackages();

            List<PackageDetails> packagesToLoopThrough = new ArrayList<>(allPackages);

            //Delete duplicate
            for (int i = 0; i < allPackages.size(); i++) {
                PackageDetails currentPackage = packagesToLoopThrough.get(i);
                if (currentPackage.getDescription().equalsIgnoreCase(packageDetails.getDescription()) && currentPackage.getPackage_order() != 0) {
                    allPackages.remove(i);
                }
            }

            packagesToLoopThrough = new ArrayList<>(allPackages);

            List<PackageDetails> packageToUpdate = new ArrayList<>();
            for (int i = 0; i < packagesToLoopThrough.size(); i++) {
                PackageDetails currentPackage = packagesToLoopThrough.get(i);
                currentPackage.setPackage_order(i+1);
                packageToUpdate.add(currentPackage);
            }

            for (PackageDetails eachPackage : packageToUpdate) {
                updateSinglePackage(eachPackage);
            }

            return;
        } else if (desiredPackageOrder != packageDetails.getPackage_id()) {

            List<PackageDetails> allPackages = getAllPackages();

            if (desiredPackageOrder > allPackages.size()) {
                desiredPackageOrder = allPackages.size();
            }

            List<PackageDetails> packagesToLoopThrough = new ArrayList<>(allPackages);

            // Add here
            for (int i = 0; i < packagesToLoopThrough.size(); i++) {
                PackageDetails currentPackage = packagesToLoopThrough.get(i);

                if (currentPackage.getPackage_order() == desiredPackageOrder) {
                    // Replace the spot whether its moving up or down

                    if (movingDown) {
                        allPackages.add(i+1, packageDetails);
                    } else {
                        allPackages.add(i,packageDetails);
                    }

                }
            }
            packagesToLoopThrough = new ArrayList<>(allPackages);

            // Deleting duplicate here
            for (int i = 0; i < allPackages.size(); i++) {
                PackageDetails currentPackage = packagesToLoopThrough.get(i);
                if (currentPackage.getDescription().equalsIgnoreCase(packageDetails.getDescription()) && currentPackage.getPackage_order() != desiredPackageOrder) {
                    allPackages.remove(i);
                }
            }

            // Third loop goes through and adjusts all the packages that come after the one that was inserted
            List<PackageDetails> packageListToUpdate = new ArrayList<>();

            for (int i = 0; i < allPackages.size(); i++) {
                PackageDetails currentPackage = allPackages.get(i);
                currentPackage.setPackage_order(i+1);
                packageListToUpdate.add(currentPackage);
            }

            // Last loop Updates all the packages
            for (PackageDetails eachPackage: packageListToUpdate) {
                updateSinglePackage(eachPackage);
            }

//            List<PackageDetails> packageToUpdate = new ArrayList<>();
//            for (int i = 0; i < packagesToLoopThrough.size(); i++) {
//                PackageDetails currentPackage = packagesToLoopThrough.get(i);
//                boolean duplicate = false;
//                if (!currentPackage.getDescription().equalsIgnoreCase(packageDetails.getDescription()) && currentPackage.getPackage_order() == desiredPackageOrder) {
//                    currentPackage.setPackage_order(i+1+1);
//                    duplicate = true;
//                } else {
//                    if (duplicate) {
//                        currentPackage.setPackage_order(i+1+1);
//                    } else {
//                        currentPackage.setPackage_order(i+1);
//                    }
//
//                }
//                packageToUpdate.add(currentPackage);
//            }
//
//            for (PackageDetails eachPackage : packageToUpdate) {
//                updateSinglePackage(eachPackage);
//            }

            return;
        }

    }

    @Override
    public boolean deletePackage(int packageId) {
        String sql = "BEGIN TRANSACTION;\n" +
                "\n" +
                "DELETE FROM package_details\n" +
                "WHERE package_id = ?;\n" +
                "\n" +
                "COMMIT TRANSACTION;";
        return jdbcTemplate.update(sql, packageId)==1;
    }

    @Override
    public List<PackageDetails> getAllPublicPackages() {
        List<PackageDetails> allPackages = new ArrayList<>();

        String sql = "SELECT * FROM package_details WHERE is_visible_online = TRUE AND active = TRUE ORDER BY package_order;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while (result.next()) {
            PackageDetails packageDetails = mapRowToPackage(result);


            allPackages.add(packageDetails);
        }

        return allPackages;
    }

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
        packageDetails.setIs_visible_online(rs.getBoolean("is_visible_online"));
        packageDetails.setIs_recurring(rs.getBoolean("is_recurring"));
        packageDetails.setActive(rs.getBoolean("active"));
        packageDetails.setPackage_order(rs.getInt("package_order"));
        return packageDetails;
    }

}
