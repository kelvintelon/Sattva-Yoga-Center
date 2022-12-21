package com.sattvayoga.dao;

import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.ClientNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlRowSetResultSetExtractor;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcClientDetailsDao implements ClientDetailsDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcClientDetailsDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate;}

    @Override
    public ClientDetails createClient(ClientDetails client) {
        String sql = "INSERT INTO client_info (last_name, first_name, is_client_active, " +
                "is_constant_contact, street_address, city, state_abbreviation, zip_code, " +
                "phone_number, is_on_email_list, email, has_record_of_liability, " +
                "date_of_entry, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING client_id";
        int clientId = jdbcTemplate.queryForObject(sql, Integer.class, client.getLast_name(), client.getFirst_name(),
                client.isIs_client_active(), client.isIs_constant_contact(),
                client.getStreet_address(), client.getCity(), client.getState_abbreviation(),
                client.getZip_code(), client.getPhone_number(), client.isIs_on_email_list(),
                client.getEmail(), client.isHas_record_of_liability(),
                client.getDate_of_entry(), client.getUser_id());
        client.setClient_id(clientId);
        return client;
    }

    @Override
    public ClientDetails findClientByUserId(int userId) {
        String sql = "SELECT * FROM client_info WHERE client_info = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        if (results.next()) {
            return mapRowToClient(results);
        } else {
            throw new ClientNotFoundException();
        }
    }

    private ClientDetails mapRowToClient(SqlRowSet rs) {
        ClientDetails clientDetails = new ClientDetails();
        clientDetails.setClient_id(rs.getInt("client_id"));
        clientDetails.setLast_name(rs.getString("last_name"));
        clientDetails.setFirst_name(rs.getString("first_name"));
        clientDetails.setIs_client_active(rs.getBoolean("is_client_active"));
        clientDetails.setIs_constant_contact(rs.getBoolean("is_constant_contact"));
        clientDetails.setStreet_address(rs.getString("street_address"));
        clientDetails.setCity(rs.getString("city"));
        clientDetails.setState_abbreviation(rs.getString("state_abbreviation"));
        clientDetails.setZip_code(rs.getString("zip_code"));
        clientDetails.setPhone_number(rs.getString("phone_number"));
        clientDetails.setIs_on_email_list(rs.getBoolean("is_on_email_list"));
        clientDetails.setEmail(rs.getString("email"));
        clientDetails.setHas_record_of_liability(rs.getBoolean("has_record_of_liability"));
        clientDetails.setDate_of_entry(rs.getTimestamp("date_of_entry"));
        clientDetails.setUser_id(rs.getInt("user_id"));
        return clientDetails;
    }


}
