package com.increff.server.flow;

import com.increff.commons.exception.ApiException;
import com.increff.server.api.ClientApi;
import com.increff.server.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = ApiException.class)
public class ClientFlow {

    @Autowired
    private ClientApi clientApi;

    public void add(Client client) throws ApiException {
        clientApi.add(client);
    }

    public List<Client> getAll() throws ApiException {
        return clientApi.getAll();
    }

    public Client get(int id) throws ApiException {
        return clientApi.get(id);
    }

    public void update(int id, Client client) throws ApiException {
        clientApi.update(id, client);
    }
}
