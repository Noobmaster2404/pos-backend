package com.increff.server.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import com.increff.commons.exception.ApiException;
import com.increff.server.api.ClientApi;
import com.increff.server.entity.Client;

@Service
@Transactional(rollbackFor = Exception.class)
public class ClientFlow {

    @Autowired
    private ClientApi clientApi;

    public Client addClient(Client client) throws ApiException {
        return clientApi.addClient(client);
    }

    public List<Client> getAllClients() throws ApiException {
        return clientApi.getAllClients();
    }

    public Client updateClientById(Integer clientId, Client client) throws ApiException {
        return clientApi.updateClientById(clientId, client);
    }
}
