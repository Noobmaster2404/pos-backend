package com.increff.server.api;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.increff.commons.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.server.dao.ClientDao;
import com.increff.server.entity.Client;

@Service
public class ClientApi {

    @Autowired
    private ClientDao dao;

    @Transactional(rollbackFor = ApiException.class)
    public void add(Client p) throws ApiException {
        if (p.getName() == null || p.getName().isEmpty()) {
            throw new ApiException("Client name cannot be empty");
        }
        Client existing = dao.selectByName(p.getName());
        if (existing != null) {
            throw new ApiException("Client with name '" + p.getName() + "' already exists");
        }
        dao.insert(p);
    }

    @Transactional
    public List<Client> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackFor = ApiException.class)
    public void update(int id, Client p) throws ApiException {
        Client existing = dao.select(id);
        if (existing == null) {
            throw new ApiException("Client with given ID does not exist");
        }
        
        // Check if name is being changed
        if (!existing.getName().equals(p.getName())) {
            // Check if new name already exists
            Client duplicateCheck = dao.selectByName(p.getName());
            if (duplicateCheck != null) {
                throw new ApiException("Client with name '" + p.getName() + "' already exists");
            }
        }
        
        existing.setName(p.getName());
        existing.setContact(p.getContact());
        existing.setEnabled(p.isEnabled());
        dao.update(existing);
    }

    @Transactional(readOnly = true)
    public Client get(int id) throws ApiException {
        Client client = dao.select(id);
        if (client == null) {
            throw new ApiException("Client with id " + id + " not found");
        }
        return client;
    }
}
