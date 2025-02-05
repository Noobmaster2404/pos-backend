package com.increff.server.api;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;

import com.increff.server.dao.ClientDao;
import com.increff.server.entity.Client;
import com.increff.commons.exception.ApiException;

@Service
@Transactional(rollbackFor = Exception.class)
public class ClientApi {

    @Autowired
    private ClientDao dao;

    public Client addClient(Client client) throws ApiException {
        Client existing = dao.selectByName(client.getClientName());
        if (Objects.nonNull(existing)) {
            throw new ApiException("Client with name '" + client.getClientName() + "' already exists");
        }
        dao.insert(client);
        return client;
    }

    @Transactional(readOnly = true)
    public List<Client> getAllClients(){
        return dao.selectAll();
    }

    public Client updateClientById(Integer clientId, Client client) throws ApiException {
        Client existingClient = getCheckClientById(clientId);
        if (!existingClient.getClientName().equals(client.getClientName())) {
            Client duplicateCheck = dao.selectByName(client.getClientName());
            if (Objects.nonNull(duplicateCheck)) {
                throw new ApiException("Client with name '" + client.getClientName() + "' already exists");
            }
        }
        existingClient.setClientName(client.getClientName());
        existingClient.setPhone(client.getPhone());
        existingClient.setEmail(client.getEmail());
        existingClient.setEnabled(client.getEnabled()); 
        return existingClient;
    }

    @Transactional(readOnly = true)
    public Client getCheckClientById(Integer clientId) throws ApiException {
        Client client = dao.select(clientId);
        if (Objects.isNull(client)) {
            throw new ApiException("Client with id " + clientId + " not found");
        }
        return client;
    }

    @Transactional(readOnly = true)
    public Map<Integer, String> getCheckClientNamesByIds(List<Integer> clientIds) throws ApiException {
        List<String> clientNames = dao.selectNamesByIds(clientIds);
        if (clientNames.size() < clientIds.size()) {
            throw new ApiException("One or more client IDs did not match any existing clients");
        }
        Map<Integer, String> clientMap = new HashMap<>();
        for (int i = 0; i < clientIds.size(); i++) {
            clientMap.put(clientIds.get(i), clientNames.get(i));
        }
        return clientMap;
    }

    @Transactional(readOnly = true)
    public List<Client> getCheckClientsByIds(List<Integer> clientIds) throws ApiException {
        List<Client> clients = dao.selectByIds(clientIds);
        if (clients.size() < clientIds.size()) {
            throw new ApiException("One or more client IDs did not match any existing clients");
        }
        return clients;
    }
}
