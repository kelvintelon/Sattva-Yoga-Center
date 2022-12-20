package com.sattvayoga.dao;

import com.sattvayoga.model.ClientDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcClientDetailsDao implements ClientDetailsDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcClientDetailsDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate;}

    @Override
    public boolean createClient(ClientDetails client) {
        String sql = "INSERT INTO client_info (last_name, first_name, is_client_active, " +
                "is_constant_contact, street_address, city, state_abbreviation, zip_code, " +
                "phone_number, is_on_email_list, email, has_record_of_liability, " +
                "date_of_entry, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql, client.getLast_name(), client.getFirst_name(),
                client.isIs_client_active(), client.isIs_constant_contact(),
                client.getStreet_address(), client.getCity(), client.getState_abbreviation(),
                client.getZip_code(), client.getPhone_number(), client.isIs_on_email_list(),
                client.getEmail(), client.isHas_record_of_liability(),
                client.getDate_of_entry(), client.getUser_id()) == 1;
    }



}
