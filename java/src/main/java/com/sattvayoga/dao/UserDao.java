package com.sattvayoga.dao;

import com.sattvayoga.model.YogaUser;

import java.util.List;

public interface UserDao {

    List<YogaUser> findAll();

    YogaUser getUserById(int userId);

    YogaUser findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password, String role);

    void updateUserToActivated(int userId);
}
