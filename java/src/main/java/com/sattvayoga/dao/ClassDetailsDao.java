package com.sattvayoga.dao;

import com.sattvayoga.model.ClassDetails;
import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.TeacherDetails;

import java.util.List;

public interface ClassDetailsDao {

    boolean createClass(ClassDetails classDetails);

    boolean registerForClass(int client_id, int class_id);

    List<ClassDetails> getAllClasses();

    TeacherDetails getTeacherDetailsByTeacherId(int TeacherId);

    List<ClientDetails> getClientDetailsByClassId(int Classid);
}
