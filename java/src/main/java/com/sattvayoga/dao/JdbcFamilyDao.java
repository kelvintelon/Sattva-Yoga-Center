package com.sattvayoga.dao;

import com.sattvayoga.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcFamilyDao implements FamilyDao{
    private final JdbcTemplate jdbcTemplate;

    public JdbcFamilyDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Family> getAllFamilies() throws SQLException {
        List<Family> allFamilies = new ArrayList<>();
        String sql = "SELECT family_id, family_name from families;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while(result.next()){
            Family family = mapRowToFamily(result);
            family.setQuick_details("("+family.getFamily_id()+") " + family.getFamily_name());
            allFamilies.add(family);
        }
        return allFamilies;
    }

    public void addClientToFamily(int client_id, int family_id){
        String sql = "Delete from client_family WHERE client_id = ?;";
        jdbcTemplate.update(sql,client_id);
        sql = "INSERT INTO client_family (client_id, family_id) VALUES (?,?);";
        jdbcTemplate.update(sql,client_id,family_id);
    }

    @Override
    public int createNewFamily(int client_id, String family_name) {
        String sql = "INSERT into families (family_name) VALUES (?) RETURNING family_id;";
        int newFamilyId = jdbcTemplate.queryForObject(sql,Integer.class, family_name);
        return newFamilyId;
    }

    @Override
    public void updateFamilyName(Family newFamilyName) {
        String sql = "UPDATE families SET family_name = ? WHERE family_id =?;";
        jdbcTemplate.update(sql,newFamilyName.getFamily_name(),newFamilyName.getFamily_id());
    }

    @Override
    public void deleteFamily(Family familyToDelete) {
        String sql = "DELETE FROM client_family WHERE family_id = ?";
        jdbcTemplate.update(sql, familyToDelete.getFamily_id());
        sql = "DELETE FROM families WHERE family_id = ?";
        jdbcTemplate.update(sql, familyToDelete.getFamily_id());
    }

    public Family mapRowToFamily(SqlRowSet rs){
        Family family = new Family();
        family.setFamily_id(rs.getInt("family_id"));
        family.setFamily_name(rs.getString("family_name"));
        return family;
    }
}
