package com.increff.server.api;

import java.util.List;
import java.util.Objects;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import com.increff.server.dao.ClientDao;
import com.increff.server.entity.Client;
import com.increff.commons.exception.ApiException;

@Service
public class ClientApi {

    @Autowired
    private ClientDao dao;

    @Transactional(rollbackFor = ApiException.class)
    public void add(Client client) throws ApiException {
        checkValid(client);
        Client existing = dao.selectByName(client.getClientName());
        if (Objects.nonNull(existing)) {
            throw new ApiException("Client with name '" + client.getClientName() + "' already exists");
        }
        dao.insert(client);
    }

    @Transactional(readOnly = true)
    public List<Client> getAll() throws ApiException {
        try {
            return dao.selectAll();
        } catch (Exception e) {
            throw new ApiException("Error while fetching clients: " + e.getMessage());
        }
    }

    @Transactional(rollbackFor = ApiException.class)
    public void update(Integer clientId, Client client) throws ApiException {
        checkValid(client);
        Client existingClient = dao.select(clientId);
        if (Objects.isNull(existingClient)) {
            throw new ApiException("Client with given ID does not exist");
        }
        
        if (!existingClient.getClientName().equals(client.getClientName())) {
            //name is being changed
            Client duplicateCheck = dao.selectByName(client.getClientName());
            if (Objects.nonNull(duplicateCheck)) {
                throw new ApiException("Client with name '" + client.getClientName() + "' already exists");
            }
        }
        
        existingClient.setClientName(client.getClientName());
        existingClient.setClientPhone(client.getClientPhone());
        existingClient.setClientEmail(client.getClientEmail());
        existingClient.setClientEnabled(client.getClientEnabled());
        dao.update(existingClient);
    }

    @Transactional(readOnly = true)
    public Client get(Integer clientId) throws ApiException {
        Client client = dao.select(clientId);
        if (Objects.isNull(client)) {
            throw new ApiException("Client with id " + clientId + " not found");
        }
        return client;
    }

    private void checkValid(Client client) throws ApiException {
        // Null and empty checks
        if (StringUtils.isEmpty(client.getClientName())) {
            throw new ApiException("Client name cannot be empty");
        }
        if (StringUtils.isEmpty(client.getClientPhone())) {
            throw new ApiException("Client phone cannot be empty");
        }
        if (StringUtils.isEmpty(client.getClientEmail())) {
            throw new ApiException("Client email cannot be empty");
        }

        // Format and length checks
        if (client.getClientName().length() > 256) {
            throw new ApiException("Client name cannot exceed 256 characters");
        }
        if (!client.getClientPhone().matches("\\d{10}")) {
            throw new ApiException("Phone must be exactly 10 digits");
        }
        if (client.getClientEmail().length() > 256 || !client.getClientEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ApiException("Invalid email format or length");
        }
    }
}
