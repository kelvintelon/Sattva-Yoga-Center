package com.sattvayoga.dao;

import com.sattvayoga.model.ClassDetails;
import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.ClientDetailsDTO;
import com.sattvayoga.model.TeacherDetails;

import java.sql.SQLException;
import java.util.List;

public interface ClassDetailsDao {

    boolean createClass(ClassDetails classDetails);

    void registerForClass(int client_id, int class_id);

    List<ClassDetails> getAllClasses() throws SQLException;

    List<ClassDetails> getAllClientClasses(int userId) throws SQLException;

    TeacherDetails getTeacherDetailsByTeacherId(int TeacherId);

    List<ClientDetailsDTO> getClientDetailsByClassId(int Classid);

    boolean updateClass(ClassDetails classDetails);

    boolean deleteClassForClient(int classId, int userId);

    boolean deleteClass(int classId);

    ClassDetails getClassByClassId(int classId);
}
