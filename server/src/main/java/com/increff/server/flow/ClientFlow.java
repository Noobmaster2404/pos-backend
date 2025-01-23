package com.increff.server.flow;

import com.increff.commons.exception.ApiException;
import com.increff.server.api.ClientApi;
import com.increff.server.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ClientFlow {

    @Autowired
    private ClientApi clientApi;

    public void add(Client client) throws ApiException {
        clientApi.add(client);
    }

    public List<Client> getAll() throws ApiException {
        return clientApi.getAll();
    }

    public Client get(Integer clientId) throws ApiException {
        return clientApi.get(clientId);
    }

    public Client update(Integer clientId, Client client) throws ApiException {
        return clientApi.update(clientId, client);
    }
}
