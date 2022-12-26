package com.sattvayoga.dao;

import com.sattvayoga.model.ClassDetails;
import com.sattvayoga.model.PackageDetails;
import com.sattvayoga.model.TeacherDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.parameters.P;
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
    public boolean createPackage(PackageDetails packageDetails) {
        String sql = "INSERT INTO package_details (description, package_cost, " +
                "classes_amount, subscription_duration, is_subscription, is_in_person) VALUES " +
                "(?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql, packageDetails.getDescription(), packageDetails.getPackage_cost(),
                packageDetails.getClasses_amount(), packageDetails.getSubscription_duration(),
                packageDetails.isIs_subscription(), packageDetails.isIs_in_person()) == 1;
    }


    @Override
    public List<PackageDetails> getAllPackages() {
        List<PackageDetails> allPackages = new ArrayList<>();

        String sql = "SELECT * FROM package_details;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while (result.next()) {
            PackageDetails packageDetails = mapRowToPackage(result);


            allPackages.add(packageDetails);
        }

        return allPackages;
    }

   @Override
   public boolean updatePackage(PackageDetails packageDetails) {
        String sql = "UPDATE package_details SET package_id = ? , " +
                "description = ? , " +
                "package_cost = ? , " +
                "classes_amount = ? , " +
                "subscription_duration = ? , " +
                "is_subscription = ? , " +
                "is_in_person = ? " +
                "WHERE package_id = ?";
        return jdbcTemplate.update(sql, packageDetails.getPackage_id(), packageDetails.getDescription(),
                packageDetails.getPackage_cost(), packageDetails.getClasses_amount(),
                packageDetails.getSubscription_duration(), packageDetails.isIs_subscription(),
                packageDetails.isIs_in_person(), packageDetails.getPackage_id())==1;
   }

    @Override
    public boolean deletePackage(int packageId) {
        String sql = "DELETE from package_details WHERE package_id = ?;";
        return jdbcTemplate.update(sql, packageId)==1;
    }

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
        packageDetails.setIs_in_person(rs.getBoolean("is_in_person"));

        return packageDetails;
    }
//    private ClassDetails mapRowToClass(SqlRowSet rs) {
//        ClassDetails classDetails = new ClassDetails();
//        classDetails.setClass_id(rs.getInt("class_id"));
//        classDetails.setTeacher_id(rs.getInt("teacher_id"));
//        classDetails.setClass_datetime(rs.getTimestamp("class_datetime"));
//        classDetails.setClass_duration(rs.getInt("class_duration"));
//        if (rs.getBoolean("is_paid") == false || rs.getBoolean("is_paid") == true) {
//            classDetails.setIs_paid(rs.getBoolean("is_paid"));
//        }
//        classDetails.setClass_description(rs.getString("class_description"));
//        return classDetails;
//    }
}
