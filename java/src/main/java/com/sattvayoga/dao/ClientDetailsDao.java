package com.sattvayoga.dao;

import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.PackageDetails;
import com.sattvayoga.model.PaginatedListOfClients;
import org.springframework.web.multipart.MultipartFile;

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

    PaginatedListOfClients getPaginatedClientsForEvent(int page, int pageSize, String search, String sortBy, boolean sortDesc, int eventId);

    PaginatedListOfClients getAllPaginatedDuplicateClients(int page, int pageSize, String search);

    void removeDuplicateClients(int clientIdToKeep, int clientIdToRemove);

    boolean updateClientCustomerId(int clientId, String customerId);

    ClientDetails findClientByCustomerId(String customerID);

    boolean saveNewClientEmail(int clientId, String newEmail);

    void uploadClientCsv(MultipartFile multipartFile);

    boolean isClientTableEmpty();
}
