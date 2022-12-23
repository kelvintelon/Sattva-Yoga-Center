package com.sattvayoga.dao;

import com.sattvayoga.model.ClassDetails;

public interface ClassDetailsDao {

    boolean createClass(ClassDetails classDetails);

    boolean registerForClass(int client_id, int class_id);
}
