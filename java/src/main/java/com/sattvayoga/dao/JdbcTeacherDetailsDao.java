package com.sattvayoga.dao;

import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.CustomException;
import com.sattvayoga.model.TeacherDetails;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Component
public class JdbcTeacherDetailsDao implements TeacherDetailsDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTeacherDetailsDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate;}


    @Override
    public List<TeacherDetails> getTeacherList() {
        List<TeacherDetails> allTeachers = new ArrayList<>();
        String sql = "SELECT teacher_id, first_name, last_name, is_teacher_active from teacher_details;";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
            while(result.next()){
                allTeachers.add(mapRowToTeacher(result));
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to retrieve list of teachers.");
        }
        return allTeachers;
    }

    @Override
    public boolean createTeacher(TeacherDetails teacher) {
        String sql = "INSERT INTO teacher_details (last_name, first_name, is_teacher_active) VALUES (?, ?, ?)";

        try {
            return jdbcTemplate.update(sql, teacher.getLast_name(), teacher.getFirst_name(),
                    teacher.isIs_teacher_active()) == 1;
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to create a teacher.");
        }
    }

    @Override
    public boolean updateTeacher(TeacherDetails teacherDetails){
        String sql = "UPDATE teacher_details SET first_name = ? , " +
                "last_name = ? , "+
                "is_teacher_active = ? "+
                "WHERE teacher_id = ?";
        try {
            return jdbcTemplate.update(sql, teacherDetails.getFirst_name(),teacherDetails.getLast_name(),
                    teacherDetails.isIs_teacher_active(), teacherDetails.getTeacher_id())==1;
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to update a teacher.");
        }
    }

    @Override
    public boolean deleteTeacher(int teacherId) {
        String sql = "DELETE from teacher_details WHERE teacher_id = ?;";
        try {
            return jdbcTemplate.update(sql, teacherId)==1;
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to delete a teacher.");
        }
    }

    @Override
    public void uploadTeacherCsv(MultipartFile multipartFile) {
        int count = 0;

        List<String> listOfStringsFromBufferedReader = new ArrayList<>();

        Set<TeacherDetails> teacherDetailsSetFromFile = new HashSet<>();

        HashMap<String,Integer> mapColumns = new HashMap<>();

        try (BufferedReader fileReader = new BufferedReader(new
                InputStreamReader(multipartFile.getInputStream(), "UTF-8"))) {

            String line;
            while ((line = fileReader.readLine()) != null) {

                if (count > 0) {

                    listOfStringsFromBufferedReader.add(line);

                } else {
                    String[] firstLine =  line.split(",");
                    mapColumns = populateColumnsForMap(firstLine);
                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        readLinesFromListAndPopulateSet(listOfStringsFromBufferedReader, teacherDetailsSetFromFile, mapColumns);

        checkForDuplicateTeachers(teacherDetailsSetFromFile);

        if (!teacherDetailsSetFromFile.isEmpty()) {
            for (TeacherDetails teacher :
                    teacherDetailsSetFromFile) {
                createTeacher(teacher);
            }
        }

    }

    public static HashMap<String, Integer> populateColumnsForMap(String[] array) {
        HashMap<String, Integer> columnMap = new HashMap<>();

        for (int i = 0; i < array.length; i++) {
            String currentString = array[i];
            if (currentString.contains("TeacherID")) {
                columnMap.put("TeacherID", i);
            } else if (currentString.contains("Teacher Name")) {
                columnMap.put("Teacher Name", i);
            }

        }

        return columnMap;
    }

    private void checkForDuplicateTeachers(Set<TeacherDetails> teacherDetailsSetFromFile) {
        List<TeacherDetails> existingTeachers = getTeacherList();

        if (!existingTeachers.isEmpty()) {
            List<TeacherDetails> listOfTeachersNoDuplicates = new ArrayList<>(teacherDetailsSetFromFile);
            for (int i = 0; i < listOfTeachersNoDuplicates.size(); i++) {
                TeacherDetails teacherDetails1 = listOfTeachersNoDuplicates.get(i);
                for (int j = 0; j < existingTeachers.size(); j++) {
                    TeacherDetails teacherDetails2 = existingTeachers.get(j);
                    if (teacherDetails1.getFirst_name().equalsIgnoreCase(teacherDetails2.getFirst_name())
                            && (teacherDetails1.getLast_name() != null && teacherDetails2.getLast_name() != null)
                            && teacherDetails1.getLast_name().equalsIgnoreCase(teacherDetails2.getLast_name())) {
                        teacherDetailsSetFromFile.remove(teacherDetails1);
                    }
                    if (teacherDetails1.getFirst_name().equalsIgnoreCase(teacherDetails2.getFirst_name())
                            && (teacherDetails1.getLast_name() == null && teacherDetails2.getLast_name() == null)
                           ) {
                        teacherDetailsSetFromFile.remove(teacherDetails1);
                    }
                }
            }
        }
    }

    public void readLinesFromListAndPopulateSet(List<String> listFromFile, Set<TeacherDetails> setToPopulate, HashMap<String, Integer> columnMap) {
        for (int i = 0; i < listFromFile.size(); i++) {
            TeacherDetails teacherDetails = new TeacherDetails();

            int teacherId = 0;

            String thisLine = listFromFile.get(i);
            String[] splitLine = thisLine.split(",");

            teacherId = Integer.valueOf(splitLine[columnMap.get("TeacherID")]);

            teacherDetails.setTeacher_id(teacherId);

            if (splitLine[columnMap.get("Teacher Name")].contains("Self")) {
                teacherDetails.setFirst_name(splitLine[columnMap.get("Teacher Name")]);
            } else {
                String[] nameArray = splitLine[columnMap.get("Teacher Name")].split(" ");

                if (nameArray.length==1) {
                    teacherDetails.setFirst_name(nameArray[0]);
                } else {
                    teacherDetails.setFirst_name(nameArray[0]);
                    teacherDetails.setLast_name(nameArray[1]);
                }
            }



            if ((teacherDetails.getFirst_name().equalsIgnoreCase("Margaret")
                && teacherDetails.getLast_name().equalsIgnoreCase("Green")) ||
                    (teacherDetails.getFirst_name().equalsIgnoreCase("Chuck")
                            && teacherDetails.getLast_name().equalsIgnoreCase("Mallur"))) {
                teacherDetails.setIs_teacher_active(true);
            } else {
                teacherDetails.setIs_teacher_active(false);
            }

            setToPopulate.add(teacherDetails);
        }
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
