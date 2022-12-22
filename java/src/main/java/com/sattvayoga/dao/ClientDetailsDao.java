package com.sattvayoga.dao;

import com.sattvayoga.model.ClientDetails;

public interface ClientDetailsDao {

    ClientDetails createClient(ClientDetails client);

    ClientDetails findClientByUserId(int userId);

    boolean updateClientDetails(ClientDetails clientDetails);
}
