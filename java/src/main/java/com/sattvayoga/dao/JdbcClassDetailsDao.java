package com.sattvayoga.dao;

import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.ClientDetailsDTO;
import com.sattvayoga.model.TeacherDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import com.sattvayoga.model.ClassDetails;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class JdbcClassDetailsDao implements ClassDetailsDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcClassDetailsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int createClass(ClassDetails classDetails) {

        String sql = "INSERT INTO class_details (teacher_id, class_duration, is_paid, " +
                "class_description, is_repeating, date_range, start_time) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING class_id";
        return jdbcTemplate.queryForObject(sql, Integer.class, classDetails.getTeacher_id(),
                classDetails.getClass_duration(), classDetails.isIs_paid(),
                classDetails.getClass_description(), classDetails.isIs_repeating(), classDetails.getDate_range(), classDetails.getStart_time());
    }

    @Override
    public void registerForClass(int client_id, int class_id) {
        String sql = "INSERT INTO client_class (client_id, class_id) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sql, client_id, class_id);

        String sql2 = "UPDATE client_details SET is_new_client = FALSE WHERE client_id = ?";

        jdbcTemplate.update(sql2, client_id);
    }

    @Override
    public List<ClassDetails> getAllClasses() throws SQLException {
        List<ClassDetails> allClasses = new ArrayList<>();
        String sql = "SELECT class_id, teacher_id, class_duration, is_paid, class_description, " +
                "is_repeating, date_range, start_time " +
                "FROM class_details; ";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while (result.next()) {
            ClassDetails classDetails = mapRowToClass(result);

            if (classDetails.getTeacher_id() > 0) {
                TeacherDetails teacherDetails = getTeacherDetailsByTeacherId(classDetails.getTeacher_id());
                classDetails.setTeacher_name(teacherDetails.getFirst_name() + " " + teacherDetails.getLast_name());
            }

            // set a list of clients for each class calling helper method
            classDetails.setClient_list(getClientDetailsByClassId(classDetails.getClass_id()));

            allClasses.add(classDetails);
        }
        return allClasses;
    }

    @Override
    public ClassDetails getClassByClassId(int classId) {
        String sql = "SELECT * FROM class_details WHERE class_id = ?;";
        ClassDetails classDetails = new ClassDetails();
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql,classId);
        if (result.next()) {
            try {
                classDetails = mapRowToClass(result);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return classDetails;
    }

    @Override
    public List<ClassDetails> getAllClientClasses(int userId) throws SQLException {
        List<ClassDetails> allClientClass = new ArrayList<>();
        String sql = "SELECT class_details.class_id, teacher_id, class_duration, is_paid, class_description, " +
                "is_repeating, date_range, start_time " +
                "FROM class_details " +
                "JOIN client_class on client_class.class_id = class_details.class_id " +
                "JOIN client_details on client_details.client_id = client_class.client_id " +
                "WHERE client_details.user_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId);
        while (result.next()) {
            ClassDetails classDetails = mapRowToClass(result);

            // set teacher name for class calling helper method
            TeacherDetails teacherDetails = getTeacherDetailsByTeacherId(classDetails.getTeacher_id());
            classDetails.setTeacher_name(teacherDetails.getFirst_name() + " " + teacherDetails.getLast_name());

            // set a list of clients for each class calling helper method
            classDetails.setClient_list(getClientDetailsByClassId(classDetails.getClass_id()));

            allClientClass.add(classDetails);
        }
        return allClientClass;
    }

    @Override
    public boolean updateClass(ClassDetails classDetails) {
        String sql = "UPDATE class_details SET teacher_id = ? , " +
                "class_duration = ? , " +
                "is_paid = ? , " +
                "class_description = ? , " +
                "is_repeating = ? , " +
                "date_range = ? , " +
                "start_time = ? " +
                "WHERE class_id = ?";
        return jdbcTemplate.update(sql, classDetails.getTeacher_id(), classDetails.getClass_duration(),
                classDetails.isIs_paid(), classDetails.getClass_description(),
                classDetails.isIs_repeating(), classDetails.getDate_range(), classDetails.getStart_time(),
                classDetails.getClass_id()) == 1;
    }

    @Override
    public boolean deleteClassForClient(int classId, int clientId) {
        String sql = "DELETE FROM client_class " +
                "WHERE client_class.class_id = ? AND client_class.client_id = ? ";
        return jdbcTemplate.update(sql, classId, clientId)==1;
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


    private ClassDetails mapRowToClass(SqlRowSet rs) throws SQLException {
        ClassDetails classDetails = new ClassDetails();
        classDetails.setClass_id(rs.getInt("class_id"));
        classDetails.setTeacher_id(rs.getInt("teacher_id"));
        classDetails.setClass_duration(rs.getInt("class_duration"));
        classDetails.setIs_paid(rs.getBoolean("is_paid"));
        classDetails.setClass_description(rs.getString("class_description"));
        classDetails.setIs_repeating(rs.getBoolean("is_repeating"));
        classDetails.setStart_time(rs.getString("start_time"));

        Object newObject = rs.getObject("date_range");

        if(newObject instanceof Array) {
            Array tempArray = (Array) newObject;
            Object[] tempObjectArray = (Object[]) tempArray.getArray();
            String[] dateRange = new String[tempObjectArray.length];
            for (int i = 0; i < tempObjectArray.length; i++) {
                dateRange[i] = tempObjectArray[i].toString();
            }
            classDetails.setDate_range(dateRange);
        }

        return classDetails;
    }

}
