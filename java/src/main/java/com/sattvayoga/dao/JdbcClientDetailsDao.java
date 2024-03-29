package com.sattvayoga.dao;

import com.sattvayoga.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.CheckedOutputStream;

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
            searchString = " WHERE (first_name ILIKE ? OR last_name ILIKE ? OR email ILIKE ?) OR (CONCAT(first_name, ' ', last_name) ILIKE ?) ";

            String sql = "SELECT * FROM client_details" + searchString + " ORDER BY " + sortBy + " " + sortDirection + offsetString;

            try {
                SqlRowSet result = jdbcTemplate.queryForRowSet(sql, search, search, search, search, offset);

                while (result.next()) {
                    ClientDetails clientDetails = mapRowToClient(result);

                    clientDetails.setFull_address(clientDetails.getStreet_address() + " "
                            + clientDetails.getCity() + " " + clientDetails.getState_abbreviation() + " " + clientDetails.getZip_code());

                    clientDetails.setQuick_details("(" + clientDetails.getClient_id() + ")" + " " + clientDetails.getFirst_name() + " " + clientDetails.getLast_name());

                    String familyName = getFamilyNameByClientId(clientDetails.getClient_id());
                    clientDetails.setFamily_name(familyName);
                    allClients.add(clientDetails);
                }
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Cause: " + e.getCause());
                throw new CustomException("Failed to retrieve paginated clients with search.");
            }

            PaginatedListOfClients paginatedListOfClients = new PaginatedListOfClients();
            paginatedListOfClients.setListOfClients(allClients);

            String countSql = "Select COUNT(*) from client_details" + searchString;

            int count = 0;
            try {
                count = jdbcTemplate.queryForObject(countSql, Integer.class,  search, search, search, search);
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Cause: " + e.getCause());
                throw new CustomException("Failed to retrieve count of paginated clients with search.");
            }

            paginatedListOfClients.setTotalRows(count);
            return paginatedListOfClients;

        } else {

            String sql = "SELECT * FROM client_details ORDER BY " + sortBy + " " + sortDirection + offsetString;

            try {
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
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Cause: " + e.getCause());
                throw new CustomException("Failed to retrieve paginated clients without search.");
            }

            PaginatedListOfClients paginatedListOfClients = new PaginatedListOfClients();
            paginatedListOfClients.setListOfClients(allClients);

            String countSql = "Select COUNT(*) from client_details";

            int count = 0;
            try {
                count = jdbcTemplate.queryForObject(countSql, Integer.class);
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Cause: " + e.getCause());
                throw new CustomException("Failed to retrieve count of paginated clients without search.");
            }

            paginatedListOfClients.setTotalRows(count);
            return paginatedListOfClients;
        }

    }

    @Override
    public PaginatedListOfClients getPaginatedClientsForEvent(int page, int pageSize, String search, String sortBy, boolean sortDesc, int eventId) {
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
            searchString = " WHERE client_id NOT IN (SELECT client_id from client_event WHERE event_id = ?) AND (first_name ILIKE ? OR last_name ILIKE ? OR email ILIKE ?) OR (CONCAT(first_name, ' ', last_name) ILIKE ?) ";

            // TODO: Here is where you filter for clients that are already tied to this specific event's attendance

            String sql = "SELECT * FROM client_details" + searchString + " ORDER BY " + sortBy + " " + sortDirection + offsetString;

            try {
                SqlRowSet result = jdbcTemplate.queryForRowSet(sql, eventId, search, search, search, search, offset);

                while (result.next()) {
                    ClientDetails clientDetails = mapRowToClient(result);

                    clientDetails.setFull_address(clientDetails.getStreet_address() + " "
                            + clientDetails.getCity() + " " + clientDetails.getState_abbreviation() + " " + clientDetails.getZip_code());

                    clientDetails.setQuick_details("(" + clientDetails.getClient_id() + ")" + " " + clientDetails.getFirst_name() + " " + clientDetails.getLast_name());

                    String familyName = getFamilyNameByClientId(clientDetails.getClient_id());
                    clientDetails.setFamily_name(familyName);
                    allClients.add(clientDetails);
                }
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Cause: " + e.getCause());
                throw new CustomException("Failed to retrieve paginated clients for event with search.");
            }

            PaginatedListOfClients paginatedListOfClients = new PaginatedListOfClients();
            paginatedListOfClients.setListOfClients(allClients);

            String countSql = "Select COUNT(*) from client_details" + searchString;

            int count = 0;
            try {
                count = jdbcTemplate.queryForObject(countSql, Integer.class, eventId, search, search, search, search);
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Cause: " + e.getCause());
                throw new CustomException("Failed to retrieve count of paginated clients for event with search.");
            }

            paginatedListOfClients.setTotalRows(count);
            return paginatedListOfClients;

        } else {

            String sql = "SELECT * FROM client_details WHERE client_id NOT IN (SELECT client_id from client_event WHERE event_id = ?) ORDER BY " + sortBy + " " + sortDirection + offsetString;

            try {
                SqlRowSet result = jdbcTemplate.queryForRowSet(sql, eventId, offset);

                while (result.next()) {
                    ClientDetails clientDetails = mapRowToClient(result);

                    clientDetails.setFull_address(clientDetails.getStreet_address() + " "
                            + clientDetails.getCity() + " " + clientDetails.getState_abbreviation() + " " + clientDetails.getZip_code());

                    clientDetails.setQuick_details("(" + clientDetails.getClient_id() + ")" + " " + clientDetails.getFirst_name() + " " + clientDetails.getLast_name());

                    String familyName = getFamilyNameByClientId(clientDetails.getClient_id());
                    clientDetails.setFamily_name(familyName);
                    allClients.add(clientDetails);
                }
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Cause: " + e.getCause());
                throw new CustomException("Failed to retrieve paginated clients for event without search.");
            }

            PaginatedListOfClients paginatedListOfClients = new PaginatedListOfClients();
            paginatedListOfClients.setListOfClients(allClients);

            String countSql = "Select COUNT(*) from client_details";

            int count = 0;
            try {
                count = jdbcTemplate.queryForObject(countSql, Integer.class);
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Cause: " + e.getCause());
                throw new CustomException("Failed to retrieve count of paginated clients for event without search.");
            }

            paginatedListOfClients.setTotalRows(count);
            return paginatedListOfClients;
        }
    }

    @Override
    public PaginatedListOfClients getPaginatedClientsForFamily(int page, int pageSize, String search, String sortBy, boolean sortDesc, int familyId) {
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
            searchString = " WHERE client_id NOT IN (SELECT client_id from client_family WHERE family_id = ?) AND (first_name ILIKE ? OR last_name ILIKE ? OR email ILIKE ?) OR (CONCAT(first_name, ' ', last_name) ILIKE ?) ";

            // TODO: Here is where you filter for clients that are already tied to this specific event's attendance

            String sql = "SELECT * FROM client_details" + searchString + " ORDER BY " + sortBy + " " + sortDirection + offsetString;

            try {
                SqlRowSet result = jdbcTemplate.queryForRowSet(sql, familyId, search, search, search, search, offset);

                while (result.next()) {
                    ClientDetails clientDetails = mapRowToClient(result);

                    clientDetails.setFull_address(clientDetails.getStreet_address() + " "
                            + clientDetails.getCity() + " " + clientDetails.getState_abbreviation() + " " + clientDetails.getZip_code());

                    clientDetails.setQuick_details("(" + clientDetails.getClient_id() + ")" + " " + clientDetails.getFirst_name() + " " + clientDetails.getLast_name());

                    String familyName = getFamilyNameByClientId(clientDetails.getClient_id());
                    clientDetails.setFamily_name(familyName);
                    allClients.add(clientDetails);
                }
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Cause: " + e.getCause());
                throw new CustomException("Failed to retrieve paginated clients for family with search.");
            }

            PaginatedListOfClients paginatedListOfClients = new PaginatedListOfClients();
            paginatedListOfClients.setListOfClients(allClients);

            String countSql = "Select COUNT(*) from client_details" + searchString;

            int count = 0;
            try {
                count = jdbcTemplate.queryForObject(countSql, Integer.class, familyId, search, search, search, search);
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Cause: " + e.getCause());
                throw new CustomException("Failed to retrieve count of paginated clients for family with search.");
            }

            paginatedListOfClients.setTotalRows(count);
            return paginatedListOfClients;

        } else {

            String sql = "SELECT * FROM client_details WHERE client_id NOT IN (SELECT client_id from client_family WHERE family_id = ?) ORDER BY " + sortBy + " " + sortDirection + offsetString;

            try {
                SqlRowSet result = jdbcTemplate.queryForRowSet(sql, familyId, offset);

                while (result.next()) {
                    ClientDetails clientDetails = mapRowToClient(result);

                    clientDetails.setFull_address(clientDetails.getStreet_address() + " "
                            + clientDetails.getCity() + " " + clientDetails.getState_abbreviation() + " " + clientDetails.getZip_code());

                    clientDetails.setQuick_details("(" + clientDetails.getClient_id() + ")" + " " + clientDetails.getFirst_name() + " " + clientDetails.getLast_name());

                    String familyName = getFamilyNameByClientId(clientDetails.getClient_id());
                    clientDetails.setFamily_name(familyName);
                    allClients.add(clientDetails);
                }
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Cause: " + e.getCause());
                throw new CustomException("Failed to retrieve paginated clients for family without search.");
            }

            PaginatedListOfClients paginatedListOfClients = new PaginatedListOfClients();
            paginatedListOfClients.setListOfClients(allClients);

            String countSql = "Select COUNT(*) from client_details";

            int count = 0;
            try {
                count = jdbcTemplate.queryForObject(countSql, Integer.class);
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Cause: " + e.getCause());
                throw new CustomException("Failed to retrieve count of paginated clients for family without search.");
            }

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
            searchString = " WHERE (first_name ILIKE ? OR last_name ILIKE ? OR email ILIKE ?) OR (CONCAT(first_name, ' ', last_name) ILIKE ?) ";

            String sql = "SELECT * FROM client_details" + searchString + " ORDER BY client_id" + offsetString;

            try {
                SqlRowSet result = jdbcTemplate.queryForRowSet(sql, search, search, search, search, offset);

                while (result.next()) {
                    ClientDetails clientDetails = mapRowToClient(result);

                    clientDetails.setFull_address(clientDetails.getStreet_address() + " "
                            + clientDetails.getCity() + " " + clientDetails.getState_abbreviation() + " " + clientDetails.getZip_code());

                    clientDetails.setQuick_details("(" + clientDetails.getClient_id() + ")" + " " + clientDetails.getFirst_name() + " " + clientDetails.getLast_name());

                    String familyName = getFamilyNameByClientId(clientDetails.getClient_id());
                    clientDetails.setFamily_name(familyName);
                    allClients.add(clientDetails);
                }
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Cause: " + e.getCause());
                throw new CustomException("Failed to retrieve paginated clients for duplicates with search.");
            }

            PaginatedListOfClients paginatedListOfClients = new PaginatedListOfClients();
            paginatedListOfClients.setListOfClients(allClients);

            String countSql = "Select COUNT(*) from client_details" + searchString;

            int count = 0;
            try {
                count = jdbcTemplate.queryForObject(countSql, Integer.class,  search, search, search, search);
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Cause: " + e.getCause());
                throw new CustomException("Failed to retrieve count of paginated clients for duplicates with search.");
            }

            paginatedListOfClients.setTotalRows(count);
            return paginatedListOfClients;

        } else {
            PaginatedListOfClients paginatedListOfClients = new PaginatedListOfClients();

            String sql = "SELECT a1.* " +
                    "FROM client_details a1 " +
                    "WHERE exists (SELECT * " +
                    "FROM client_details a2 " +
                    "WHERE a2.last_name = a1.last_name AND SUBSTRING(a2.first_name from 0 for 3) = SUBSTRING(a1.first_name from 0 for 3) " +
                    "AND a2.client_id <> a1.client_id) ORDER BY a1.last_name, a1.first_name " + offsetString;

            try {
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
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Cause: " + e.getCause());
                throw new CustomException("Failed to retrieve paginated clients for duplicates without search.");
            }

            paginatedListOfClients.setListOfClients(allClients);

            String countSql = "SELECT COUNT(a1.*) " +
                    "FROM client_details a1 " +
                    "WHERE exists (SELECT * " +
                    "FROM client_details a2 " +
                    "WHERE a2.last_name = a1.last_name AND SUBSTRING(a2.first_name from 0 for 3) = SUBSTRING(a1.first_name from 0 for 3) " +
                    "AND a2.client_id <> a1.client_id);";

            int count = 0;
            try {
                count = jdbcTemplate.queryForObject(countSql, Integer.class);
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Cause: " + e.getCause());
                throw new CustomException("Failed to retrieve count of paginated clients for duplicates without search.");
            }

            paginatedListOfClients.setTotalRows(count);

            return paginatedListOfClients;
        }

    }

    @Override
    public List<ClientDetails> getAllClients() {
        List<ClientDetails> allClients = new ArrayList<>();

        String sql = "SELECT * FROM client_details ORDER BY client_id;";
        try {
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
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to retrieve all clients.");
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
                "AND a2.client_id <> a1.client_id) ORDER BY a1.last_name, a1.first_name;";

        try {
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
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to retrieve all duplicate clients.");
        }
        return allClients;
    }

    @Override
    public ClientDetails createClient(ClientDetails client) {
        String sql = "INSERT INTO client_details (last_name, first_name, is_client_active, " +
                "is_new_client, street_address, city, state_abbreviation, zip_code, country, " +
                "phone_number, is_on_email_list, email, has_record_of_liability, " +
                "date_of_entry, is_allowed_video, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING client_id";
        int clientId = 0;
        try {
            clientId = jdbcTemplate.queryForObject(sql, Integer.class, client.getLast_name(), client.getFirst_name(),
                    client.isIs_client_active(), client.isIs_new_client(), client.getStreet_address(), client.getCity(),
                    client.getState_abbreviation(), client.getZip_code(), client.getCountry(), client.getPhone_number(), client.isIs_on_email_list(),
                    client.getEmail(), client.isHas_record_of_liability(),
                    client.getDate_of_entry(), client.isIs_allowed_video(), client.getUser_id());
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to create client.");
        }
        client.setClient_id(clientId);
        return client;
    }

    @Override
    public ClientDetails createNewClient(ClientDetails client) {
        String sql = "INSERT INTO client_details (last_name, first_name, is_client_active, email, is_on_email_list, " +
                "is_new_client, user_id, date_of_entry, is_allowed_video) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING client_id";
        int clientId = 0;
        try {
            clientId = jdbcTemplate.queryForObject(sql, Integer.class, client.getLast_name(), client.getFirst_name(),
                    client.isIs_client_active(), client.getEmail(), client.isIs_on_email_list(), client.isIs_new_client(), client.getUser_id(), client.getDate_of_entry(), client.isIs_allowed_video());
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to create new client.");
        }
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
        ClientDetails clientDetails = null;
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            clientDetails = new ClientDetails();
            if (results.next()) {
                clientDetails = mapRowToClient(results);
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to find client by user ID.");
        }

        return clientDetails;
    }

    @Override
    public ClientDetails findClientByClientId(int clientId) {
        String sql = "SELECT * FROM client_details WHERE client_id = ?";
        ClientDetails clientDetails = null;
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, clientId);
            clientDetails = new ClientDetails();
            if (results.next()) {
                clientDetails = mapRowToClient(results);
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to find client by ID.");
        }

        return clientDetails;
    }

    @Override
    public ClientDetails findClientByCustomerId(String customerID) {
        String sql = "SELECT * FROM client_details WHERE customer_id = ?";
        ClientDetails clientDetails = null;
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, customerID);
            clientDetails = new ClientDetails();
            if (results.next()) {
                clientDetails = mapRowToClient(results);
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to find client by cus ID.");
        }

        return clientDetails;
    }

    @Override
    public void removeDuplicateClients(int clientIdToKeep, int clientIdToRemove) {
        String sql = "UPDATE client_event SET client_id = ? WHERE client_id = ? " +
                "AND event_id NOT IN (SELECT event_id FROM client_event WHERE client_id = ? OR client_id = ? GROUP BY event_id having count(event_id) > 1);";
        try {
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
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to remove duplicate clients.");
        }
    }

    @Override
    public boolean updateClientDetails(ClientDetails clientDetails) {
        String sql = "UPDATE client_details SET last_name = ? , " +
                "first_name = ? , " +
                "street_address = ? , " +
                "city = ? , " +
                "state_abbreviation = ? , " +
                "zip_code = ? , " +
                "country = ? , " +
                "email = ? , " +
                "phone_number = ? , " +
                "is_on_email_list = ? , " +
                "has_record_of_liability = ? , " +
                "is_client_active = ?  , " +
                "is_allowed_video = ?  , " +
                "is_new_client = ? " +
                "WHERE user_id = ?";
        try {
            return jdbcTemplate.update(sql, clientDetails.getLast_name(), clientDetails.getFirst_name(),
                    clientDetails.getStreet_address(), clientDetails.getCity(), clientDetails.getState_abbreviation(),
                    clientDetails.getZip_code(), clientDetails.getCountry(), clientDetails.getEmail(), clientDetails.getPhone_number(),
                    clientDetails.isIs_on_email_list(), clientDetails.isHas_record_of_liability(),
                    clientDetails.isIs_client_active(), clientDetails.isIs_allowed_video(), clientDetails.isIs_new_client(),
                    clientDetails.getUser_id()) == 1;
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to update client details.");
        }
    }

    @Override
    public boolean updateClientCustomerId(int clientId, String customerId) {
        String sql = "UPDATE client_details SET customer_id = ? WHERE client_id = ?";
        try {
            return jdbcTemplate.update(sql, customerId, clientId) == 1;
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to update customer ID for client.");
        }
    }

    @Override
    public boolean saveNewClientEmail(int clientId, String newEmail) {
        String sql = "UPDATE client_details SET email = ? WHERE client_id = ?";
        try {
            return jdbcTemplate.update(sql, newEmail, clientId)==1;
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to save new client email.");
        }
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
        try {
            return jdbcTemplate.update(sql, clientId, clientId, clientId, clientId) == 1;
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to delete client.");
        }
    }

    public String getFamilyNameByClientId(int clientId) {
        String sql = "SELECT family_name from families \n" +
                "JOIN client_family ON families.family_id = client_family.family_id \n" +
                "WHERE client_id = ?";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, clientId);
            if (result.next()) {
                return result.getString("family_name");
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to retrieve family name for client.");
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

    @Override
    public void uploadClientCsv(MultipartFile multipartFile) {

        int count = 0;

        long startTimeForEntireUpload = System.nanoTime();

        List<String> listOfStringsFromBufferedReader = new ArrayList<>();

        HashSet<ClientDetails> setOfClientDetailsFromFile = new HashSet<>();

        long startTimeForReading = System.nanoTime();

        HashMap<String,Integer> mapColumns = new HashMap<>();


        try (BufferedReader fileReader = new BufferedReader(new
                InputStreamReader(multipartFile.getInputStream(), "UTF-8"))) {

            String line;
            while ((line = fileReader.readLine()) != null) {

                if (count > 0) {

                    listOfStringsFromBufferedReader.add(line);

                } else {
                    String[] firstLine =  line.split(",");
                    mapColumns = populateColumnsForMap(firstLine);
                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        readLinesFromListAndPopulateSets(listOfStringsFromBufferedReader, setOfClientDetailsFromFile, mapColumns);

        long endTimeForReading = System.nanoTime();
        long totalTimeForReading = endTimeForReading - startTimeForReading;
//        System.out.println("Total Time for reading file : " + getReadableTime(totalTimeForReading) + " / " + totalTimeForReading  + " ns");

        long startTimeForDoesClientExists = System.nanoTime();

        // We turn it into a list in order to modify as we iterate
        List<ClientDetails> clientDetailsList = new ArrayList<>(setOfClientDetailsFromFile);
        Set<ClientDetails> setOfClientObjectsWithNoDuplicates = new HashSet<>(clientDetailsList);

        // Check if client exists with a populated table
        checkIfClientIdIsDuplicate(clientDetailsList, setOfClientObjectsWithNoDuplicates);

        long endTimeForDoesClientExists = System.nanoTime();
        long totalTimeForDoesClientExists = endTimeForDoesClientExists - startTimeForDoesClientExists;
//        System.out.println("Total time to check if client ID exists already : "  + getReadableTime(totalTimeForDoesClientExists)+ " / " + totalTimeForDoesClientExists  + " ns");

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
//        System.out.println("Total time for creating a batch of users + check existing emails: " + getReadableTime(totalTimeForCreateCheckClients) + " / " + totalTimeForCreateCheckClients  + " ns");

        long startTimeForBatchUpload = System.nanoTime();

        batchCreateClientDetailsSaveId(newSetOfClientObjectsToSaveId);

        long endTimeForBatchUpload = System.nanoTime();
        long totalTimeForBatchUpload = endTimeForBatchUpload - startTimeForBatchUpload;
//        System.out.println("Total time for batch upload in : " + getReadableTime(totalTimeForBatchUpload) + " / " + totalTimeForBatchUpload + " ns");

        long startTimeForBatchUploadNoClientId = System.nanoTime();

        batchCreateClientDetailsWithNeedsNewId(setOfClientDetailsFromFileWithNoId);

        long endTimeForBatchUploadNoClientId = System.nanoTime();
        long totalTimeForBatchUploadNoClientId = endTimeForBatchUploadNoClientId - startTimeForBatchUploadNoClientId;
//        System.out.println("Total time for batch upload with no Client Id in : " + getReadableTime(totalTimeForBatchUploadNoClientId) + " / " + totalTimeForBatchUploadNoClientId + " ns");

        long endTimeForEntireUpload = System.nanoTime();
        long totalTimeForEntireUpload = endTimeForEntireUpload - startTimeForEntireUpload;
//        System.out.println("Total time for entire upload in : " + getReadableTime(totalTimeForEntireUpload) + " / " + totalTimeForEntireUpload + " ns");
    }

    public static HashMap<String, Integer> populateColumnsForMap(String[] array) {
        HashMap<String, Integer> columnMap = new HashMap<>();

        for (int i = 0; i < array.length; i++) {
            String currentString = array[i];
            if (currentString.contains("ID")) {
                columnMap.put("ID", i);
            } else if (currentString.contains("Last name")) {
                columnMap.put("Last name", i);
            } else if (currentString.contains("First name")) {
                columnMap.put("First name", i);
            } else if (currentString.contains("Address")) {
                columnMap.put("Address", i);
            } else if (currentString.contains("City")) {
                columnMap.put("City", i);
            } else if (currentString.contains("State")) {
                columnMap.put("State", i);
            }  else if (currentString.contains("Postal code")) {
                columnMap.put("Postal code", i);
            } else if (currentString.contains("Country")) {
                columnMap.put("Country", i);
            } else if (currentString.contains("Phone")) {
                columnMap.put("Phone", i);
            } else if (currentString.contains("Email")) {
                columnMap.put("Email", i);
            }

        }

        return columnMap;
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

        Set<YogaUser> setOfYogaUsers = new HashSet<>();
        Set<String> setOfUsernames = new HashSet<>();

//        Map<String, String> mapGenUsernameToGenPassword = new HashMap<>();

        // Set of usernames to populate, map to populate
        generateUsernamesAndPasswordThenPopulateSets(setOfClients, setOfUsernames, usernameToClientDetailsMap, setOfYogaUsers);

        // send batch of user objects
        batchCreateUsers(setOfYogaUsers);

        // retrieve a Hashmap where <Key username, Value user id>
        Map<String, Integer> usernameMapToId = retrieveUsernameAndIdMap(setOfUsernames);

        setUserIdIntoClientDetailsInMap(usernameToClientDetailsMap, usernameMapToId);

        // then return the set of clients after the user ID has been set into each object.
        return usernameToClientDetailsMap;
    }

    private void populateSetOfYogaUsersWithMaps(Set<YogaUser> setOfYogaUsers,
                                                Map<String, String> firstMapToHashPassword,
                                                Map<String, String> secondMapToHashPassword,
                                                Map<String, String> thirdMapToHashPassword,
                                                Map<String, String> fourthMapToHashPassword,
                                                Map<String, String> fifthMapToHashPassword,
                                                Map<String, String> sixthMapToHashPassword,
                                                Map<String, String> seventhMapToHashPassword,
                                                Map<String, String> eighthMapToHashPassword) {
        Map<String, String> combinedMap = new HashMap<>();
        combinedMap.putAll(firstMapToHashPassword);
        combinedMap.putAll(secondMapToHashPassword);
        combinedMap.putAll(thirdMapToHashPassword);
        combinedMap.putAll(fourthMapToHashPassword);
        combinedMap.putAll(fifthMapToHashPassword);
        combinedMap.putAll(sixthMapToHashPassword);
        combinedMap.putAll(seventhMapToHashPassword);
        combinedMap.putAll(eighthMapToHashPassword);

        for (Map.Entry<String, String> entry : combinedMap.entrySet()) {

            YogaUser yogaUser = new YogaUser();

            String username = entry.getKey();
            String hashedPassword = entry.getValue();

            yogaUser.setUsername(username);
            yogaUser.setPassword(hashedPassword);
            setOfYogaUsers.add(yogaUser);
        }
    }

    private void runThreadsToHashPassword(Map<String, String> firstMapToHashPassword,
                                          Map<String, String> secondMapToHashPassword,
                                          Map<String, String> thirdMapToHashPassword,
                                          Map<String, String> fourthMapToHashPassword,
                                          Map<String, String> fifthMapToHashPassword,
                                          Map<String, String> sixthMapToHashPassword,
                                          Map<String, String> seventhMapToHashPassword,
                                          Map<String, String> eighthMapToHashPassword) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Runnable encryptMap = () -> {
            Map<String, String> mapToProcess = null;

            // Identify which map this thread is responsible for
            Thread currentThread = Thread.currentThread();
            if (currentThread.getName().equals("Thread-1")) {
                mapToProcess = firstMapToHashPassword;
            } else if (currentThread.getName().equals("Thread-2")) {
                mapToProcess = secondMapToHashPassword;
            } else if (currentThread.getName().equals("Thread-3")) {
                mapToProcess = thirdMapToHashPassword;
            } else if (currentThread.getName().equals("Thread-4")) {
                mapToProcess = fourthMapToHashPassword;
            } else if (currentThread.getName().equals("Thread-5")) {
                mapToProcess = fifthMapToHashPassword;
            } else if (currentThread.getName().equals("Thread-6")) {
                mapToProcess = sixthMapToHashPassword;
            } else if (currentThread.getName().equals("Thread-7")) {
                mapToProcess = seventhMapToHashPassword;
            } else if (currentThread.getName().equals("Thread-8")) {
                mapToProcess = eighthMapToHashPassword;
            }

            if (mapToProcess != null) {
                for (Map.Entry<String, String> entry : mapToProcess.entrySet()) {
                    String username = entry.getKey();
                    String password = entry.getValue();
                    String hashedPassword = passwordEncoder.encode(password);

                    // Update the map with the hashed password
                    mapToProcess.put(username, hashedPassword);
                }
            }
        };

        // Create and start three threads
        Thread thread1 = new Thread(encryptMap);
        Thread thread2 = new Thread(encryptMap);
        Thread thread3 = new Thread(encryptMap);
        Thread thread4 = new Thread(encryptMap);
        Thread thread5 = new Thread(encryptMap);
        Thread thread6 = new Thread(encryptMap);
        Thread thread7 = new Thread(encryptMap);
        Thread thread8 = new Thread(encryptMap);

        thread1.setName("Thread-1");
        thread2.setName("Thread-2");
        thread3.setName("Thread-3");
        thread4.setName("Thread-4");
        thread5.setName("Thread-5");
        thread6.setName("Thread-6");
        thread7.setName("Thread-7");
        thread8.setName("Thread-8");

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        thread7.start();
        thread8.start();

        // Wait for all threads to finish
        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
            thread5.join();
            thread6.join();
            thread7.join();
            thread8.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Shutdown the executor service
        executor.shutdown();
    }

    private void divideGeneratedUsernameAndPasswordMap(Map<String, String> mapGenUsernameToGenPassword,
                                                       Map<String, String> firstMapToHashPassword,
                                                       Map<String, String> secondMapToHashPassword,
                                                       Map<String, String> thirdMapToHashPassword,
                                                       Map<String, String> fourthMapToHashPassword,
                                                       Map<String, String> fifthMapToHashPassword,
                                                       Map<String, String> sixthMapToHashPassword,
                                                       Map<String, String> seventhMapToHashPassword,
                                                       Map<String, String> eighthMapToHashPassword) {
        int totalSize = mapGenUsernameToGenPassword.size();
        int chunkSize = totalSize / 8;
        int currentChunk = 1;

        for (Map.Entry<String, String> entry : mapGenUsernameToGenPassword.entrySet()) {
            if (currentChunk <= chunkSize) {
                firstMapToHashPassword.put(entry.getKey(), entry.getValue());
            } else if (currentChunk <= 2 * chunkSize) {
                secondMapToHashPassword.put(entry.getKey(), entry.getValue());
            } else if (currentChunk <= 3 * chunkSize) {
                thirdMapToHashPassword.put(entry.getKey(), entry.getValue());
            } else if (currentChunk <= 4 * chunkSize) {
                fourthMapToHashPassword.put(entry.getKey(), entry.getValue());
            } else if (currentChunk <= 5 * chunkSize) {
                fifthMapToHashPassword.put(entry.getKey(), entry.getValue());
            } else if (currentChunk <= 6 * chunkSize) {
                sixthMapToHashPassword.put(entry.getKey(), entry.getValue());
            } else if (currentChunk <= 7 * chunkSize) {
                seventhMapToHashPassword.put(entry.getKey(), entry.getValue());
            } else {
                eighthMapToHashPassword.put(entry.getKey(), entry.getValue());
            }
            currentChunk++;
        }
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

    private void generateUsernamesAndPasswordThenPopulateSets(Set<ClientDetails> setOfClients, Set<String> setOfUsernames, Map<String,ClientDetails> mapToPopulate, Set<YogaUser> yogaUsers) {
        for (ClientDetails clientDetails : setOfClients) {
            YogaUser yogaUser = new YogaUser();

            String generatedUsername;
            int leftLimit = 48; // numeral '0'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();

            // create username
            generatedUsername = random.ints(leftLimit, rightLimit + 1)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            generatedUsername = "user" + generatedUsername;

//            mapGenUsernameToGenPassword.put(generatedUsername, generatedPassword);
            yogaUser.setUsername(generatedUsername);
            yogaUser.setPassword("");

            yogaUsers.add(yogaUser);

            setOfUsernames.add(generatedUsername);
            mapToPopulate.put(generatedUsername,clientDetails);
        }
    }

    public void batchCreateClientDetailsSaveId(final Collection<ClientDetails> clients) {
        try {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO client_details (client_id, last_name, " +
                            "first_name, is_client_active, email, is_on_email_list, " +
                            "is_new_client, user_id, date_of_entry, is_allowed_video, " +
                            "street_address, city, state_abbreviation, zip_code, country, " +
                            "phone_number, has_record_of_liability) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
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
                        ps.setString(15, clientDetails.getCountry());
                        ps.setString(16, clientDetails.getPhone_number());
                        ps.setBoolean(17, clientDetails.isHas_record_of_liability());

                    });
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to batch create clients with ID.");
        }

    }

    public void batchCreateClientDetailsWithNeedsNewId(final Collection<ClientDetails> clients) {
        try {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO client_details (last_name, " +
                            "first_name, is_client_active, email, is_on_email_list, " +
                            "is_new_client, user_id, date_of_entry, is_allowed_video, " +
                            "street_address, city, state_abbreviation, zip_code, country, " +
                            "phone_number, has_record_of_liability) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
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
                        ps.setString(14, clientDetails.getCountry());
                        ps.setString(15, clientDetails.getPhone_number());
                        ps.setBoolean(16, clientDetails.isHas_record_of_liability());

                    });
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to batch create clients.");
        }

    }

    public void batchCreateUsers(final Collection<YogaUser> users) {
        try {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO users (username, password_hash, role, activated) " +
                            "VALUES (?,?,?,?)",
                    users,
                    100,
                    (PreparedStatement ps, YogaUser yogaUser) -> {
                        ps.setString(1, yogaUser.getUsername());
                        ps.setString(2, yogaUser.getPassword());
                        ps.setString(3, "ROLE_USER");
                        ps.setBoolean(4, false);

                    });
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to batch create users.");
        }
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
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                int user_id = results.getInt("user_id");
                String username = results.getString("username");
                mapToReturn.put(username,user_id);
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to retrieve a map of Usernames and IDs.");
        }

        return mapToReturn;
    }

    public Set<String> getSetOfExistingEmails() {
        String sql = "SELECT email from client_details";
        Set<String> setToReturn = new HashSet<>();
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
            while (result.next()) {
                setToReturn.add(result.getString("email"));
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to get a set of existing emails.");
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
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
            while (result.next()) {
                setToReturn.add(result.getInt("client_id"));
            }
            return setToReturn;
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to get set of existing clients.");
        }
    }

    @Override
    public boolean isEmailDuplicate(int clientId, String email) {

        String sql = "SELECT client_id, COUNT(email) FROM client_details WHERE email = ? GROUP BY client_id;";
        int count = 0;
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, email);
            count = 0;
            while (result.next()) {
                if (result.getInt("client_id") != clientId) {
                    count++;
                }
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to confirm if email is duplicate.");
        }
        return count > 0;
    }

    @Override
    public boolean isClientTableEmpty() {
        int count = 0;
        String sql = "SELECT COUNT(client_id) FROM client_details;";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
            if (result.next()) {
                count = result.getInt("count");
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to confirm if client table is empty.");
        }
        return count == 0;
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

    private void readLinesFromListAndPopulateSets(List<String> listOfStringsFromBufferedReader, HashSet<ClientDetails> setOfClientDetailsFromFile, HashMap<String,Integer> columnMap) {
        for(int i = 0; i < listOfStringsFromBufferedReader.size(); i++) {
            ClientDetails clientDetails = new ClientDetails();
            clientDetails.setIs_client_active(true);
            clientDetails.setIs_new_client(false);
            clientDetails.setIs_allowed_video(false);

            String thisLine = listOfStringsFromBufferedReader.get(i);
            String[] splitLine = thisLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            int clientId = 0;
            if (!splitLine[0].isEmpty()) {
                clientId = Integer.valueOf(splitLine[columnMap.get("ID")]);
            }

            clientDetails.setClient_id(clientId);

            String lastName = splitLine[columnMap.get("Last name")];
            if (lastName.startsWith("(")) {
                lastName = lastName.substring(1, lastName.length() - 1);
            }

            if (lastName.length() > 1) {
                lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
            }

            clientDetails.setLast_name(lastName);

            String firstName = splitLine[columnMap.get("First name")];

            if (firstName.startsWith("(")) {
                firstName = firstName.substring(1, firstName.length() - 1);
            }

            if (firstName.length() > 1) {
                firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
            }

            clientDetails.setFirst_name(firstName);

            String address = splitLine[columnMap.get("Address")];

            if (address.startsWith("\"") && address.length() > 1) {
                address = address.substring(1, address.length() - 1);
            }

            clientDetails.setStreet_address(address);

            String city = splitLine[columnMap.get("City")];

            clientDetails.setCity(city);

            String state = splitLine[columnMap.get("State")];

            setStateAbbreviationWithMap(clientDetails, state);

            String zipCode = splitLine[columnMap.get("Postal code")];

            clientDetails.setZip_code(zipCode);

            String country = splitLine[columnMap.get("Country")];

            setCountry(clientDetails, country);

            if (splitLine.length >= 9) {
                String phoneNumber = splitLine[columnMap.get("Phone")];

                clientDetails.setPhone_number(phoneNumber);
            }


            if (splitLine.length == 10) {
                String email = splitLine[columnMap.get("Email")];

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

    private void setCountry(ClientDetails clientDetails, String country) {
        HashMap<String, String> countryAbbreviations = new HashMap<>();

        countryAbbreviations.put("AF", "Afghanistan");
        countryAbbreviations.put("AL", "Albania");
        countryAbbreviations.put("DZ", "Algeria");
        countryAbbreviations.put("AD", "Andorra");
        countryAbbreviations.put("AO", "Angola");
        countryAbbreviations.put("AG", "Antigua and Barbuda");
        countryAbbreviations.put("AR", "Argentina");
        countryAbbreviations.put("AM", "Armenia");
        countryAbbreviations.put("AU", "Australia");
        countryAbbreviations.put("AT", "Austria");
        countryAbbreviations.put("AZ", "Azerbaijan");
        countryAbbreviations.put("BS", "Bahamas");
        countryAbbreviations.put("BH", "Bahrain");
        countryAbbreviations.put("BD", "Bangladesh");
        countryAbbreviations.put("BB", "Barbados");
        countryAbbreviations.put("BY", "Belarus");
        countryAbbreviations.put("BE", "Belgium");
        countryAbbreviations.put("BZ", "Belize");
        countryAbbreviations.put("BJ", "Benin");
        countryAbbreviations.put("BT", "Bhutan");
        countryAbbreviations.put("BO", "Bolivia");
        countryAbbreviations.put("BA", "Bosnia and Herzegovina");
        countryAbbreviations.put("BW", "Botswana");
        countryAbbreviations.put("BR", "Brazil");
        countryAbbreviations.put("BN", "Brunei");
        countryAbbreviations.put("BG", "Bulgaria");
        countryAbbreviations.put("BF", "Burkina Faso");
        countryAbbreviations.put("BI", "Burundi");
        countryAbbreviations.put("CV", "Cabo Verde");
        countryAbbreviations.put("KH", "Cambodia");
        countryAbbreviations.put("CM", "Cameroon");
        countryAbbreviations.put("CA", "Canada");
        countryAbbreviations.put("CF", "Central African Republic");
        countryAbbreviations.put("TD", "Chad");
        countryAbbreviations.put("CL", "Chile");
        countryAbbreviations.put("CN", "China");
        countryAbbreviations.put("CO", "Colombia");
        countryAbbreviations.put("KM", "Comoros");
        countryAbbreviations.put("CD", "Congo (Congo-Kinshasa)");
        countryAbbreviations.put("CG", "Congo (Congo-Brazzaville)");
        countryAbbreviations.put("CR", "Costa Rica");
        countryAbbreviations.put("HR", "Croatia");
        countryAbbreviations.put("CU", "Cuba");
        countryAbbreviations.put("CY", "Cyprus");
        countryAbbreviations.put("CZ", "Czech Republic");
        countryAbbreviations.put("CI", "Côte d'Ivoire");
        countryAbbreviations.put("KP", "North Korea");
        countryAbbreviations.put("KR", "South Korea");
        countryAbbreviations.put("DK", "Denmark");
        countryAbbreviations.put("DJ", "Djibouti");
        countryAbbreviations.put("DM", "Dominica");
        countryAbbreviations.put("DO", "Dominican Republic");
        countryAbbreviations.put("EC", "Ecuador");
        countryAbbreviations.put("EG", "Egypt");
        countryAbbreviations.put("SV", "El Salvador");
        countryAbbreviations.put("GQ", "Equatorial Guinea");
        countryAbbreviations.put("ER", "Eritrea");
        countryAbbreviations.put("EE", "Estonia");
        countryAbbreviations.put("ET", "Ethiopia");
        countryAbbreviations.put("FJ", "Fiji");
        countryAbbreviations.put("FI", "Finland");
        countryAbbreviations.put("FR", "France");
        countryAbbreviations.put("GA", "Gabon");
        countryAbbreviations.put("GM", "Gambia");
        countryAbbreviations.put("GE", "Georgia");
        countryAbbreviations.put("DE", "Germany");
        countryAbbreviations.put("GH", "Ghana");
        countryAbbreviations.put("GR", "Greece");
        countryAbbreviations.put("GD", "Grenada");
        countryAbbreviations.put("GT", "Guatemala");
        countryAbbreviations.put("GN", "Guinea");
        countryAbbreviations.put("GW", "Guinea-Bissau");
        countryAbbreviations.put("GY", "Guyana");
        countryAbbreviations.put("HT", "Haiti");
        countryAbbreviations.put("HN", "Honduras");
        countryAbbreviations.put("HU", "Hungary");
        countryAbbreviations.put("IS", "Iceland");
        countryAbbreviations.put("IN", "India");
        countryAbbreviations.put("ID", "Indonesia");
        countryAbbreviations.put("IR", "Iran");
        countryAbbreviations.put("IQ", "Iraq");
        countryAbbreviations.put("IE", "Ireland");
        countryAbbreviations.put("IL", "Israel");
        countryAbbreviations.put("IT", "Italy");
        countryAbbreviations.put("JM", "Jamaica");
        countryAbbreviations.put("JP", "Japan");
        countryAbbreviations.put("JO", "Jordan");
        countryAbbreviations.put("KZ", "Kazakhstan");
        countryAbbreviations.put("KE", "Kenya");
        countryAbbreviations.put("KI", "Kiribati");
        countryAbbreviations.put("XK", "Kosovo");
        countryAbbreviations.put("KW", "Kuwait");
        countryAbbreviations.put("KG", "Kyrgyzstan");
        countryAbbreviations.put("LA", "Laos");
        countryAbbreviations.put("LV", "Latvia");
        countryAbbreviations.put("LB", "Lebanon");
        countryAbbreviations.put("LS", "Lesotho");
        countryAbbreviations.put("LR", "Liberia");
        countryAbbreviations.put("LY", "Libya");
        countryAbbreviations.put("LI", "Liechtenstein");
        countryAbbreviations.put("LT", "Lithuania");
        countryAbbreviations.put("LU", "Luxembourg");
        countryAbbreviations.put("MG", "Madagascar");
        countryAbbreviations.put("MW", "Malawi");
        countryAbbreviations.put("MY", "Malaysia");
        countryAbbreviations.put("MV", "Maldives");
        countryAbbreviations.put("ML", "Mali");
        countryAbbreviations.put("MT", "Malta");
        countryAbbreviations.put("MH", "Marshall Islands");
        countryAbbreviations.put("MR", "Mauritania");
        countryAbbreviations.put("MU", "Mauritius");
        countryAbbreviations.put("MX", "Mexico");
        countryAbbreviations.put("FM", "Micronesia");
        countryAbbreviations.put("MD", "Moldova");
        countryAbbreviations.put("MC", "Monaco");
        countryAbbreviations.put("MN", "Mongolia");
        countryAbbreviations.put("ME", "Montenegro");
        countryAbbreviations.put("MA", "Morocco");
        countryAbbreviations.put("MZ", "Mozambique");
        countryAbbreviations.put("MM", "Myanmar (Burma)");
        countryAbbreviations.put("NA", "Namibia");
        countryAbbreviations.put("NR", "Nauru");
        countryAbbreviations.put("NP", "Nepal");
        countryAbbreviations.put("NL", "Netherlands");
        countryAbbreviations.put("NZ", "New Zealand");
        countryAbbreviations.put("NI", "Nicaragua");
        countryAbbreviations.put("NE", "Niger");
        countryAbbreviations.put("NG", "Nigeria");
        countryAbbreviations.put("NO", "Norway");
        countryAbbreviations.put("OM", "Oman");
        countryAbbreviations.put("PK", "Pakistan");
        countryAbbreviations.put("PW", "Palau");
        countryAbbreviations.put("PS", "Palestine");
        countryAbbreviations.put("PA", "Panama");
        countryAbbreviations.put("PG", "Papua New Guinea");
        countryAbbreviations.put("PY", "Paraguay");
        countryAbbreviations.put("PE", "Peru");
        countryAbbreviations.put("PH", "Philippines");
        countryAbbreviations.put("PL", "Poland");
        countryAbbreviations.put("PT", "Portugal");
        countryAbbreviations.put("QA", "Qatar");
        countryAbbreviations.put("RO", "Romania");
        countryAbbreviations.put("RU", "Russia");
        countryAbbreviations.put("RW", "Rwanda");
        countryAbbreviations.put("KN", "Saint Kitts and Nevis");
        countryAbbreviations.put("LC", "Saint Lucia");
        countryAbbreviations.put("VC", "Saint Vincent and the Grenadines");
        countryAbbreviations.put("WS", "Samoa");
        countryAbbreviations.put("SM", "San Marino");
        countryAbbreviations.put("ST", "Sao Tome and Principe");
        countryAbbreviations.put("SA", "Saudi Arabia");
        countryAbbreviations.put("SN", "Senegal");
        countryAbbreviations.put("RS", "Serbia");
        countryAbbreviations.put("SC", "Seychelles");
        countryAbbreviations.put("SL", "Sierra Leone");
        countryAbbreviations.put("SG", "Singapore");
        countryAbbreviations.put("SK", "Slovakia");
        countryAbbreviations.put("SI", "Slovenia");
        countryAbbreviations.put("SB", "Solomon Islands");
        countryAbbreviations.put("SO", "Somalia");
        countryAbbreviations.put("ZA", "South Africa");
        countryAbbreviations.put("SS", "South Sudan");
        countryAbbreviations.put("ES", "Spain");
        countryAbbreviations.put("LK", "Sri Lanka");
        countryAbbreviations.put("SD", "Sudan");
        countryAbbreviations.put("SR", "Suriname");
        countryAbbreviations.put("SE", "Sweden");
        countryAbbreviations.put("CH", "Switzerland");
        countryAbbreviations.put("SY", "Syria");
        countryAbbreviations.put("TW", "Taiwan");
        countryAbbreviations.put("TJ", "Tajikistan");
        countryAbbreviations.put("TZ", "Tanzania");
        countryAbbreviations.put("TH", "Thailand");
        countryAbbreviations.put("TG", "Togo");
        countryAbbreviations.put("TO", "Tonga");
        countryAbbreviations.put("TT", "Trinidad and Tobago");
        countryAbbreviations.put("TN", "Tunisia");
        countryAbbreviations.put("TR", "Turkey");
        countryAbbreviations.put("TM", "Turkmenistan");
        countryAbbreviations.put("TV", "Tuvalu");
        countryAbbreviations.put("UG", "Uganda");
        countryAbbreviations.put("UA", "Ukraine");
        countryAbbreviations.put("AE", "United Arab Emirates");
        countryAbbreviations.put("GB", "United Kingdom");
        countryAbbreviations.put("US", "United States");
        countryAbbreviations.put("UY", "Uruguay");
        countryAbbreviations.put("UZ", "Uzbekistan");
        countryAbbreviations.put("VU", "Vanuatu");
        countryAbbreviations.put("VA", "Vatican City (Holy See)");
        countryAbbreviations.put("VE", "Venezuela");
        countryAbbreviations.put("VN", "Vietnam");
        countryAbbreviations.put("YE", "Yemen");
        countryAbbreviations.put("ZM", "Zambia");
        countryAbbreviations.put("ZW", "Zimbabwe");

        if (countryAbbreviations.containsKey(country)) {
            clientDetails.setCountry(countryAbbreviations.get(country));
        } else {
            clientDetails.setCountry("");
        }

    }


}
