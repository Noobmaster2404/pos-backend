package com.increff.server.dto;

import com.increff.server.entity.Client;
import com.increff.commons.model.ClientForm;
import com.increff.commons.model.ClientData;
import com.increff.server.flow.ClientFlow;
import com.increff.commons.exception.ApiException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientDto extends AbstractDto {
    
    @Autowired
    private ClientFlow clientFlow;

    public void add(ClientForm form) throws ApiException {
        normalize(form);
        
        Client client = convert(form);
        clientFlow.add(client);
    }

    public List<ClientData> getAll() throws ApiException {
        return clientFlow.getAll()
                .stream()
                .map(this::convertToData)
                .collect(Collectors.toList());
    }

    public ClientData get(Integer clientId) throws ApiException {
        return convertToData(clientFlow.get(clientId));
    }

    public void update(Integer clientId, ClientForm form) throws ApiException {
        normalize(form);
        
        Client client = convert(form);
        clientFlow.update(clientId, client);
    }

    private Client convert(ClientForm form) {
        Client client = new Client();
        client.setClientName(form.getClientName());
        client.setPhone(form.getPhone());
        client.setEmail(form.getEmail());
        client.setEnabled(form.getEnabled());
        return client;
    }

    private ClientData convertToData(Client client) {
        ClientData data = new ClientData();
        data.setClientId(client.getClientId());
        data.setClientName(client.getClientName());
        data.setPhone(client.getPhone());
        data.setEmail(client.getEmail());
        data.setEnabled(client.getEnabled());
        return data;
    }

    @Override
    protected String getPrefix() {
        return "Client: ";
    }
}