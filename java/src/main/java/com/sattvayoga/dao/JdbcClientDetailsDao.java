package com.sattvayoga.dao;

import ch.qos.logback.core.net.server.Client;
import com.sattvayoga.model.ClassDetails;
import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.ClientNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlRowSetResultSetExtractor;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleBiFunction;

@Component
public class JdbcClientDetailsDao implements ClientDetailsDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcClientDetailsDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate;}

    @Override
    public ClientDetails createClient(ClientDetails client) {
        String sql = "INSERT INTO client_details (last_name, first_name, is_client_active, " +
                "is_new_client, street_address, city, state_abbreviation, zip_code, " +
                "phone_number, is_on_email_list, email, has_record_of_liability, " +
                "date_of_entry, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?) RETURNING client_id";
        int clientId = jdbcTemplate.queryForObject(sql, Integer.class, client.getLast_name(), client.getFirst_name(),
                client.isIs_client_active(), client.isIs_new_client(),client.getStreet_address(), client.getCity(),
                client.getState_abbreviation(), client.getZip_code(), client.getPhone_number(), client.isIs_on_email_list(),
                client.getEmail(), client.isHas_record_of_liability(),
                client.getDate_of_entry(), client.getUser_id());
        client.setClient_id(clientId);
        return client;
    }

    @Override
    public ClientDetails createNewClient(ClientDetails client) {
        String sql = "INSERT INTO client_details (last_name, first_name, is_client_active, " +
                "is_new_client, user_id) VALUES (?, ?, ?, ?, ?) RETURNING client_id";
        int clientId = jdbcTemplate.queryForObject(sql, Integer.class, client.getLast_name(), client.getFirst_name(),
                client.isIs_client_active(), client.isIs_new_client(), client.getUser_id());
        client.setClient_id(clientId);
        return client;
    }

    @Override
    public ClientDetails findClientByUserId(int userId) {
        String sql = "SELECT * FROM client_details WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        ClientDetails clientDetails = new ClientDetails();
        if (results.next()) {
            clientDetails = mapRowToClient(results);
        }

        return clientDetails;
    }

    @Override
    public ClientDetails findClientByClientId(int clientId) {
        String sql = "SELECT * FROM client_details WHERE client_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, clientId);
        ClientDetails clientDetails = new ClientDetails();
        if (results.next()) {
            clientDetails = mapRowToClient(results);
        }

        return clientDetails;
    }


    @Override
    public boolean updateClientDetails(ClientDetails clientDetails){
        String sql = "UPDATE client_details SET last_name = ? , " +
                "first_name = ? , " +
                "street_address = ? , " +
                "state_abbreviation = ? , " +
                "zip_code = ? , " +
                "email = ? , " +
                "phone_number = ? , " +
                "is_on_email_list = ? , " +
                "has_record_of_liability = ? , " +
                "is_client_active = ?  , " +
                "is_new_client = ? " +
                "WHERE user_id = ?";
        return jdbcTemplate.update(sql, clientDetails.getLast_name(), clientDetails.getFirst_name(),
                clientDetails.getStreet_address(), clientDetails.getState_abbreviation(),
                clientDetails.getZip_code(), clientDetails.getEmail(), clientDetails.getPhone_number(),
                clientDetails.isIs_on_email_list(), clientDetails.isHas_record_of_liability(),
                clientDetails.isIs_client_active(), clientDetails.isIs_new_client(),
                clientDetails.getUser_id()) == 1;
    }

    @Override
    public boolean deleteClient(int clientId) {
        String sql = "BEGIN TRANSACTION;\n" +
                "\n" +
                "DELETE FROM client_class \n" +
                "WHERE client_class.client_id = ?;\n" +
                "\n" +
                "DELETE FROM client_details\n" +
                "WHERE client_id = ?;\n" +
                "\n" +
                "COMMIT TRANSACTION;";
        return jdbcTemplate.update(sql, clientId, clientId)==1;
    }


    @Override
    public List<ClientDetails> getAllClients() {
        List<ClientDetails> allClients = new ArrayList<>();

        String sql = "SELECT * FROM client_details ORDER BY client_id;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while(result.next()) {
            ClientDetails clientDetails = mapRowToClient(result);

            clientDetails.setFull_address(clientDetails.getStreet_address() + " "
                    + clientDetails.getCity() + " " + clientDetails.getState_abbreviation() + " " + clientDetails.getZip_code());

            clientDetails.setQuick_details("("+clientDetails.getClient_id()+")" + " " + clientDetails.getFirst_name() + " " + clientDetails.getLast_name());

            String familyName = getFamilyNameByClientId(clientDetails.getClient_id());
            clientDetails.setFamily_name(familyName);
            allClients.add(clientDetails);
        }
        return allClients;
    }

    public String getFamilyNameByClientId(int clientId){
        String sql = "SELECT family_name from families \n" +
                "JOIN client_family ON families.family_id = client_family.family_id \n" +
                "WHERE client_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, clientId);
        if(result.next()){
            return result.getString("family_name");
        }
        return "";
    }

    @Override
    public boolean isEmailDuplicate(int clientId, String email) {
        List<ClientDetails> allClientsWithSameEmail = new ArrayList<>();
        String sql = "SELECT * FROM client_details WHERE email = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, email);
        while(result.next()) {
            ClientDetails clientDetails = mapRowToClient(result);
           if (clientDetails.getClient_id() != clientId) {
               allClientsWithSameEmail.add(clientDetails);
            }
        }
        return allClientsWithSameEmail.size() > 0;
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
        clientDetails.setPhone_number(rs.getString("phone_number"));
        clientDetails.setIs_on_email_list(rs.getBoolean("is_on_email_list"));
        clientDetails.setEmail(rs.getString("email"));
        clientDetails.setHas_record_of_liability(rs.getBoolean("has_record_of_liability"));
        clientDetails.setDate_of_entry(rs.getTimestamp("date_of_entry"));
        clientDetails.setUser_id(rs.getInt("user_id"));
        return clientDetails;
    }


}
