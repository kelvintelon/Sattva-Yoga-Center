package com.sattvayoga.dao;

import com.sattvayoga.model.YogaUser;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserDao {

    List<YogaUser> findAll();

    YogaUser getUserById(int userId);

    YogaUser findByUsername(String username);

    int findIdByUsername(String username);

    int create(String username, String password, String role);

    void updateUserToActivated(int userId);

    YogaUser findByEmail(String email);

    void updateUsernameAndPassword(String username, String password, String usernameToUpdate);

    void updatePassword(String password, String usernameToUpdate);

    boolean isPasswordEmpty(String username);
}
