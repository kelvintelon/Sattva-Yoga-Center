package com.sattvayoga.dao;

import com.sattvayoga.model.ClassAttendance;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcClassAttendanceDao implements ClassAttendanceDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcClassAttendanceDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean registerIntoClass(ClassAttendance classAttendance) {


        String sql = "INSERT INTO class_attendance (class_id, client_id, package_id, class_purchase_id, is_new_client, " +
                "is_drop_in, mat_use_fee, is_guest, attendance_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql, classAttendance.getClass_id(), classAttendance.getClient_id(),
                classAttendance.getPackage_id(), classAttendance.getClass_purchase_id(), classAttendance.isIs_new_client(),
                classAttendance.isIs_drop_in(), classAttendance.getMat_use_fee(),
                classAttendance.isIs_guest(), classAttendance.getAttendance_count()) == 1;
    }
}
