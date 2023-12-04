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

    @Override
    public List<Family> getAllFamilies() throws SQLException {
        List<Family> allFamilies = new ArrayList<>();
        String sql = "SELECT family_id, family_name from families;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while(result.next()){
            Family family = mapRowToFamily(result);
            family.setQuick_details("("+family.getFamily_id()+") " + family.getFamily_name());
            family.setListOfFamilyMembers(getFamilyMemberListByFamilyId(family.getFamily_id()));
            allFamilies.add(family);
        }
        return allFamilies;
    }

    @Override
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

    @Override
    public Family getFamilyDetailsByFamilyId(int familyId) {
        Family family = new Family();

        String sql = "SELECT * FROM families WHERE family_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql,familyId);
        if (result.next()) {
            family = mapRowToFamily(result);
        }

        sql = "SELECT * FROM client_details JOIN client_family ON client_details.client_id = client_family.client_id WHERE family_id = ?";
        SqlRowSet result2 = jdbcTemplate.queryForRowSet(sql,familyId);

        while (result2.next()) {
            ClientDetails familyMember = mapRowToClient(result2);
            family.getListOfFamilyMembers().add(familyMember);
        }

        return family;
    }

    public List<ClientDetails> getFamilyMemberListByFamilyId(int familyId) {
        List<ClientDetails> familyMemberList = new ArrayList<>();

        String sql = "SELECT * FROM client_details JOIN client_family ON client_details.client_id = client_family.client_id WHERE family_id = ?";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, familyId);

        while(result.next()) {
            ClientDetails familyMember = mapRowToClient(result);
            familyMemberList.add(familyMember);
        }

        return  familyMemberList;
    }

    public Family mapRowToFamily(SqlRowSet rs){
        Family family = new Family();
        family.setFamily_id(rs.getInt("family_id"));
        family.setFamily_name(rs.getString("family_name"));
        return family;
    }

    private ClientDetails mapRowToClient(SqlRowSet rs) {
        ClientDetails clientDetails = new ClientDetails();
        clientDetails.setClient_id(rs.getInt("client_id"));
        clientDetails.setLast_name(rs.getString("last_name"));
        clientDetails.setFirst_name(rs.getString("first_name"));
        clientDetails.setIs_client_active(rs.getBoolean("is_client_active"));
        clientDetails.setIs_new_client(rs.getBoolean("is_new_client"));
        clientDetails.setStreet_address(rs.getString("street_address"));
        clientDetails.setCity(rs.getString("city"));
        clientDetails.setState_abbreviation(rs.getString("state_abbreviation"));
        clientDetails.setZip_code(rs.getString("zip_code"));
        clientDetails.setCountry(rs.getString("country"));
        clientDetails.setPhone_number(rs.getString("phone_number"));
        clientDetails.setIs_on_email_list(rs.getBoolean("is_on_email_list"));
        clientDetails.setEmail(rs.getString("email"));
        clientDetails.setHas_record_of_liability(rs.getBoolean("has_record_of_liability"));
        clientDetails.setDate_of_entry(rs.getTimestamp("date_of_entry"));
        clientDetails.setIs_allowed_video(rs.getBoolean("is_allowed_video"));
        clientDetails.setUser_id(rs.getInt("user_id"));
        if (rs.getString("customer_id") != null) {
            clientDetails.setCustomer_id(rs.getString("customer_id"));
        }
        return clientDetails;
    }
}
