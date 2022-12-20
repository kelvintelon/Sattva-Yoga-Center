package com.sattvayoga.dao;

import com.sattvayoga.model.TeacherDetails;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcTeacherDetailsDao implements TeacherDetailsDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTeacherDetailsDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate;}
    @Override
    public boolean createTeacher(TeacherDetails teacher) {
        String sql = "INSERT INTO teacher_details (last_name, first_name, is_teacher_active, " +
                "user_id) VALUES (?, ?, ?, ?)";

        return jdbcTemplate.update(sql, teacher.getLast_name(), teacher.getFirst_name(),
                teacher.isIs_teacher_active(), teacher.getUser_id()) == 1;
    }
}
