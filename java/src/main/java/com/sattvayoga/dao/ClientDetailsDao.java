package com.sattvayoga.dao;

import com.sattvayoga.model.ClientDetails;
import com.sattvayoga.model.PackageDetails;

import java.util.List;

public interface ClientDetailsDao {

    ClientDetails createClient(ClientDetails client);

    ClientDetails findClientByUserId(int userId);

    boolean updateClientDetails(ClientDetails clientDetails);

    public List<ClientDetails> getAllClients();

    boolean deleteClient(int clientId);
}
