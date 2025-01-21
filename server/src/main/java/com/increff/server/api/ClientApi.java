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
        Client existing = dao.selectByName(client.getName());
        if (Objects.nonNull(existing)) {
            throw new ApiException("Client with name '" + client.getName() + "' already exists");
        }
        dao.insert(client);
    }

    @Transactional
    public List<Client> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackFor = ApiException.class)
    public void update(int id, Client client) throws ApiException {
        checkValid(client);
        Client existingClient = dao.select(id);
        if (Objects.isNull(existingClient)) {
            throw new ApiException("Client with given ID does not exist");
        }
        
        if (!existingClient.getName().equals(client.getName())) {
            Client duplicateCheck = dao.selectByName(client.getName());
            if (Objects.nonNull(duplicateCheck)) {
                throw new ApiException("Client with name '" + client.getName() + "' already exists");
            }
        }
        
        existingClient.setName(client.getName());
        existingClient.setPhone(client.getPhone());
        existingClient.setEmail(client.getEmail());
        existingClient.setEnabled(client.getEnabled());
        dao.update(existingClient);
    }

    @Transactional(readOnly = true)
    public Client get(int id) throws ApiException {
        Client client = dao.select(id);
        if (Objects.isNull(client)) {
            throw new ApiException("Client with id " + id + " not found");
        }
        return client;
    }

    private void checkValid(Client client) throws ApiException {
        // Null and empty checks
        if (StringUtils.isEmpty(client.getName())) {
            throw new ApiException("Client name cannot be empty");
        }
        if (StringUtils.isEmpty(client.getPhone())) {
            throw new ApiException("Client phone cannot be empty");
        }
        if (StringUtils.isEmpty(client.getEmail())) {
            throw new ApiException("Client email cannot be empty");
        }

        // Format and length checks
        if (client.getName().length() > 256) {
            throw new ApiException("Client name cannot exceed 256 characters");
        }
        if (!client.getPhone().matches("\\d{10}")) {
            throw new ApiException("Phone must be exactly 10 digits");
        }
        if (client.getEmail().length() > 256 || !client.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ApiException("Invalid email format or length");
        }
    }
}
