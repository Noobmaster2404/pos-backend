package com.increff.server.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

import com.increff.server.entity.Client;
import com.increff.commons.model.ClientForm;
import com.increff.commons.model.ClientData;
import com.increff.server.flow.ClientFlow;
import com.increff.commons.exception.ApiException;

@Component
public class ClientDto extends AbstractDto {
    
    @Autowired
    private ClientFlow clientFlow;

    public List<ClientData> getClientsByName(String namePrefix) throws ApiException {
        return ConversionHelper.convertToClientData(clientFlow.getClientsByName(namePrefix));
    }

    public List<ClientData> getAllClients() throws ApiException {
        return ConversionHelper.convertToClientData(clientFlow.getAllClients());
    }

    public ClientData addClient(ClientForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        Client client = ConversionHelper.convertToClient(form);
        return ConversionHelper.convertToClientData(clientFlow.addClient(client));
    }

    public ClientData updateClientById(Integer clientId, ClientForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        Client client = ConversionHelper.convertToClient(form);
        Client updatedClient = clientFlow.updateClientById(clientId, client);
        return ConversionHelper.convertToClientData(updatedClient);
    }
}