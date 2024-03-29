package com.sattvayoga.dao;

import com.sattvayoga.model.*;
import org.springframework.dao.DataAccessException;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.*;

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
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
            while(result.next()){
                Family family = mapRowToFamily(result);
                family.setQuick_details("("+family.getFamily_id()+") " + family.getFamily_name());
                family.setListOfFamilyMembers(getFamilyMemberListByFamilyId(family.getFamily_id()));
                allFamilies.add(family);
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to retrieve all families.");
        }
        return allFamilies;
    }

    @Override
    public void addClientToFamily(int client_id, int family_id){
        try {
            String sql = "Delete from client_family WHERE client_id = ?;";
            jdbcTemplate.update(sql,client_id);
            sql = "INSERT INTO client_family (client_id, family_id) VALUES (?,?);";
            jdbcTemplate.update(sql,client_id,family_id);
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to add client to family.");
        }
    }

    @Override
    public int createNewFamily(String family_name) {
        String sql = "INSERT into families (family_name) VALUES (?) RETURNING family_id;";
        int newFamilyId = 0;
        try {
            newFamilyId = jdbcTemplate.queryForObject(sql,Integer.class, family_name);
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to create new family.");
        }
        return newFamilyId;
    }

    @Override
    public void updateFamilyName(Family newFamilyName) {
        String sql = "UPDATE families SET family_name = ? WHERE family_id =?;";
        try {
            jdbcTemplate.update(sql,newFamilyName.getFamily_name(),newFamilyName.getFamily_id());
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to update family name.");
        }
    }

    @Override
    public void deleteFamily(Family familyToDelete) {
        try {
            String sql = "DELETE FROM client_family WHERE family_id = ?";
            jdbcTemplate.update(sql, familyToDelete.getFamily_id());
            sql = "DELETE FROM families WHERE family_id = ?";
            jdbcTemplate.update(sql, familyToDelete.getFamily_id());
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to delete family name.");
        }
    }

    @Override
    public Family getFamilyDetailsByFamilyId(int familyId) {
        Family family = new Family();

        String sql = "SELECT * FROM families WHERE family_id = ?";
        try {
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
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to get family details by ID.");
        }

        return family;
    }

    @Override
    public void removeFamilyMembersFromSelectedClients(List<ClientDetails> clientDetailsList) {
        for (int i = 0; i < clientDetailsList.size(); i++) {
            int currentClientId = clientDetailsList.get(i).getClient_id();
            int familyId = clientDetailsList.get(i).getFamily_id();

            String sql = "DELETE FROM client_family WHERE client_id = ? AND family_id = ?";
            try {
                jdbcTemplate.update(sql, currentClientId, familyId);
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Cause: " + e.getCause());
                throw new CustomException("Failed to remove a family member from selected clients in a list.");
            }
        }
    }

    @Override
    public void uploadFamily(MultipartFile multipartFile) {
        int count = 0;

        List<String> listOfStringsFromBufferedReader = new ArrayList<>();

        Set<Family> familySetFromFile = new HashSet<>();

        HashMap<String,Integer> findClientColumns = new HashMap<>();
        try (BufferedReader fileReader = new BufferedReader(new
                InputStreamReader(multipartFile.getInputStream(), "UTF-8"))) {

            String line;
            while ((line = fileReader.readLine()) != null) {
                if (count > 0) {

                    listOfStringsFromBufferedReader.add(line);

                } else {
                    findClientColumns = findClientIDs(line.split(","));
                }

                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        readLinesFromListAndPopulateSet(listOfStringsFromBufferedReader, familySetFromFile, findClientColumns);

        // HashMap of Name and Newly created Family ID
        HashMap<String, Integer> mapOfNewFamilies = new HashMap<>();

        // Creates the new Families in DB.
        updateMapOfNewFamilies(mapOfNewFamilies, familySetFromFile);

        if (!familySetFromFile.isEmpty()) {
            for (Family family : familySetFromFile) {
                int familyId = family.getFamily_id();
                for (Integer clientId : family.getListOfFamilyMembersClientIds()) {
                    addClientToFamily(clientId, familyId);
                }
            }
        }
    }

    public void updateMapOfNewFamilies(HashMap<String, Integer> mapOfNewFamilies, Set<Family> familySetFromFile) {

        for(Family family : familySetFromFile) {
            String familyName = family.getFamily_name();
            String oldFamilyName = family.getFamily_name();
            int newFamilyId = createNewFamily(familyName);
            family.setFamily_id(newFamilyId);
            familyName = ""+newFamilyId+"-"+familyName;
            family.setFamily_name(familyName);
            updateFamilyName(family);
            familySetFromFile.add(family);
            mapOfNewFamilies.put(familyName, newFamilyId);
        }
    }


    public void readLinesFromListAndPopulateSet(List<String> listOfStrings, Set<Family> familySetFromFile, HashMap<String,Integer> findClientColumns ) {
        for (int i = 0; i < listOfStrings.size(); i++) {

            Family family = new Family();
//
//            int leftLimit = 48; // numeral '0'
//            int rightLimit = 122; // letter 'z'
//            int targetStringLength = 10;
//            Random random = new Random();
//
//            String generatedFamilyName = "F " + random.ints(leftLimit, rightLimit + 1)
//                    .filter(z -> (z <= 57 || z >= 65) && (z <= 90 || z >= 97))
//                    .limit(targetStringLength)
//                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
//                    .toString();
//
//            family.setFamily_name(generatedFamilyName);

            String thisLine = listOfStrings.get(i);
            String[] splitLine = thisLine.split(",");

            List<Integer> listOfIndexes = new ArrayList<>(findClientColumns.values());

            for (int j = 0; j < listOfIndexes.size(); j++) {
                int clientIndex = listOfIndexes.get(j);
                String clientIdString = splitLine[clientIndex].replaceAll("[^\\d]", "");
                int clientId = Integer.valueOf(clientIdString);
                family.getListOfFamilyMembersClientIds().add(clientId);
                family.setFamily_name(splitLine[clientIndex + 1]);
            }

            familySetFromFile.add(family);
        }
    }

    public static HashMap<String, Integer> findClientIDs(String[] array) {
        HashMap<String, Integer> clientIdMap = new HashMap<>();
        int clientIdCounter = 1;

        for (int i = 0; i < array.length; i++) {
            if (array[i].contains("ClientID")) {
                String clientIdKey = "ClientID" + clientIdCounter;
                clientIdMap.put(clientIdKey, i);
                clientIdCounter++;
            }
        }

        return clientIdMap;
    }

    public List<ClientDetails> getFamilyMemberListByFamilyId(int familyId) {
        List<ClientDetails> familyMemberList = new ArrayList<>();

        String sql = "SELECT * FROM client_details JOIN client_family ON client_details.client_id = client_family.client_id WHERE family_id = ?";

        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, familyId);

            while(result.next()) {
                ClientDetails familyMember = mapRowToClient(result);
                familyMember.setQuick_details("(" + familyMember.getClient_id() + ")" + " " + familyMember.getFirst_name() + " " + familyMember.getLast_name());
                familyMemberList.add(familyMember);
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
            throw new CustomException("Failed to retrieve family members by ID.");
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
