package com.increff.server.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

import com.increff.server.entity.Client;
import com.increff.commons.model.ClientForm;
import com.increff.commons.model.ClientData;
import com.increff.server.flow.ClientFlow;
import com.increff.commons.exception.ApiException;

@Component
public class ClientDto extends AbstractDto {
    
    @Autowired
    private ClientFlow clientFlow;

    public ClientData addClient(ClientForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        Client client = ConversionClass.convertToClient(form);
        return ConversionClass.convertToClientData(clientFlow.addClient(client));
    }

    public List<ClientData> getAllClients() throws ApiException {
        return clientFlow.getAllClients()
                .stream()
                .map(client -> {
                    try {
                        return ConversionClass.convertToClientData(client);
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public ClientData getClientById(Integer clientId) throws ApiException {
        return ConversionClass.convertToClientData(clientFlow.getClientById(clientId));
    }

    public ClientData updateClientById(Integer clientId, ClientForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        Client client = ConversionClass.convertToClient(form);
        Client updatedClient = clientFlow.updateClientById(clientId, client);
        return ConversionClass.convertToClientData(updatedClient);
    }

    public List<ClientData> getClientsByName(String namePrefix) throws ApiException {
        return clientFlow.getClientsByName(namePrefix)
                .stream()
                .map(client -> {
                    try {
                        return ConversionClass.convertToClientData(client);
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    protected String getPrefix() {
        return "Client: ";
    }
}