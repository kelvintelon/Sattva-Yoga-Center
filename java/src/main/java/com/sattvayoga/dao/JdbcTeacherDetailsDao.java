package com.sattvayoga.dao;

import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.TeacherDetails;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTeacherDetailsDao implements TeacherDetailsDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTeacherDetailsDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate;}


    @Override
    public List<TeacherDetails> getTeacherList() {
        List<TeacherDetails> allTeachers = new ArrayList<>();
        String sql = "SELECT teacher_id, first_name, last_name, is_teacher_active from teacher_details;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while(result.next()){
            allTeachers.add(mapRowToTeacher(result));
        }
        return allTeachers;
    }

    @Override
    public boolean createTeacher(TeacherDetails teacher) {
        String sql = "INSERT INTO teacher_details (last_name, first_name, is_teacher_active) VALUES (?, ?, ?)";

        return jdbcTemplate.update(sql, teacher.getLast_name(), teacher.getFirst_name(),
                teacher.isIs_teacher_active()) == 1;
    }

    @Override
    public boolean updateTeacher(TeacherDetails teacherDetails){
        String sql = "UPDATE teacher_details SET first_name = ? , " +
                "last_name = ? , "+
                "is_teacher_active = ? "+
                "WHERE teacher_id = ?";
        return jdbcTemplate.update(sql, teacherDetails.getFirst_name(),teacherDetails.getLast_name(),
                teacherDetails.isIs_teacher_active(), teacherDetails.getTeacher_id())==1;
    }

    @Override
    public boolean deleteTeacher(int teacherId) {
        String sql = "DELETE from teacher_details WHERE teacher_id = ?;";
        return jdbcTemplate.update(sql, teacherId)==1;
    }

    private TeacherDetails mapRowToTeacher(SqlRowSet rs){
        TeacherDetails teacherDetails = new TeacherDetails();
        teacherDetails.setTeacher_id(rs.getInt("teacher_id"));
        teacherDetails.setFirst_name(rs.getString("first_name"));
        teacherDetails.setLast_name(rs.getString("last_name"));
        teacherDetails.setIs_teacher_active(rs.getBoolean("is_teacher_active"));
        return teacherDetails;
    }


}
