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
import java.util.Objects;

@Component
public class ClientDto extends AbstractDto {
    
    @Autowired
    private ClientFlow clientFlow;

    public void add(ClientForm form) throws ApiException {
        normalize(form);
        validate(form);
        
        Client client = convert(form);
        clientFlow.add(client);
    }

    public List<ClientData> getAll() {
        return clientFlow.getAll()
                .stream()
                .map(this::convertToData)
                .collect(Collectors.toList());
    }

    public ClientData get(int id) throws ApiException {
        return convertToData(clientFlow.get(id));
    }

    public void update(int id, ClientForm form) throws ApiException {
        normalize(form);
        validate(form);
        
        Client client = convert(form);
        clientFlow.update(id, client);
    }

    private Client convert(ClientForm form) {
        Client client = new Client();
        client.setName(form.getName());
        client.setContact(form.getContact());
        client.setEnabled(form.isEnabled());
        return client;
    }

    private ClientData convertToData(Client client) {
        ClientData data = new ClientData();
        data.setId(client.getId());
        data.setName(client.getName());
        data.setContact(client.getContact());
        data.setEnabled(client.isEnabled());
        return data;
    }

    @Override
    protected void validate(ClientForm form) throws ApiException {
        if (Objects.isNull(form.getName()) || Objects.isNull(form.getContact())) {
            throw new ApiException("Client name and contact cannot be empty");
        }
    }

    @Override
    protected String getPrefix() {
        return "Client: ";
    }
}