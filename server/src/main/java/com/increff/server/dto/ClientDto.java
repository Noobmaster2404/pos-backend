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
        checkValid(form);
        normalize(form);
        Client client = ConversionClass.convert(form);
        clientFlow.add(client);
    }

    public List<ClientData> getAll() throws ApiException {
        return clientFlow.getAll()
                .stream()
                .map(ConversionClass::convert)
                .collect(Collectors.toList());
    }

    public ClientData get(Integer clientId) throws ApiException {
        return ConversionClass.convert(clientFlow.get(clientId));
    }

    public ClientData update(Integer clientId, ClientForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        Client client = ConversionClass.convert(form);
        Client updatedClient = clientFlow.update(clientId, client);
        return ConversionClass.convert(updatedClient);
    }

    @Override
    protected String getPrefix() {
        return "Client: ";
    }
}