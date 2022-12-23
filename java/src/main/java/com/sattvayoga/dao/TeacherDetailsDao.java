package com.sattvayoga.dao;

import com.sattvayoga.model.TeacherDetails;

public interface TeacherDetailsDao {

    boolean createTeacher(TeacherDetails teacher);

    boolean updateTeacher(TeacherDetails teacherDetails);

    boolean deleteTeacher(int teacherId);
}
