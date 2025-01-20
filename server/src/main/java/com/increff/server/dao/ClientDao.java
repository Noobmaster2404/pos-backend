package com.increff.server.dao;

import org.springframework.stereotype.Repository;
import com.increff.server.entity.Client;

@Repository
public class ClientDao extends AbstractDao<Client> {

    public ClientDao() {
        super(Client.class);
    }
}
