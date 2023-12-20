package com.sattvayoga.dao;

import java.sql.PreparedStatement;
import java.util.*;

import com.sattvayoga.model.CustomException;
import com.sattvayoga.model.EmailNotFoundException;
import com.sattvayoga.model.UserNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.sattvayoga.model.YogaUser;

@Component
public class JdbcUserDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findIdByUsername(String username) {
        if (username == null) throw new IllegalArgumentException("Username cannot be null");

        int userId;
        try {
            userId = jdbcTemplate.queryForObject("select user_id from users where username = ?", int.class, username);
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("User " + username + " was not found.");
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to find user ID by username");
        }

        return userId;
    }

	@Override
	public YogaUser getUserById(int userId) {
		String sql = "SELECT * FROM users WHERE user_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
		if (results.next()) {
			return mapRowToUser(results);
		} else {
			throw new UserNotFoundException();
		}
	}

    @Override
    public void updateUserToActivated(int userId) {
        String sql = "UPDATE users SET activated = true WHERE user_id = ?";
        try {
            jdbcTemplate.update(sql, userId);
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to update user as activated.");
        }
    }

    @Override
    public void updateUsernameAndPassword(String username, String password, String usernameToUpdate) {
        String sql = "UPDATE users SET username = ? , password_hash = ? WHERE username = ?;";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        try {
            jdbcTemplate.update(sql,username,password_hash,usernameToUpdate);
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to update username and password.");
        }
    }

    @Override
    public void updatePassword(String password, String usernameToUpdate) {
        String sql = "UPDATE users SET password_hash = ? WHERE username = ?;";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        try {
            jdbcTemplate.update(sql,password_hash,usernameToUpdate);
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to update password.");
        }
    }

    @Override
    public boolean isPasswordEmpty(String username) {
        String sql = "SELECT LENGTH(password_hash) FROM users WHERE username = ?;";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, username);
            if (result.next()) {
                return result.getInt("length") == 0;
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to confirm if password is empty.");
        }
        return false;
    }


    @Override
    public int create(String username, String password, String role) {
        String insertUserSql = "insert into users (username,password_hash,role,activated) " +
                "values (?,?,?,?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        String ssRole = role.toUpperCase().startsWith("ROLE_") ? role.toUpperCase() : "ROLE_" + role.toUpperCase();

        //try/catch + exception for the following
        try {
            return jdbcTemplate.queryForObject(insertUserSql, Integer.class, username, password_hash, ssRole, false);
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to create user.");
        }

    }


    @Override
    public List<YogaUser> findAll() {
        List<YogaUser> users = new ArrayList<>();
        String sql = "select * from users";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                YogaUser user = mapRowToUser(results);
                users.add(user);
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to find all users.");
        }

        return users;
    }

    @Override
    public YogaUser findByUsername(String username) {
        if (username == null) throw new IllegalArgumentException("Username cannot be null");

        for (YogaUser user : this.findAll()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public YogaUser findByEmail(String email) {
        if (email == null) throw new IllegalArgumentException("Email cannot be null");

        String sql = "select * from users JOIN client_details ON client_details.user_id = users.user_id WHERE email = ?";

        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql,email);
            if (results.next()) {
                YogaUser user = mapRowToUser(results);
                return user;
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to find user by email.");
        }

        throw new EmailNotFoundException();

    }



    private YogaUser mapRowToUser(SqlRowSet rs) {
        YogaUser user = new YogaUser();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setAuthorities(Objects.requireNonNull(rs.getString("role")));
        user.setActivated(true);
        return user;
    }
}
