package com.sattvayoga.dao;

import com.sattvayoga.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.*;

@Component
public class JdbcClientDetailsDao implements ClientDetailsDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcClientDetailsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    UserDao userDao;

    @Override
    public PaginatedListOfClients getAllPaginatedClients(int page, int pageSize, String search, String sortBy, boolean sortDesc) {

        int offset = 0;
        String sortDirection = (sortDesc ? "DESC" : "ASC");

        String offsetString = "";
        if (page == 1) {
            offset = pageSize * (page);
            offsetString = " LIMIT ?";
        } else {
            offset = pageSize * (page-1);
            offsetString = " OFFSET ? LIMIT " + pageSize;
        }

        List<ClientDetails> allClients = new ArrayList<>();
        String searchString = "";
        if (!search.isEmpty()) {
            search = "%" + search + "%";
            searchString = " WHERE first_name ILIKE ? OR last_name ILIKE ? OR email ILIKE ?";

            String sql = "SELECT * FROM client_details" + searchString + " ORDER BY " + sortBy + " " + sortDirection + offsetString;

            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, search, search, search, offset);

            while (result.next()) {
                ClientDetails clientDetails = mapRowToClient(result);

                clientDetails.setFull_address(clientDetails.getStreet_address() + " "
                        + clientDetails.getCity() + " " + clientDetails.getState_abbreviation() + " " + clientDetails.getZip_code());

                clientDetails.setQuick_details("(" + clientDetails.getClient_id() + ")" + " " + clientDetails.getFirst_name() + " " + clientDetails.getLast_name());

                String familyName = getFamilyNameByClientId(clientDetails.getClient_id());
                clientDetails.setFamily_name(familyName);
                allClients.add(clientDetails);
            }

            PaginatedListOfClients paginatedListOfClients = new PaginatedListOfClients();
            paginatedListOfClients.setListOfClients(allClients);

            String countSql = "Select COUNT(*) from client_details" + searchString;

            int count = jdbcTemplate.queryForObject(countSql, Integer.class,  search, search, search);

            paginatedListOfClients.setTotalRows(count);
            return paginatedListOfClients;

        } else {

            String sql = "SELECT * FROM client_details ORDER BY " + sortBy + " " + sortDirection + offsetString;

            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, offset);

            while (result.next()) {
                ClientDetails clientDetails = mapRowToClient(result);

                clientDetails.setFull_address(clientDetails.getStreet_address() + " "
                        + clientDetails.getCity() + " " + clientDetails.getState_abbreviation() + " " + clientDetails.getZip_code());

                clientDetails.setQuick_details("(" + clientDetails.getClient_id() + ")" + " " + clientDetails.getFirst_name() + " " + clientDetails.getLast_name());

                String familyName = getFamilyNameByClientId(clientDetails.getClient_id());
                clientDetails.setFamily_name(familyName);
                allClients.add(clientDetails);
            }

            PaginatedListOfClients paginatedListOfClients = new PaginatedListOfClients();
            paginatedListOfClients.setListOfClients(allClients);

            String countSql = "Select COUNT(*) from client_details";

            int count = jdbcTemplate.queryForObject(countSql, Integer.class);

            paginatedListOfClients.setTotalRows(count);
            return paginatedListOfClients;
        }

    }

    @Override
    public PaginatedListOfClients getAllPaginatedDuplicateClients(int page, int pageSize, String search) {

        int offset = 0;

        String offsetString = "";
        if (page == 1) {
            offset = pageSize * (page);
            offsetString = " LIMIT ?";
        } else {
            offset = pageSize * (page-1);
            offsetString = " OFFSET ? LIMIT " + pageSize;
        }

        List<ClientDetails> allClients = new ArrayList<>();
        String searchString = "";


        if (!search.isEmpty()) {
            search = "%" + search + "%";
            searchString = " WHERE first_name ILIKE ? OR last_name ILIKE ? OR email ILIKE ?";

            String sql = "SELECT * FROM client_details" + searchString + " ORDER BY client_id" + offsetString;

            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, search, search, search, offset);

            while (result.next()) {
                ClientDetails clientDetails = mapRowToClient(result);

                clientDetails.setFull_address(clientDetails.getStreet_address() + " "
                        + clientDetails.getCity() + " " + clientDetails.getState_abbreviation() + " " + clientDetails.getZip_code());

                clientDetails.setQuick_details("(" + clientDetails.getClient_id() + ")" + " " + clientDetails.getFirst_name() + " " + clientDetails.getLast_name());

                String familyName = getFamilyNameByClientId(clientDetails.getClient_id());
                clientDetails.setFamily_name(familyName);
                allClients.add(clientDetails);
            }

            PaginatedListOfClients paginatedListOfClients = new PaginatedListOfClients();
            paginatedListOfClients.setListOfClients(allClients);

            String countSql = "Select COUNT(*) from client_details" + searchString;

            int count = jdbcTemplate.queryForObject(countSql, Integer.class,  search, search, search);

            paginatedListOfClients.setTotalRows(count);
            return paginatedListOfClients;

        } else {
            PaginatedListOfClients paginatedListOfClients = new PaginatedListOfClients();

            String sql = "SELECT a1.* " +
                    "FROM client_details a1 " +
                    "WHERE exists (SELECT * " +
                    "FROM client_details a2 " +
                    "WHERE a2.last_name = a1.last_name AND SUBSTRING(a2.first_name from 0 for 3) = SUBSTRING(a1.first_name from 0 for 3) " +
                    "AND a2.client_id <> a1.client_id) " + offsetString;

            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, offset);
            while (result.next()) {
                ClientDetails clientDetails = mapRowToClient(result);

                clientDetails.setFull_address(clientDetails.getStreet_address() + " "
                        + clientDetails.getCity() + " " + clientDetails.getState_abbreviation() + " " + clientDetails.getZip_code());

                clientDetails.setQuick_details("(" + clientDetails.getClient_id() + ")" + " " + clientDetails.getFirst_name() + " " + clientDetails.getLast_name());

                String familyName = getFamilyNameByClientId(clientDetails.getClient_id());
                clientDetails.setFamily_name(familyName);
                allClients.add(clientDetails);
            }

            paginatedListOfClients.setListOfClients(allClients);

            String countSql = "SELECT COUNT(a1.*) " +
                    "FROM client_details a1 " +
                    "WHERE exists (SELECT * " +
                    "FROM client_details a2 " +
                    "WHERE a2.last_name = a1.last_name AND SUBSTRING(a2.first_name from 0 for 3) = SUBSTRING(a1.first_name from 0 for 3) " +
                    "AND a2.client_id <> a1.client_id);";

            int count = jdbcTemplate.queryForObject(countSql, Integer.class);

            paginatedListOfClients.setTotalRows(count);

            return paginatedListOfClients;
        }

    }

    public void uploadClient(ClientDetails client) {
        String sql = "INSERT INTO client_details (client_id, last_name, first_name, is_client_active, email, is_on_email_list, " +
                "is_new_client, user_id, date_of_entry, is_allowed_video, street_address, city, state_abbreviation, zip_code, phone_number, has_record_of_liability) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, client.getClient_id(), client.getLast_name(), client.getFirst_name(),
                client.isIs_client_active(), client.getEmail(), client.isIs_on_email_list(), client.isIs_new_client(),
                client.getUser_id(), client.getDate_of_entry(), client.isIs_allowed_video(), client.getStreet_address(),
                client.getCity(), client.getState_abbreviation(), client.getZip_code(), client.getPhone_number(), client.isHas_record_of_liability());

        // look up the email here
//        boolean foundEmail = false;
//        if (client.getEmail() != null && !(client.getEmail().equalsIgnoreCase(""))) {
//            foundEmail = isEmailDuplicate(client.getClient_id(),client.getEmail());
//        }
//
//        if (foundEmail || (client.getEmail() != null && ( client.getEmail().equalsIgnoreCase("info@sattva-yoga-center.com") || client.getEmail().equalsIgnoreCase("sattva.yoga.center.michigan@gmail.com")))) {
//            client.setEmail("");
//            client.setIs_on_email_list(false);
//
//            updateClientDetails(client);
//        }

    }

    private void uploadClientNoClientId(ClientDetails client) {
        String sql = "INSERT INTO client_details (last_name, first_name, is_client_active, email, is_on_email_list, " +
                "is_new_client, user_id, date_of_entry, is_allowed_video, street_address, city, state_abbreviation, zip_code, phone_number, has_record_of_liability) VALUES  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING client_id";
        int intNewClientId = jdbcTemplate.queryForObject(sql, Integer.class, client.getLast_name(), client.getFirst_name(),
                client.isIs_client_active(), client.getEmail(), client.isIs_on_email_list(), client.isIs_new_client(),
                client.getUser_id(), client.getDate_of_entry(), client.isIs_allowed_video(), client.getStreet_address(),
                client.getCity(), client.getState_abbreviation(), client.getZip_code(), client.getPhone_number(), client.isHas_record_of_liability());

//        client.setClient_id(intNewClientId);
//        // look up the email here
//        boolean foundEmail = false;
//        if (client.getEmail() != null && !(client.getEmail().equalsIgnoreCase(""))) {
//            foundEmail = isEmailDuplicate(client.getClient_id(), client.getEmail());
//        }
//
//        if (foundEmail || (client.getEmail() != null && ( client.getEmail().equalsIgnoreCase("info@sattva-yoga-center.com") || client.getEmail().equalsIgnoreCase("sattva.yoga.center.michigan@gmail.com")))) {
//            client.setEmail("");
//            client.setIs_on_email_list(false);
//
//            updateClientDetails(client);
//
//
//        }
    }

    @Override
    public List<ClientDetails> getAllClients() {
        List<ClientDetails> allClients = new ArrayList<>();

        String sql = "SELECT * FROM client_details ORDER BY client_id;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while (result.next()) {
            ClientDetails clientDetails = mapRowToClient(result);

            clientDetails.setFull_address(clientDetails.getStreet_address() + " "
                    + clientDetails.getCity() + " " + clientDetails.getState_abbreviation() + " " + clientDetails.getZip_code());

            clientDetails.setQuick_details("(" + clientDetails.getClient_id() + ")" + " " + clientDetails.getFirst_name() + " " + clientDetails.getLast_name());

            String familyName = getFamilyNameByClientId(clientDetails.getClient_id());
            clientDetails.setFamily_name(familyName);
            allClients.add(clientDetails);
        }
        return allClients;
    }

    @Override
    public List<ClientDetails> getAllDuplicateClients() {
        List<ClientDetails> allClients = new ArrayList<>();

        String sql = "SELECT a1.* " +
                "FROM client_details a1 " +
                "WHERE exists (SELECT * " +
                "FROM client_details a2 " +
                "WHERE a2.last_name = a1.last_name AND SUBSTRING(a2.first_name from 0 for 3) = SUBSTRING(a1.first_name from 0 for 3) " +
                "AND a2.client_id <> a1.client_id);";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while (result.next()) {
            ClientDetails clientDetails = mapRowToClient(result);

            clientDetails.setFull_address(clientDetails.getStreet_address() + " "
                    + clientDetails.getCity() + " " + clientDetails.getState_abbreviation() + " " + clientDetails.getZip_code());

            clientDetails.setQuick_details("(" + clientDetails.getClient_id() + ")" + " " + clientDetails.getFirst_name() + " " + clientDetails.getLast_name());

            String familyName = getFamilyNameByClientId(clientDetails.getClient_id());
            clientDetails.setFamily_name(familyName);
            allClients.add(clientDetails);
        }
        return allClients;
    }

    @Override
    public ClientDetails createClient(ClientDetails client) {
        String sql = "INSERT INTO client_details (last_name, first_name, is_client_active, " +
                "is_new_client, street_address, city, state_abbreviation, zip_code, " +
                "phone_number, is_on_email_list, email, has_record_of_liability, " +
                "date_of_entry, is_allowed_video, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING client_id";
        int clientId = jdbcTemplate.queryForObject(sql, Integer.class, client.getLast_name(), client.getFirst_name(),
                client.isIs_client_active(), client.isIs_new_client(), client.getStreet_address(), client.getCity(),
                client.getState_abbreviation(), client.getZip_code(), client.getPhone_number(), client.isIs_on_email_list(),
                client.getEmail(), client.isHas_record_of_liability(),
                client.getDate_of_entry(), client.isIs_allowed_video(), client.getUser_id());
        client.setClient_id(clientId);
        return client;
    }

    @Override
    public ClientDetails createNewClient(ClientDetails client) {
        String sql = "INSERT INTO client_details (last_name, first_name, is_client_active, email, is_on_email_list, " +
                "is_new_client, user_id, date_of_entry, is_allowed_video) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING client_id";
        int clientId = jdbcTemplate.queryForObject(sql, Integer.class, client.getLast_name(), client.getFirst_name(),
                client.isIs_client_active(), client.getEmail(), client.isIs_on_email_list(), client.isIs_new_client(), client.getUser_id(), client.getDate_of_entry(), client.isIs_allowed_video());
        client.setClient_id(clientId);
        // look up the email here
        boolean foundEmail = false;
        if (client.getEmail() != null && !(client.getEmail().equalsIgnoreCase(""))) {
            foundEmail = isEmailDuplicate(client.getClient_id(),client.getEmail());
        }

        if (foundEmail || (client.getEmail() != null && ( client.getEmail().equalsIgnoreCase("info@sattva-yoga-center.com") || client.getEmail().equalsIgnoreCase("sattva.yoga.center.michigan@gmail.com")))) {
            client.setEmail("");
            client.setIs_on_email_list(false);

            updateClientDetails(client);

            throw new EmailAlreadyExistsException();
        }
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
    public ClientDetails findClientByCustomerId(String customerID) {
        String sql = "SELECT * FROM client_details WHERE customer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, customerID);
        ClientDetails clientDetails = new ClientDetails();
        if (results.next()) {
            clientDetails = mapRowToClient(results);
        }

        return clientDetails;
    }

    @Override
    public void removeDuplicateClients(int clientIdToKeep, int clientIdToRemove) {
        String sql = "UPDATE client_event SET client_id = ? WHERE client_id = ? " +
                "AND event_id NOT IN (SELECT event_id FROM client_event WHERE client_id = ? OR client_id = ? GROUP BY event_id having count(event_id) > 1);";
        jdbcTemplate.update(sql, clientIdToKeep, clientIdToRemove, clientIdToKeep, clientIdToRemove);
        sql = "DELETE FROM client_event WHERE client_id = ?";
        jdbcTemplate.update(sql, clientIdToRemove);
        sql = "UPDATE client_family SET client_id = ? WHERE client_id = ? " +
                "AND family_id NOT IN (SELECT family_id FROM client_family WHERE client_id = ? OR client_id = ? GROUP BY family_id having count(family_id) > 1);";
        jdbcTemplate.update(sql, clientIdToKeep, clientIdToRemove, clientIdToKeep, clientIdToRemove);
        sql = "DELETE FROM client_family WHERE client_id = ?";
        jdbcTemplate.update(sql, clientIdToRemove);
        sql = "UPDATE package_purchase SET client_id = ? WHERE client_id = ?;";
        jdbcTemplate.update(sql, clientIdToKeep, clientIdToRemove);
        sql = "UPDATE client_class SET client_id = ? WHERE client_id = ?;";
        jdbcTemplate.update(sql, clientIdToKeep, clientIdToRemove);
        sql = "DELETE FROM client_details WHERE client_id = ?;";
        jdbcTemplate.update(sql, clientIdToRemove);
    }

    @Override
    public boolean updateClientDetails(ClientDetails clientDetails) {
        String sql = "UPDATE client_details SET last_name = ? , " +
                "first_name = ? , " +
                "street_address = ? , " +
                "city = ? , " +
                "state_abbreviation = ? , " +
                "zip_code = ? , " +
                "email = ? , " +
                "phone_number = ? , " +
                "is_on_email_list = ? , " +
                "has_record_of_liability = ? , " +
                "is_client_active = ?  , " +
                "is_allowed_video = ?  , " +
                "is_new_client = ? " +
                "WHERE user_id = ?";
        return jdbcTemplate.update(sql, clientDetails.getLast_name(), clientDetails.getFirst_name(),
                clientDetails.getStreet_address(), clientDetails.getCity(), clientDetails.getState_abbreviation(),
                clientDetails.getZip_code(), clientDetails.getEmail(), clientDetails.getPhone_number(),
                clientDetails.isIs_on_email_list(), clientDetails.isHas_record_of_liability(),
                clientDetails.isIs_client_active(), clientDetails.isIs_allowed_video(), clientDetails.isIs_new_client(),
                clientDetails.getUser_id()) == 1;
    }

    @Override
    public boolean updateClientCustomerId(int clientId, String customerId) {
        String sql = "UPDATE client_details SET customer_id = ? WHERE client_id = ?";
        return jdbcTemplate.update(sql, customerId, clientId) == 1;
    }

    @Override
    public boolean saveNewClientEmail(int clientId, String newEmail) {
        String sql = "UPDATE client_details SET email = ? WHERE client_id = ?";
        return jdbcTemplate.update(sql, newEmail, clientId)==1;
    }

    @Override
    public boolean deleteClient(int clientId) {
        String sql = "BEGIN TRANSACTION;\n" +
                "\n" +
                "DELETE FROM client_event \n" +
                "WHERE client_event.client_id = ?;\n" +
                "\n" +
                "DELETE FROM client_family \n" +
                "WHERE client_family.client_id = ?;\n" +
                "\n" +
                "DELETE FROM client_class \n" +
                "WHERE client_class.client_id = ?;\n" +
                "\n" +
                "DELETE FROM client_details\n" +
                "WHERE client_id = ?;\n" +
                "\n" +
                "COMMIT TRANSACTION;";
        return jdbcTemplate.update(sql, clientId, clientId, clientId, clientId) == 1;
    }

    public String getFamilyNameByClientId(int clientId) {
        String sql = "SELECT family_name from families \n" +
                "JOIN client_family ON families.family_id = client_family.family_id \n" +
                "WHERE client_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, clientId);
        if (result.next()) {
            return result.getString("family_name");
        }
        return "";
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
        clientDetails.setIs_allowed_video(rs.getBoolean("is_allowed_video"));
        clientDetails.setUser_id(rs.getInt("user_id"));
        if (rs.getString("customer_id") != null) {
        clientDetails.setCustomer_id(rs.getString("customer_id"));
        }
        return clientDetails;
    }

    @Override
    public void uploadClientCsv(MultipartFile multipartFile) {

        int count = 0;

        long startTimeForEntireUpload = System.nanoTime();

        List<String> listOfStringsFromBufferedReader = new ArrayList<>();

        HashSet<ClientDetails> setOfClientDetailsFromFile = new HashSet<>();

        long startTimeForReading = System.nanoTime();

        try (BufferedReader fileReader = new BufferedReader(new
                InputStreamReader(multipartFile.getInputStream(), "UTF-8"))) {

            String line;
            while ((line = fileReader.readLine()) != null) {

                if (count > 0) {

                    listOfStringsFromBufferedReader.add(line);

                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        readLinesFromListAndPopulateSets(listOfStringsFromBufferedReader, setOfClientDetailsFromFile);

        long endTimeForReading = System.nanoTime();
        long totalTimeForReading = endTimeForReading - startTimeForReading;
        System.out.println("Total Time for reading file : " + getReadableTime(totalTimeForReading) + " / " + totalTimeForReading  + " ns");

        long startTimeForDoesClientExists = System.nanoTime();

        // We turn it into a list in order to modify as we iterate
        List<ClientDetails> clientDetailsList = new ArrayList<>(setOfClientDetailsFromFile);
        Set<ClientDetails> setOfClientObjectsWithNoDuplicates = new HashSet<>(clientDetailsList);

        // Check if client exists with a populated table
        checkIfClientIdIsDuplicate(clientDetailsList, setOfClientObjectsWithNoDuplicates);

        long endTimeForDoesClientExists = System.nanoTime();
        long totalTimeForDoesClientExists = endTimeForDoesClientExists - startTimeForDoesClientExists;
        System.out.println("Total time to check if client ID exists already : "  + getReadableTime(totalTimeForDoesClientExists)+ " / " + totalTimeForDoesClientExists  + " ns");

        long startTimeForCreateCheckClients = System.nanoTime();

        //TODO: First create all users proportionate to the amount we need

        // Pass in our big Set of clients read from the file, this will create a batch of users and set the user ID into each element
        Map<String, ClientDetails> newMapOfClientObjects = getMapOfNewUsers(setOfClientObjectsWithNoDuplicates);

        // Grab a set of emails to compare to
        Set<String> setOfExistingEmails = getSetOfExistingEmails();

        checkIfClientEmailIsDuplicateOrInvalid(newMapOfClientObjects, setOfExistingEmails);

        Set<ClientDetails> newSetOfClientObjectsToSaveId = new HashSet<>(newMapOfClientObjects.values());
        HashSet<ClientDetails> setOfClientDetailsFromFileWithNoId = new HashSet<>();
        List<ClientDetails> listOfClientsToSplit = new ArrayList<>(newSetOfClientObjectsToSaveId);

        //TODO: Hold off on breaking up the big set into two sets until the end
        for (int i = 0; i < listOfClientsToSplit.size(); i++) {
            ClientDetails clientDetails = listOfClientsToSplit.get(i);
            if (clientDetails.getClient_id() == 0) {
                // Removes object from the set that has client ids and put it
                // into the set that will generate new client ids
                newSetOfClientObjectsToSaveId.remove(clientDetails);
                setOfClientDetailsFromFileWithNoId.add(clientDetails);
            }
        }

        long endTimeForCreateCheckClients = System.nanoTime();
        long totalTimeForCreateCheckClients = endTimeForCreateCheckClients - startTimeForCreateCheckClients;
        System.out.println("Total time for creating a batch of users + check existing emails: " + getReadableTime(totalTimeForCreateCheckClients) + " / " + totalTimeForCreateCheckClients  + " ns");

        long startTimeForBatchUpload = System.nanoTime();

        batchCreateClientDetailsSaveId(newSetOfClientObjectsToSaveId);

        long endTimeForBatchUpload = System.nanoTime();
        long totalTimeForBatchUpload = endTimeForBatchUpload - startTimeForBatchUpload;
        System.out.println("Total time for batch upload in : " + getReadableTime(totalTimeForBatchUpload) + " / " + totalTimeForBatchUpload + " ns");

        long startTimeForBatchUploadNoClientId = System.nanoTime();

        batchCreateClientDetailsWithNeedsNewId(setOfClientDetailsFromFileWithNoId);

        long endTimeForBatchUploadNoClientId = System.nanoTime();
        long totalTimeForBatchUploadNoClientId = endTimeForBatchUploadNoClientId - startTimeForBatchUploadNoClientId;
        System.out.println("Total time for batch upload with no Client Id in : " + getReadableTime(totalTimeForBatchUploadNoClientId) + " / " + totalTimeForBatchUploadNoClientId + " ns");

        long endTimeForEntireUpload = System.nanoTime();
        long totalTimeForEntireUpload = endTimeForEntireUpload - startTimeForEntireUpload;
        System.out.println("Total time for entire upload in : " + getReadableTime(totalTimeForEntireUpload) + " / " + totalTimeForEntireUpload + " ns");
    }

    private void checkIfClientEmailIsDuplicateOrInvalid(Map<String, ClientDetails> newMapOfClientObjects, Set<String> setOfExistingEmails) {
        for (String username : newMapOfClientObjects.keySet()) {
            ClientDetails clientDetails = newMapOfClientObjects.get(username);

            // If the email exists
            if (setOfExistingEmails.contains(clientDetails.getEmail())) {
                // Set Email
                clientDetails.setEmail("");
            // Or if the email belongs to the admin
            } else if ((clientDetails.getEmail() != null
                    && ( clientDetails.getEmail().equalsIgnoreCase("info@sattva-yoga-center.com")
                    || clientDetails.getEmail().equalsIgnoreCase("sattva.yoga.center.michigan@gmail.com")))) {
                // Set Email
                clientDetails.setEmail("");
            }
        }
    }

    private Map<String, ClientDetails> getMapOfNewUsers(Set<ClientDetails> setOfClients) {

        Map<String, ClientDetails> usernameToClientDetailsMap = new HashMap<>();

        Set<YogaUser> setOfGeneratedYogaUsers = new HashSet<>();
        Set<String> setOfUsernames = new HashSet<>();

        // Set of usernames to populate, map to populate
        generateUsernamesAndPasswordThenPopulateSets(setOfClients, setOfUsernames, usernameToClientDetailsMap, setOfGeneratedYogaUsers);

        // send batch of user objects
        batchCreateUsers(setOfGeneratedYogaUsers);

        // retrieve a Hashmap where <Key username, Value user id>
        Map<String, Integer> usernameMapToId = retrieveUsernameAndIdMap(setOfUsernames);

        setUserIdIntoClientDetailsInMap(usernameToClientDetailsMap, usernameMapToId);

        // then return the set of clients after the user ID has been set into each object.
        return usernameToClientDetailsMap;
    }

    private void setUserIdIntoClientDetailsInMap(Map<String, ClientDetails> usernameToClientDetailsMap, Map<String, Integer> usernameMapToId) {
        for (String username : usernameMapToId.keySet()) {
            int user_id = usernameMapToId.get(username);

            if (usernameToClientDetailsMap.containsKey(username)) {
                // Extract Client Details
                ClientDetails clientDetails = usernameToClientDetailsMap.get(username);
                // Set User ID
                clientDetails.setUser_id(user_id);
                // Replace with modified object
                usernameToClientDetailsMap.put(username,clientDetails);
            }
        }
    }

    private void generateUsernamesAndPasswordThenPopulateSets(Set<ClientDetails> setOfClients, Set<String> setOfUsernames, Map<String,ClientDetails> mapToPopulate, Set<YogaUser> setOfGeneratedYogaUsers) {
        for (ClientDetails clientDetails : setOfClients) {
            YogaUser yogaUser = new YogaUser();

            String generatedPassword;
            String generatedUsername;
            int leftLimit = 48; // numeral '0'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();

            // create password
            generatedPassword = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();


            // create username
            generatedUsername = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            generatedUsername = "user" + generatedUsername;

            yogaUser.setUsername(generatedUsername);
            yogaUser.setPassword(generatedPassword);

            setOfGeneratedYogaUsers.add(yogaUser);
            setOfUsernames.add(generatedUsername);
            mapToPopulate.put(generatedUsername,clientDetails);
        }
    }


    public void batchCreateClientDetailsSaveId(final Collection<ClientDetails> clients) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO client_details (client_id, last_name, " +
                        "first_name, is_client_active, email, is_on_email_list, " +
                        "is_new_client, user_id, date_of_entry, is_allowed_video, " +
                        "street_address, city, state_abbreviation, zip_code, " +
                        "phone_number, has_record_of_liability) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                clients,
                100,
                (PreparedStatement ps, ClientDetails clientDetails) -> {
                    ps.setInt(1, clientDetails.getClient_id());
                    ps.setString(2, clientDetails.getLast_name());
                    ps.setString(3, clientDetails.getFirst_name());
                    ps.setBoolean(4, clientDetails.isIs_client_active());
                    ps.setString(5, clientDetails.getEmail());
                    ps.setBoolean(6, clientDetails.isIs_on_email_list());
                    ps.setBoolean(7, clientDetails.isIs_new_client());
                    ps.setInt(8, clientDetails.getUser_id());
                    ps.setTimestamp(9, clientDetails.getDate_of_entry());
                    ps.setBoolean(10, clientDetails.isIs_allowed_video());
                    ps.setString(11, clientDetails.getStreet_address());
                    ps.setString(12, clientDetails.getCity());
                    ps.setString(13, clientDetails.getState_abbreviation());
                    ps.setString(14, clientDetails.getZip_code());
                    ps.setString(15, clientDetails.getPhone_number());
                    ps.setBoolean(16, clientDetails.isHas_record_of_liability());

                });

    }

    public void batchCreateClientDetailsWithNeedsNewId(final Collection<ClientDetails> clients) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO client_details (last_name, " +
                        "first_name, is_client_active, email, is_on_email_list, " +
                        "is_new_client, user_id, date_of_entry, is_allowed_video, " +
                        "street_address, city, state_abbreviation, zip_code, " +
                        "phone_number, has_record_of_liability) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                clients,
                100,
                (PreparedStatement ps, ClientDetails clientDetails) -> {
                    ps.setString(1, clientDetails.getLast_name());
                    ps.setString(2, clientDetails.getFirst_name());
                    ps.setBoolean(3, clientDetails.isIs_client_active());
                    ps.setString(4, clientDetails.getEmail());
                    ps.setBoolean(5, clientDetails.isIs_on_email_list());
                    ps.setBoolean(6, clientDetails.isIs_new_client());
                    ps.setInt(7, clientDetails.getUser_id());
                    ps.setTimestamp(8, clientDetails.getDate_of_entry());
                    ps.setBoolean(9, clientDetails.isIs_allowed_video());
                    ps.setString(10, clientDetails.getStreet_address());
                    ps.setString(11, clientDetails.getCity());
                    ps.setString(12, clientDetails.getState_abbreviation());
                    ps.setString(13, clientDetails.getZip_code());
                    ps.setString(14, clientDetails.getPhone_number());
                    ps.setBoolean(15, clientDetails.isHas_record_of_liability());

                });

    }

    public void batchCreateUsers(final Collection<YogaUser> users) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO users (username,password_hash,role,activated) " +
                        "VALUES (?,?,?,?)",
                users,
                100,
                (PreparedStatement ps, YogaUser yogaUser) -> {
//                    ps.getConnection().setAutoCommit(false);
                    ps.setString(1, yogaUser.getUsername());
                    ps.setString(2, new BCryptPasswordEncoder().encode(yogaUser.getPassword()));
                    ps.setString(3, "ROLE_USER");
                    ps.setBoolean(4, false);
//                    ps.getConnection().setAutoCommit(true);
                });
    }

    public Map<String, Integer> retrieveUsernameAndIdMap(Set<String> setOfUsernames) {
        Map<String, Integer> mapToReturn = new HashMap<>();
        String allUsernamesConcated = "";

        int count = 0;

        for (String currentString : setOfUsernames ) {
            if (count == 0) {
                allUsernamesConcated = "('" + currentString + "'";
            } else {
                allUsernamesConcated = allUsernamesConcated + ", " + "'" + currentString + "'";
            }
            if (count == setOfUsernames.size()-1) {
                allUsernamesConcated += ");";
            }
            count++;
        }

        String sql = "SELECT username, user_id FROM users WHERE username IN " + allUsernamesConcated;
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql,allUsernamesConcated);
        while (results.next()) {
            int user_id = results.getInt("user_id");
            String username = results.getString("username");
            mapToReturn.put(username,user_id);
        }

        return mapToReturn;
    }

    public Set<String> getSetOfExistingEmails() {
        String sql = "SELECT email from client_details";
        Set<String> setToReturn = new HashSet<>();
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while (result.next()) {
            setToReturn.add(result.getString("email"));
        }
        return setToReturn;
    }

    private void checkIfClientIdIsDuplicate(List<ClientDetails> clientDetailsList, Set<ClientDetails> setOfClientObjectsWithNoDuplicates) {
        if (!isClientTableEmpty()) {
            // Grab one big set of existing client IDs
            Set<Integer> setOfExistingClientIds = getSetOfExistingClientIds();
            for (ClientDetails clientDetails : clientDetailsList) {

                // Check that the client Object's client ID is not zero first.
                if (clientDetails.getClient_id() != 0) {
                    // Check if the client ID is in our setOfClientIds, that means its unique
                    if (setOfExistingClientIds.contains(clientDetails.getClient_id())) {
                        // this removes duplicates
                        setOfClientObjectsWithNoDuplicates.remove(clientDetails);
                        continue;
                    }
                }
            }
        }
    }


    public Set<Integer> getSetOfExistingClientIds() {
        String sql = "SELECT client_id from client_details";
        Set<Integer> setToReturn = new HashSet<>();
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        while (result.next()) {
            setToReturn.add(result.getInt("client_id"));
        }
        return setToReturn;
    }

    @Override
    public boolean isEmailDuplicate(int clientId, String email) {

        String sql = "SELECT client_id, COUNT(email) FROM client_details WHERE email = ? GROUP BY client_id;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, email);
        int count = 0;
        while (result.next()) {
            if (result.getInt("client_id") != clientId) {
                count++;
            }
        }
        return count > 0;
    }

    @Override
    public boolean isClientTableEmpty() {
        int count = 0;
        String sql = "SELECT COUNT(client_id) FROM client_details;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        if (result.next()) {
            count = result.getInt("count");
        }
        return count > 0;
    }

    private void setStateAbbreviationWithMap(ClientDetails clientDetails, String state) {
        Map<String, String> states = new HashMap<String, String>();
        states.put("ALABAMA", "AL");
        states.put("ALASKA", "AK");
        states.put("ALBERTA", "AB");
        states.put("AMERICAN SAMOA", "AS");
        states.put("ARIZONA", "AZ");
        states.put("ARKANSAS", "AR");
        states.put("ARMED FORCES (AE)", "AE");
        states.put("ARMED FORCES AMERICAS", "AA");
        states.put("ARMED FORCES PACIFIC", "AP");
        states.put("BRITISH COLUMBIA", "BC");
        states.put("CALIFORNIA", "CA");
        states.put("COLORADO", "CO");
        states.put("CONNECTICUT", "CT");
        states.put("DELAWARE", "DE");
        states.put("DISTRICT OF COLUMBIA", "DC");
        states.put("FLORIDA", "FL");
        states.put("GEORGIA", "GA");
        states.put("GUAM", "GU");
        states.put("HAWAII", "HI");
        states.put("IDAHO", "ID");
        states.put("ILLINOIS", "IL");
        states.put("INDIANA", "IN");
        states.put("IOWA", "IA");
        states.put("KANSAS", "KS");
        states.put("KENTUCKY", "KY");
        states.put("LOUISIANA", "LA");
        states.put("MAINE", "ME");
        states.put("MANITOBA", "MB");
        states.put("MARYLAND", "MD");
        states.put("MASSACHUSETTS", "MA");
        states.put("MICHIGAN", "MI");
        states.put("MINNESOTA", "MN");
        states.put("MISSISSIPPI", "MS");
        states.put("MISSOURI", "MO");
        states.put("MONTANA", "MT");
        states.put("NEBRASKA", "NE");
        states.put("NEVADA", "NV");
        states.put("NEW BRUNSWICK", "NB");
        states.put("NEW HAMPSHIRE", "NH");
        states.put("NEW JERSEY", "NJ");
        states.put("NEW MEXICO", "NM");
        states.put("NEW YORK", "NY");
        states.put("NEWFOUNDLAND", "NF");
        states.put("NORTH CAROLINA", "NC");
        states.put("NORTH DAKOTA", "ND");
        states.put("NORTHWEST TERRITORIES", "NT");
        states.put("NOVA SCOTIA", "NS");
        states.put("NUNAVUT", "NU");
        states.put("OHIO", "OH");
        states.put("OKLAHOMA", "OK");
        states.put("ONTARIO", "ON");
        states.put("OREGON", "OR");
        states.put("PENNSYLVANIA", "PA");
        states.put("PRINCE EDWARD ISLAND", "PE");
        states.put("PUERTO RICO", "PR");
        states.put("QUEBEC", "QC");
        states.put("RHODE ISLAND", "RI");
        states.put("SASKATCHEWAN", "SK");
        states.put("SOUTH CAROLINA", "SC");
        states.put("SOUTH DAKOTA", "SD");
        states.put("TENNESSEE", "TN");
        states.put("TEXAS", "TX");
        states.put("UTAH", "UT");
        states.put("VERMONT", "VT");
        states.put("VIRGIN ISLANDS", "VI");
        states.put("VIRGINIA", "VA");
        states.put("WASHINGTON", "WA");
        states.put("WEST VIRGINIA", "WV");
        states.put("WISCONSIN", "WI");
        states.put("WYOMING", "WY");
        states.put("YUKON TERRITORY", "YT");

        clientDetails.setState_abbreviation(states.get(state.toUpperCase()));
    }

    private String getReadableTime(Long nanos){

        long tempSec    = nanos/(1000*1000*1000);
        long sec        = tempSec % 60;
        long min        = (tempSec /60) % 60;

        return String.format("%dm %ds", min,sec);

    }

    private void readLinesFromListAndPopulateSets(List<String> listOfStringsFromBufferedReader, HashSet<ClientDetails> setOfClientDetailsFromFile) {
        for(int i = 0; i < listOfStringsFromBufferedReader.size(); i++) {
            ClientDetails clientDetails = new ClientDetails();
            clientDetails.setIs_client_active(true);
            clientDetails.setIs_new_client(false);
            clientDetails.setIs_allowed_video(false);

            String thisLine = listOfStringsFromBufferedReader.get(i);
            String[] splitLine = thisLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            int clientId = 0;
            if (!splitLine[0].isEmpty()) {
                clientId = Integer.valueOf(splitLine[0]);
            }

            clientDetails.setClient_id(clientId);

            String lastName = splitLine[1];
            if (lastName.startsWith("(")) {
                lastName = lastName.substring(1, lastName.length() - 1);
            }

            if (lastName.length() > 1) {
                lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
            }

            clientDetails.setLast_name(lastName);

            String firstName = splitLine[2];

            if (firstName.startsWith("(")) {
                firstName = firstName.substring(1, firstName.length() - 1);
            }

            if (firstName.length() > 1) {
                firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
            }

            clientDetails.setFirst_name(firstName);

            String address = splitLine[3];

            if (address.startsWith("\"") && address.length() > 1) {
                address = address.substring(1, address.length() - 1);
            }

            clientDetails.setStreet_address(address);

            String city = splitLine[4];

            clientDetails.setCity(city);

            String state = splitLine[5];

            setStateAbbreviationWithMap(clientDetails, state);

            String zipCode = splitLine[6];

            clientDetails.setZip_code(zipCode);

            if (splitLine.length >= 9) {
                String phoneNumber = splitLine[8];

                clientDetails.setPhone_number(phoneNumber);
            }


            if (splitLine.length == 10) {
                String email = splitLine[9];

                clientDetails.setEmail(email);

            }

            clientDetails.setIs_on_email_list(false);

            Date date = new Date();
            Timestamp theLatestTimestamp = new Timestamp(date.getTime());
            clientDetails.setDate_of_entry(theLatestTimestamp);

            clientDetails.setHas_record_of_liability(false);

            // Set the clientDetails object into the set
            setOfClientDetailsFromFile.add(clientDetails);
        }
    }



}
