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

    public ClientData addClient(ClientForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        Client client = ConversionClass.convert(form);
        return ConversionClass.convert(clientFlow.addClient(client));
    }

    public List<ClientData> getAllClients() throws ApiException {
        return clientFlow.getAllClients()
                .stream()
                .map(ConversionClass::convert)
                .collect(Collectors.toList());
    }

    public ClientData getClientById(Integer clientId) throws ApiException {
        return ConversionClass.convert(clientFlow.getClientById(clientId));
    }

    public ClientData updateClientById(Integer clientId, ClientForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        Client client = ConversionClass.convert(form);
        Client updatedClient = clientFlow.updateClientById(clientId, client);
        return ConversionClass.convert(updatedClient);
    }

    @Override
    protected String getPrefix() {
        return "Client: ";
    }
}