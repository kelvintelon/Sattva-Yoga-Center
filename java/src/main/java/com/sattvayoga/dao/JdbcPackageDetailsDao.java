package com.sattvayoga.dao;

import com.sattvayoga.model.PackageDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
