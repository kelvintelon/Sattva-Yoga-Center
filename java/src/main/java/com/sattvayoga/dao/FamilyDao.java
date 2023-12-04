package com.sattvayoga.dao;

import com.sattvayoga.model.Family;

import java.sql.SQLException;
import java.util.List;

public interface FamilyDao {

    List<Family> getAllFamilies() throws SQLException;

    void addClientToFamily(int client_id, int family_id);

    int createNewFamily(int client_id, String family_name);

    void updateFamilyName(Family newFamilyName);

    void deleteFamily(Family familyToDelete);

    Family getFamilyDetailsByFamilyId(int familyId);
}
