package com.sattvayoga.dao;

import com.sattvayoga.model.TeacherDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TeacherDetailsDao {

    List<TeacherDetails> getTeacherList();

    boolean createTeacher(TeacherDetails teacher);

    boolean updateTeacher(TeacherDetails teacherDetails);

    boolean deleteTeacher(int teacherId);

    void uploadTeacherCsv(MultipartFile multipartFile);

}
