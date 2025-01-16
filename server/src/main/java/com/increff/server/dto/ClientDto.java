package com.increff.server.dto;

import javax.validation.constraints.NotNull;
import com.increff.server.entity.Client;
import com.increff.commons.model.ClientForm;
import com.increff.commons.model.ClientData;
import com.increff.commons.exception.ApiException;

public class ClientDto extends AbstractDto {
    
    @NotNull
    private String name;
    private String contact;
    private boolean enabled = true;

    // Convert from Form to Entity
    public static Client fromForm(ClientForm form) throws ApiException {
        ClientDto dto = new ClientDto();
        dto.setName(form.getName());
        dto.setContact(form.getContact());
        dto.setEnabled(form.isEnabled());
        
        dto.normalize();
        dto.validate();
        
        Client client = new Client();
        client.setName(dto.getName());
        client.setContact(dto.getContact());
        client.setEnabled(dto.isEnabled());
        return client;
    }

    // Convert from Entity to Data
    public static ClientData toData(Client client) {
        ClientData data = new ClientData();
        data.setId(client.getId());
        data.setName(client.getName());
        data.setContact(client.getContact());
        data.setEnabled(client.isEnabled());
        return data;
    }

    @Override
    protected String getPrefix() {
        return "Client ";
    }

    // Standard getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}