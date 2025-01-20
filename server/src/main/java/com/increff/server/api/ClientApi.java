package com.increff.server.api;

import java.util.List;
import java.util.Objects;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.server.dao.ClientDao;
import com.increff.server.entity.Client;
import com.increff.commons.exception.ApiException;

@Service
public class ClientApi {

    @Autowired
    private ClientDao dao;

    @Transactional(rollbackFor = ApiException.class)
    public void add(Client client) throws ApiException {
        if (Objects.nonNull(client.getName())) {
            throw new ApiException("Client name cannot be empty");
        }
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
        Client existingClient = dao.select(id);
        if (Objects.isNull(existingClient)) {
            throw new ApiException("Client with given ID does not exist");
        }
        
        // Check if name is being changed
        if (!existingClient.getName().equals(client.getName())) {
            // Check if new name already exists
            Client duplicateCheck = dao.selectByName(client.getName());
            if (Objects.nonNull(duplicateCheck)) {
                throw new ApiException("Client with name '" + client.getName() + "' already exists");
            }
        }
        //todo make validateUpdate method
        
        existingClient.setName(client.getName());
        existingClient.setContact(client.getContact());
        existingClient.setEnabled(client.isEnabled());
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
}
