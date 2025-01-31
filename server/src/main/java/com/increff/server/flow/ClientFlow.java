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
//didn't rollback just for Api Exception because what if there's a Runtime Exception
//that is not wrapped in Api Exception?
public class ClientFlow {

    @Autowired
    //TODO: Read IOC Container, Bean lifecycle, etc
    private ClientApi clientApi;

    public Client addClient(Client client) throws ApiException {
        return clientApi.addClient(client);
    }

    public List<Client> getAllClients() throws ApiException {
        return clientApi.getAllClients();
    }

    public Client getClientById(Integer clientId) throws ApiException {
        return clientApi.getClientById(clientId);
    }

    public Client updateClientById(Integer clientId, Client client) throws ApiException {
        return clientApi.updateClientById(clientId, client);
    }

    public List<Client> getClientsByName(String namePrefix) throws ApiException {
        return clientApi.getClientsByName(namePrefix);
    }
}
