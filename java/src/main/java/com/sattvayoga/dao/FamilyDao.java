package com.sattvayoga.dao;

import com.sattvayoga.model.ClassDetails;
import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.Family;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;

public interface FamilyDao {

    List<Family> getAllFamilies() throws SQLException;

    void addClientToFamily(int client_id, int family_id);

    int createNewFamily(String family_name);

    void updateFamilyName(Family newFamilyName);

    void deleteFamily(Family familyToDelete);

    Family getFamilyDetailsByFamilyId(int familyId);

    void removeFamilyMembersFromSelectedClients(List<ClientDetails> clientDetailsList);

    void uploadFamily(MultipartFile multipartFile);
}
