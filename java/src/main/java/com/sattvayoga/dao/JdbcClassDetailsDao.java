package com.sattvayoga.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.sattvayoga.model.ClassDetails;

@Component
public class JdbcClassDetailsDao implements ClassDetailsDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcClassDetailsDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    @Override
    public boolean createClass(ClassDetails classDetails) {

        String sql = "INSERT INTO class_details (teacher_id, class_datetime, class_duration, is_paid, " +
                "class_description) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, classDetails.getTeacher_id(), classDetails.getClass_datetime(),
                classDetails.getClass_duration(), classDetails.isIs_paid(),
                classDetails.getClass_description()) == 1;
    }

    // for mapRow, be careful about is_paid . It can be null.

}
