package com.sattvayoga.dao;

import com.sattvayoga.model.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

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

        String sql = "INSERT INTO class_details (teacher_id, class_duration, is_paid, " + "class_description, is_repeating, date_range, start_time) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING class_id";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, classDetails.getTeacher_id(), classDetails.getClass_duration(), classDetails.isIs_paid(), classDetails.getClass_description(), classDetails.isIs_repeating(), classDetails.getDate_range(), classDetails.getStart_time());
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to create class.");
        }
    }

    @Override
    public void registerForClass(int client_id, int class_id) {
        String sql = "INSERT INTO client_class (client_id, class_id) " + "VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, client_id, class_id);
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to register into class.");
        }

        String sql2 = "UPDATE client_details SET is_new_client = FALSE WHERE client_id = ?";

        jdbcTemplate.update(sql2, client_id);
    }

    @Override
    public List<ClassDetails> getAllClasses() throws SQLException {
        List<ClassDetails> allClasses = new ArrayList<>();
        String sql = "SELECT class_id, teacher_id, class_duration, is_paid, class_description, " + "is_repeating, date_range, start_time " + "FROM class_details; ";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
            while (result.next()) {
                ClassDetails classDetails = mapRowToClass(result);

                if (classDetails.getTeacher_id() > 0) {
                    TeacherDetails teacherDetails = getTeacherDetailsByTeacherId(classDetails.getTeacher_id());
                    classDetails.setTeacher_name(teacherDetails.getFirst_name() + " " + teacherDetails.getLast_name());
                }

                // set a list of clients for each class calling helper method
                //            classDetails.setClient_list(getClientDetailsByClassId(classDetails.getClass_id()));

                allClasses.add(classDetails);
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to get all classes.");
        }
        return allClasses;
    }

    @Override
    public ClassDetails getClassByClassId(int classId) {
        String sql = "SELECT * FROM class_details WHERE class_id = ?;";
        ClassDetails classDetails = new ClassDetails();
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, classId);
            if (result.next()) {
                classDetails = mapRowToClass(result);
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to retrieve class by ID");
        }
        return classDetails;
    }

    @Override
    public List<ClassDetails> getAllClientClasses(int userId) throws SQLException {
        List<ClassDetails> allClientClass = new ArrayList<>();
        String sql = "SELECT class_details.class_id, teacher_id, class_duration, is_paid, class_description, " + "is_repeating, date_range, start_time " + "FROM class_details " + "JOIN client_class on client_class.class_id = class_details.class_id " + "JOIN client_details on client_details.client_id = client_class.client_id " + "WHERE client_details.user_id = ?";
        try {
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
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to retrieve all client classes.");
        }
        return allClientClass;
    }

    @Override
    public boolean updateClass(ClassDetails classDetails) {
        String sql = "UPDATE class_details SET teacher_id = ? , " + "class_duration = ? , " + "is_paid = ? , " + "class_description = ? , " + "is_repeating = ? , " + "date_range = ? , " + "start_time = ? " + "WHERE class_id = ?";
        try {
            return jdbcTemplate.update(sql, classDetails.getTeacher_id(), classDetails.getClass_duration(), classDetails.isIs_paid(), classDetails.getClass_description(), classDetails.isIs_repeating(), classDetails.getDate_range(), classDetails.getStart_time(), classDetails.getClass_id()) == 1;
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to update class details.");
        }
    }

    @Override
    public boolean deleteClassForClient(int classId, int clientId) {
        String sql = "DELETE FROM client_class " + "WHERE client_class.class_id = ? AND client_class.client_id = ? ";
        try {
            return jdbcTemplate.update(sql, classId, clientId) == 1;
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to delete class for client.");
        }
    }

    @Override
    public boolean deleteClass(int classId) {
        String sql = "BEGIN TRANSACTION;\n" + "\n" + "DELETE FROM client_class \n" + "WHERE client_class.class_id = ?;\n" + "\n" + "DELETE FROM class_details\n" + "WHERE class_id = ?;\n" + "\n" + "COMMIT TRANSACTION;";
        try {
            return jdbcTemplate.update(sql, classId, classId) == 1;
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("");
        }
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
        String sql = "SELECT * " + "FROM client_details " + "JOIN client_class ON client_details.client_id = client_class.client_id " + "WHERE class_id = ?";

        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, ClassId);
            while (result.next()) {
                ClientDetailsDTO clientDetails = new ClientDetailsDTO(result.getInt("client_id"), result.getString("first_name"), result.getString("last_name"));

                client_list.add(clientDetails);
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to get client details by ID.");
        }
        return client_list;
    }

    // helper method to find teacher details
    public TeacherDetails getTeacherDetailsByTeacherId(int TeacherId) {
        TeacherDetails teacherDetails = null;
        String sql = "SELECT teacher_id, last_name, first_name, is_teacher_active FROM teacher_details WHERE teacher_id = ?";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, TeacherId);
            if (result.next()) {
                teacherDetails = mapRowToTeacher(result);
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to help get teacher details by ID.");
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

        if (newObject instanceof Array) {
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
