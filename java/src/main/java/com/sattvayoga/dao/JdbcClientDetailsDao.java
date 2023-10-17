package com.sattvayoga.dao;

import com.sattvayoga.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    @Override
    public void uploadClientCsv(MultipartFile multipartFile) {

        BufferedReader fileReader = null;
        int count = 0;

        long startTimeForEntireUpload = System.nanoTime();

        List<String> listOfStringsFromBufferedReader = new ArrayList<>();

        HashSet<Integer> setOfClientIds = new HashSet<>();
        HashSet<ClientDetails> setOfClientObjects = new HashSet<>();

        long startTimeForReading = System.nanoTime();

        try {
            fileReader = new BufferedReader(new
                    InputStreamReader(multipartFile.getInputStream(), "UTF-8"));
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


        for(int i = 0; i < listOfStringsFromBufferedReader.size(); i++) {
            ClientDetails clientDetails = new ClientDetails();
            clientDetails.setIs_client_active(true);
            clientDetails.setIs_new_client(false);
            clientDetails.setIs_allowed_video(false);

            String thisLine = listOfStringsFromBufferedReader.get(i);
            String[] splitLine = thisLine.split(",");
            int clientId = 0;
            if (!splitLine[0].isEmpty()) {
                clientId = Integer.valueOf(splitLine[0]);

                //TODO: Set client ID into set if we found one
                setOfClientIds.add(clientId);
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

            // TODO: Set the clientDetails object into the set

            setOfClientObjects.add(clientDetails);
        }



        long endTimeForReading = System.nanoTime();
        long totalTimeForReading = endTimeForReading - startTimeForReading;
        System.out.println("Total Time for reading in ns: " + totalTimeForReading);

        long startTimeForDoesClientExists = System.nanoTime();
        // TODO: Check if client exists with a populated tbale
        if (isClientTableEmpty()) {
            Iterator<Integer> itrOfClientIds = setOfClientIds.iterator();
            while (itrOfClientIds.hasNext()) {
                // If the client ID exists, deletes it from the set
                if (doesClientExistByClientId(itrOfClientIds.next())) {
                    itrOfClientIds.remove();
                }
            }
        }
        long endTimeForDoesClientExists = System.nanoTime();
        long totalTimeForDoesClientExists = endTimeForDoesClientExists - startTimeForDoesClientExists;
        System.out.println("Total time for does client exist in ns: " + totalTimeForDoesClientExists);

        // TODO In no order:
        //  1.Create user + Set a User ID.
        //  2.Check Email.
        //  3.Check Client ID against our setOfClientIds.
        //  4.Upload.

        long startTimeForCreateCheckClients = System.nanoTime();
        Iterator<ClientDetails> itrClientObject = setOfClientObjects.iterator();
        HashSet<ClientDetails> setOfClientObjectsWithNoId = new HashSet<>();
        while (itrClientObject.hasNext()) {
            ClientDetails clientDetails = itrClientObject.next();

            // Check that the client Object's client ID is not zero first.
            if (clientDetails.getClient_id() != 0) {
                // Check if the client ID is in our setOfClientIds, that means its unique
                if (!setOfClientIds.contains(clientDetails.getClient_id())) {
                    itrClientObject.remove();
                }
            } else {
                setOfClientObjectsWithNoId.add(clientDetails);
            }

            // Create user and set user ID
            int newUserId = getNewUserId();
            clientDetails.setUser_id(newUserId);

            // look up the email here
            boolean foundEmail = false;
            if (clientDetails.getEmail() != null && !(clientDetails.getEmail().equalsIgnoreCase(""))) {
                foundEmail = isEmailDuplicate(clientDetails.getClient_id(),clientDetails.getEmail());
            }

            if (foundEmail || (clientDetails.getEmail() != null
                    && ( clientDetails.getEmail().equalsIgnoreCase("info@sattva-yoga-center.com")
                    || clientDetails.getEmail().equalsIgnoreCase("sattva.yoga.center.michigan@gmail.com")))) {
                clientDetails.setEmail("");
                clientDetails.setIs_on_email_list(false);
            }

        }
        
        long endTimeForCreateCheckClients = System.nanoTime();
        long totalTimeForCreateCheckClients = endTimeForCreateCheckClients - startTimeForCreateCheckClients;
        System.out.println("Total time for create/check/upload clients in ns: " + totalTimeForCreateCheckClients);

        long startTimeForBatchUpload = System.nanoTime();

        batchUpdate(setOfClientObjects);

        long endTimeForBatchUpload = System.nanoTime();
        long totalTimeForBatchUpload = endTimeForBatchUpload - startTimeForBatchUpload;
        System.out.println("Total time for batch upload in ns: " + totalTimeForBatchUpload);

        long startTimeForBatchUploadNoClientId = System.nanoTime();

        batchUpdateWithNoID(setOfClientObjectsWithNoId);

        long endTimeForBatchUploadNoClientId = System.nanoTime();
        long totalTimeForBatchUploadNoClientId = endTimeForBatchUploadNoClientId - startTimeForBatchUploadNoClientId;
        System.out.println("Total time for batch upload with no Client Id in ns: " + totalTimeForBatchUploadNoClientId);

        long endTimeForEntireUpload = System.nanoTime();
        long totalTimeForEntireUpload = endTimeForEntireUpload - startTimeForEntireUpload;
        System.out.println("Total time for entire upload in ns: " + totalTimeForEntireUpload);
    }

    @Override
    public boolean doesClientExistByClientId(int clientId) {
        String sql = "SELECT COUNT(client_id) FROM client_details WHERE client_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, clientId);
        int count = 0;
        if (result.next()) {
            count = result.getInt("count");
        }
        return count > 0;
    }

    public void batchUpdate(final Collection<ClientDetails> clients) {
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

    public void batchUpdateWithNoID(final Collection<ClientDetails> clients) {
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

    @Override
    public boolean isEmailDuplicate(int clientId, String email) {
        int count = 0;
        String sql = "SELECT COUNT(email) FROM client_details WHERE email = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, email);
        if (result.next()) {
            count = result.getInt("count");
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

    private int getNewUserId() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        // create password
        String generatedPassword = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();


        // create username
        String generatedUsername = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        generatedUsername = "user" + generatedUsername;

        // create user with username and password
        userDao.create(generatedUsername,generatedPassword, "user");

        // retrieve the user ID,
        YogaUser newUser = userDao.findByUsername(generatedUsername);
        int userId = newUser.getId();
        return userId;
    }

    private void setStateAbbreviationWithMap(ClientDetails clientDetails, String state) {
        Map<String, String> states = new HashMap<String, String>();
        states.put("Alabama","AL");
        states.put("Alaska","AK");
        states.put("Alberta","AB");
        states.put("American Samoa","AS");
        states.put("Arizona","AZ");
        states.put("Arkansas","AR");
        states.put("Armed Forces (AE)","AE");
        states.put("Armed Forces Americas","AA");
        states.put("Armed Forces Pacific","AP");
        states.put("British Columbia","BC");
        states.put("California","CA");
        states.put("Colorado","CO");
        states.put("Connecticut","CT");
        states.put("Delaware","DE");
        states.put("District Of Columbia","DC");
        states.put("Florida","FL");
        states.put("Georgia","GA");
        states.put("Guam","GU");
        states.put("Hawaii","HI");
        states.put("Idaho","ID");
        states.put("Illinois","IL");
        states.put("Indiana","IN");
        states.put("Iowa","IA");
        states.put("Kansas","KS");
        states.put("Kentucky","KY");
        states.put("Louisiana","LA");
        states.put("Maine","ME");
        states.put("Manitoba","MB");
        states.put("Maryland","MD");
        states.put("Massachusetts","MA");
        states.put("Michigan","MI");
        states.put("Minnesota","MN");
        states.put("Mississippi","MS");
        states.put("Missouri","MO");
        states.put("Montana","MT");
        states.put("Nebraska","NE");
        states.put("Nevada","NV");
        states.put("New Brunswick","NB");
        states.put("New Hampshire","NH");
        states.put("New Jersey","NJ");
        states.put("New Mexico","NM");
        states.put("New York","NY");
        states.put("Newfoundland","NF");
        states.put("North Carolina","NC");
        states.put("North Dakota","ND");
        states.put("Northwest Territories","NT");
        states.put("Nova Scotia","NS");
        states.put("Nunavut","NU");
        states.put("Ohio","OH");
        states.put("Oklahoma","OK");
        states.put("Ontario","ON");
        states.put("Oregon","OR");
        states.put("Pennsylvania","PA");
        states.put("Prince Edward Island","PE");
        states.put("Puerto Rico","PR");
        states.put("Quebec","QC");
        states.put("Rhode Island","RI");
        states.put("Saskatchewan","SK");
        states.put("South Carolina","SC");
        states.put("South Dakota","SD");
        states.put("Tennessee","TN");
        states.put("Texas","TX");
        states.put("Utah","UT");
        states.put("Vermont","VT");
        states.put("Virgin Islands","VI");
        states.put("Virginia","VA");
        states.put("Washington","WA");
        states.put("West Virginia","WV");
        states.put("Wisconsin","WI");
        states.put("Wyoming","WY");
        states.put("Yukon Territory","YT");

        clientDetails.setState_abbreviation(states.get(state));
    }



//    public class ThreadToReadThroughLines implements Runnable {
//
//        private Boolean finishedReading;
//        private Queue<String> queueOfLines;
//        private HashSet<Integer> setOfClientIds;
//        private HashSet<ClientDetails> setOfClientObjects;
//
//        public ThreadToReadThroughLines(Boolean finishedReading, Queue<String> queueOfLines, HashSet<Integer> setOfClientIds, HashSet<ClientDetails> setOfClientObjects) {
//            this.finishedReading = finishedReading;
//            this.queueOfLines = queueOfLines;
//            this.setOfClientIds = setOfClientIds;
//            this.setOfClientObjects = setOfClientObjects;
//        }
//
//        public void run() {
//            Iterator<String> itr = queueOfLines.iterator();
//            while(!finishedReading) {
//                while (itr.hasNext()) {
//
//                        ClientDetails clientDetails = new ClientDetails();
//                        clientDetails.setIs_client_active(true);
//                        clientDetails.setIs_new_client(false);
//                        clientDetails.setIs_allowed_video(false);
//
//                        String thisLine = itr.next();
//                        String[] splitLine = thisLine.split(",");
//                        int clientId = 0;
//                        if (!splitLine[0].isEmpty()) {
//                            clientId = Integer.valueOf(splitLine[0]);
//
//                            //TODO: Set client ID into set if we found one
//                            setOfClientIds.add(clientId);
//                        }
//
//                        clientDetails.setClient_id(clientId);
//
//                        String lastName = splitLine[1];
//                        if (lastName.startsWith("(")) {
//                            lastName = lastName.substring(1, lastName.length() - 1);
//                        }
//
//                        if (lastName.length() > 1) {
//                            lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
//                        }
//
//                        clientDetails.setLast_name(lastName);
//
//                        String firstName = splitLine[2];
//
//                        if (firstName.startsWith("(")) {
//                            firstName = firstName.substring(1, firstName.length() - 1);
//                        }
//
//                        if (firstName.length() > 1) {
//                            firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
//                        }
//
//                        clientDetails.setFirst_name(firstName);
//
//                        String address = splitLine[3];
//
//                        if (address.startsWith("\"") && address.length() > 1) {
//                            address = address.substring(1, address.length() - 1);
//                        }
//
//                        clientDetails.setStreet_address(address);
//
//                        String city = splitLine[4];
//
//                        clientDetails.setCity(city);
//
//                        String state = splitLine[5];
//
//                        setStateAbbreviationWithMap(clientDetails, state);
//
//                        String zipCode = splitLine[6];
//
//                        clientDetails.setZip_code(zipCode);
//
//                        if (splitLine.length >= 9) {
//                            String phoneNumber = splitLine[8];
//
//                            clientDetails.setPhone_number(phoneNumber);
//                        }
//
//
//                        if (splitLine.length == 10) {
//                            String email = splitLine[9];
//
//                            clientDetails.setEmail(email);
//
//                        }
//
//                        clientDetails.setIs_on_email_list(false);
//
//                        Date date = new Date();
//                        Timestamp theLatestTimestamp = new Timestamp(date.getTime());
//                        clientDetails.setDate_of_entry(theLatestTimestamp);
//
//                        clientDetails.setHas_record_of_liability(false);
//
//                        // TODO: Set the clientDetails object into the set
//
//                        setOfClientObjects.add(clientDetails);
//
//                }
//            }
//        }

//        // Invoke it like so:
//        Runnable r1 = new ThreadToReadThroughLines(finishedReading, queue, setOfClientIds, setOfClientObjects);
//        new Thread(r1).start();
//
//        Runnable r2 = new ThreadToReadThroughLines(finishedReading, queue, setOfClientIds, setOfClientObjects);
//        new Thread(r2).start();
//    }

    //                    // TODO: create user
//
//                    int newUserId = getNewUserId();
//
//                    ClientDetails clientDetails = new ClientDetails();
//                    clientDetails.setIs_client_active(true);
//                    clientDetails.setIs_new_client(false);
//                    clientDetails.setIs_allowed_video(false);

    //TODO: Set user ID
//                    clientDetails.setUser_id(newUserId);
//
//                    // splitLine
//
//                    String[] splitLine = line.split(",");
//                    int clientId = 0;
//                    if (!splitLine[0].isEmpty()) {
//                        clientId = Integer.valueOf(splitLine[0]);
//
    // TODO: Check if client exists
//                        if (doesClientExistByClientId(clientId)) {
//                            continue;
//                        }
//                    }
//
//                    clientDetails.setClient_id(clientId);
//
//
//                    String lastName = splitLine[1];
//                    if (lastName.startsWith("(")) {
//                        lastName = lastName.substring(1,lastName.length()-1);
//                    }
//
//                    if (lastName.length() > 1) {
//                        lastName = lastName.substring(0,1).toUpperCase() + lastName.substring(1).toLowerCase();
//                    }
//
//                    clientDetails.setLast_name(lastName);
//
//                    String firstName = splitLine[2];
//
//                    if (firstName.startsWith("(")) {
//                        firstName = firstName.substring(1,firstName.length()-1);
//                    }
//
//                    if (firstName.length() > 1) {
//                        firstName = firstName.substring(0,1).toUpperCase() + firstName.substring(1).toLowerCase();
//                    }
//
//
//                    clientDetails.setFirst_name(firstName);
//
//                    String address = splitLine[3];
//
//                    if (address.startsWith("\"") && address.length() > 1) {
//                        address = address.substring(1,address.length()-1);
//                    }
//
//                    clientDetails.setStreet_address(address);
//
//                    String city = splitLine[4];
//
//                    clientDetails.setCity(city);
//
//                    String state = splitLine[5];
//
//                    setStateAbbreviationWithMap(clientDetails, state);
//
//                    String zipCode = splitLine[6];
//
//                    clientDetails.setZip_code(zipCode);
//
//                    if (splitLine.length >= 9) {
//                        String phoneNumber = splitLine[8];
//
//                        clientDetails.setPhone_number(phoneNumber);
//                    }
//
//
//                    if (splitLine.length == 10) {
//                        String email = splitLine[9];
//
//                        clientDetails.setEmail(email);
//
//                    }
//
//                    clientDetails.setIs_on_email_list(false);
//
//                    Date date = new Date();
//                    Timestamp theLatestTimestamp = new Timestamp(date.getTime());
//                    clientDetails.setDate_of_entry(theLatestTimestamp);
//
//                    clientDetails.setHas_record_of_liability(false);
//
    //TODO: Upload Client into DB depending on whether they had a client ID or not
//                    if (clientId != 0) {
    //TODO: These upload methods also check for email if it exists
//                        uploadClient(clientDetails);
//                    } else {
    //TODO: These upload methods also check for email if it exists
//                        uploadClientNoClientId(clientDetails);
//                    }

    //        // print 1 number every 100ms
//        Thread t1 = new Thread() {
//            public void run() {
//                Iterator<String> itr = queue.iterator();
//                while(itr.hasNext()) {
//                    ClientDetails clientDetails = new ClientDetails();
//                    clientDetails.setIs_client_active(true);
//                    clientDetails.setIs_new_client(false);
//                    clientDetails.setIs_allowed_video(false);
//
//                    String thisLine = itr.next();
//                    String[] splitLine = thisLine.split(",");
//                    int clientId = 0;
//                    if (!splitLine[0].isEmpty()) {
//                        clientId = Integer.valueOf(splitLine[0]);
//
//                        //TODO: Set client ID into set if we found one
//                        setOfClientIds.add(clientId);
//                    }
//
//                    clientDetails.setClient_id(clientId);
//
//                    String lastName = splitLine[1];
//                    if (lastName.startsWith("(")) {
//                        lastName = lastName.substring(1, lastName.length() - 1);
//                    }
//
//                    if (lastName.length() > 1) {
//                        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
//                    }
//
//                    clientDetails.setLast_name(lastName);
//
//                    String firstName = splitLine[2];
//
//                    if (firstName.startsWith("(")) {
//                        firstName = firstName.substring(1, firstName.length() - 1);
//                    }
//
//                    if (firstName.length() > 1) {
//                        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
//                    }
//
//                    clientDetails.setFirst_name(firstName);
//
//                    String address = splitLine[3];
//
//                    if (address.startsWith("\"") && address.length() > 1) {
//                        address = address.substring(1, address.length() - 1);
//                    }
//
//                    clientDetails.setStreet_address(address);
//
//                    String city = splitLine[4];
//
//                    clientDetails.setCity(city);
//
//                    String state = splitLine[5];
//
//                    setStateAbbreviationWithMap(clientDetails, state);
//
//                    String zipCode = splitLine[6];
//
//                    clientDetails.setZip_code(zipCode);
//
//                    if (splitLine.length >= 9) {
//                        String phoneNumber = splitLine[8];
//
//                        clientDetails.setPhone_number(phoneNumber);
//                    }
//
//
//                    if (splitLine.length == 10) {
//                        String email = splitLine[9];
//
//                        clientDetails.setEmail(email);
//
//                    }
//
//                    clientDetails.setIs_on_email_list(false);
//
//                    Date date = new Date();
//                    Timestamp theLatestTimestamp = new Timestamp(date.getTime());
//                    clientDetails.setDate_of_entry(theLatestTimestamp);
//
//                    clientDetails.setHas_record_of_liability(false);
//
//                    // TODO: Set the clientDetails object into the set
//
//                    setOfClientObjects.add(clientDetails);
//                }
//            }
//        };
//
//
//        Thread t2 = new Thread() {
//            public void run() {
//                Iterator<String> itr = queue.iterator();
//                while(itr.hasNext()) {
//                    ClientDetails clientDetails = new ClientDetails();
//                    clientDetails.setIs_client_active(true);
//                    clientDetails.setIs_new_client(false);
//                    clientDetails.setIs_allowed_video(false);
//
//                    String thisLine = itr.next();
//                    String[] splitLine = thisLine.split(",");
//                    int clientId = 0;
//                    if (!splitLine[0].isEmpty()) {
//                        clientId = Integer.valueOf(splitLine[0]);
//
//                        //TODO: Set client ID into set if we found one
//                        setOfClientIds.add(clientId);
//                    }
//
//                    clientDetails.setClient_id(clientId);
//
//                    String lastName = splitLine[1];
//                    if (lastName.startsWith("(")) {
//                        lastName = lastName.substring(1, lastName.length() - 1);
//                    }
//
//                    if (lastName.length() > 1) {
//                        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
//                    }
//
//                    clientDetails.setLast_name(lastName);
//
//                    String firstName = splitLine[2];
//
//                    if (firstName.startsWith("(")) {
//                        firstName = firstName.substring(1, firstName.length() - 1);
//                    }
//
//                    if (firstName.length() > 1) {
//                        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
//                    }
//
//                    clientDetails.setFirst_name(firstName);
//
//                    String address = splitLine[3];
//
//                    if (address.startsWith("\"") && address.length() > 1) {
//                        address = address.substring(1, address.length() - 1);
//                    }
//
//                    clientDetails.setStreet_address(address);
//
//                    String city = splitLine[4];
//
//                    clientDetails.setCity(city);
//
//                    String state = splitLine[5];
//
//                    setStateAbbreviationWithMap(clientDetails, state);
//
//                    String zipCode = splitLine[6];
//
//                    clientDetails.setZip_code(zipCode);
//
//                    if (splitLine.length >= 9) {
//                        String phoneNumber = splitLine[8];
//
//                        clientDetails.setPhone_number(phoneNumber);
//                    }
//
//
//                    if (splitLine.length == 10) {
//                        String email = splitLine[9];
//
//                        clientDetails.setEmail(email);
//
//                    }
//
//                    clientDetails.setIs_on_email_list(false);
//
//                    Date date = new Date();
//                    Timestamp theLatestTimestamp = new Timestamp(date.getTime());
//                    clientDetails.setDate_of_entry(theLatestTimestamp);
//
//                    clientDetails.setHas_record_of_liability(false);
//
//                    // TODO: Set the clientDetails object into the set
//
//                    setOfClientObjects.add(clientDetails);
//
//                    break;
//                }
//            }
//        };
//
//        t1.start();
//        t2.start();
//
//        try {
//            t1.join();
//            t2.join();
//        }
//
//        catch(InterruptedException e)
//        {
//            e.printStackTrace();
//        }

}
