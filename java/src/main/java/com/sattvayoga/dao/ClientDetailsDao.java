package com.sattvayoga.dao;

import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.PackageDetails;
import com.sattvayoga.model.PaginatedListOfClients;

import java.util.List;

public interface ClientDetailsDao {

    ClientDetails createClient(ClientDetails client);

    ClientDetails findClientByUserId(int userId);

    boolean updateClientDetails(ClientDetails clientDetails);

    public List<ClientDetails> getAllClients();

    boolean deleteClient(int clientId);

    ClientDetails findClientByClientId(int clientId);

    ClientDetails createNewClient(ClientDetails client);

    boolean isEmailDuplicate(int clientId, String email);

    List<ClientDetails> getAllDuplicateClients();

    PaginatedListOfClients getAllPaginatedClients(int page, int pageSize, String search, String sortBy, boolean sortDesc);

    PaginatedListOfClients getAllPaginatedDuplicateClients(int page, int pageSize, String search);

    void removeDuplicateClients(int clientIdToKeep, int clientIdToRemove);
}
