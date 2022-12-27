package com.sattvayoga.dao;

import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.ClientDetailsDTO;
import com.sattvayoga.model.TeacherDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import com.sattvayoga.model.ClassDetails;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcClassDetailsDao implements ClassDetailsDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcClassDetailsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean createClass(ClassDetails classDetails) {

        String sql = "INSERT INTO class_details (teacher_id, class_datetime, class_duration, is_paid, " +
                "class_description) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, classDetails.getTeacher_id(), classDetails.getClass_datetime(),
                classDetails.getClass_duration(), classDetails.isIs_paid(),
                classDetails.getClass_description()) == 1;
    }

    @Override
    public boolean registerForClass(int client_id, int class_id) {
        String sql = "INSERT INTO client_class (client_id, class_id) " +
                "VALUES (?, ?)";
        return jdbcTemplate.update(sql, client_id, class_id) == 1;
    }

    @Override
    public List<ClassDetails> getAllClasses() {
        List<ClassDetails> allClasses = new ArrayList<>();
        String sql = "SELECT class_id, teacher_id, class_datetime, class_duration, is_paid, class_description " +
                "FROM class_details; ";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while (result.next()) {
            ClassDetails classDetails = mapRowToClass(result);

            // set teacher name for class calling helper method
            TeacherDetails teacherDetails = getTeacherDetailsByTeacherId(classDetails.getTeacher_id());
            classDetails.setTeacher_name(teacherDetails.getFirst_name() + " " + teacherDetails.getLast_name());

            // set a list of clients for each class calling helper method
            classDetails.setClient_list(getClientDetailsByClassId(classDetails.getClass_id()));

            allClasses.add(classDetails);
        }
        return allClasses;
    }

    @Override
    public boolean updateClass(ClassDetails classDetails) {
        String sql = "UPDATE class_details SET teacher_id = ? , " +
                "class_datetime = ? , " +
                "class_duration = ? , " +
                "is_paid = ? , " +
                "class_description = ? " +
                "WHERE class_id = ?";
        return jdbcTemplate.update(sql, classDetails.getTeacher_id(), classDetails.getClass_datetime(),
                classDetails.getClass_duration(), classDetails.isIs_paid(), classDetails.getClass_description(), classDetails.getClass_id()) == 1;
    }

    @Override
    public boolean deleteClass(int classId) {
        String sql = "BEGIN TRANSACTION;\n" +
                "\n" +
                "DELETE FROM client_class \n" +
                "WHERE client_class.class_id = ?;\n" +
                "\n" +
                "DELETE FROM class_details\n" +
                "WHERE class_id = ?;\n" +
                "\n" +
                "COMMIT TRANSACTION;";
        return jdbcTemplate.update(sql, classId, classId)==1;
    }

//    @Override
//    public boolean deleteTeacher(int teacherId) {
//        String sql = "DELETE from teacher_details WHERE teacher_id = ?;";
//        return jdbcTemplate.update(sql, teacherId)==1;
//    }

    // helper method to find a list of clients per class
    @Override
    public List<ClientDetailsDTO> getClientDetailsByClassId(int ClassId) {
        List<ClientDetailsDTO> client_list = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM client_details " +
                "JOIN client_class ON client_details.client_id = client_class.client_id " +
                "WHERE class_id = ?";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, ClassId);
        while (result.next()) {
            ClientDetailsDTO clientDetails = new ClientDetailsDTO(result.getInt("client_id"),result.getString("first_name"),result.getString("last_name"));

            client_list.add(clientDetails);
        }
        return client_list;
    }

    // helper method to find teacher details
    public TeacherDetails getTeacherDetailsByTeacherId(int TeacherId) {
        TeacherDetails teacherDetails = null;
        String sql = "SELECT teacher_id, last_name, first_name, is_teacher_active FROM teacher_details WHERE teacher_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, TeacherId);
        if (result.next()) {
            teacherDetails = mapRowToTeacher(result);
        }
        return teacherDetails;
    }

    private TeacherDetails mapRowToTeacher(SqlRowSet rs) {
        TeacherDetails teacherDetails = new TeacherDetails();
        teacherDetails.setTeacher_id(rs.getInt("teacher_id"));
        teacherDetails.setFirst_name(rs.getString("first_name"));
        teacherDetails.setLast_name(rs.getString("last_name"));
        teacherDetails.setIs_teacher_active(rs.getBoolean("is_teacher_active"));
        return teacherDetails;
    }


    private ClassDetails mapRowToClass(SqlRowSet rs) {
        ClassDetails classDetails = new ClassDetails();
        classDetails.setClass_id(rs.getInt("class_id"));
        classDetails.setTeacher_id(rs.getInt("teacher_id"));
        classDetails.setClass_datetime(rs.getTimestamp("class_datetime"));
        classDetails.setClass_duration(rs.getInt("class_duration"));
        if (rs.getBoolean("is_paid") == false || rs.getBoolean("is_paid") == true) {
            classDetails.setIs_paid(rs.getBoolean("is_paid"));
        }
        classDetails.setClass_description(rs.getString("class_description"));
        return classDetails;
    }

}
