package com.increff.server.dto;

import javax.validation.constraints.NotNull;
import com.increff.server.entity.Client;
import com.increff.commons.model.ClientForm;
import com.increff.commons.model.ClientData;
import com.increff.server.api.ClientApi;
import com.increff.commons.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientDto extends AbstractDto {
    
    @Autowired
    private ClientApi api;

    @NotNull
    private String name;
    private String contact;
    private boolean enabled = true;

    public void add(ClientForm form) throws ApiException {
        setName(form.getName());
        setContact(form.getContact());
        setEnabled(form.isEnabled());
        
        normalize();  // From AbstractDto
        validate();   // From AbstractDto
        
        Client client = convert();
        api.add(client);
    }

    public List<ClientData> getAll() {
        return api.getAll().stream()
                 .map(this::convertToData)
                 .collect(Collectors.toList());
    }

    public ClientData get(int id) throws ApiException {
        return convertToData(api.get(id));
    }

    public void update(int id, ClientForm form) throws ApiException {
        setName(form.getName());
        setContact(form.getContact());
        setEnabled(form.isEnabled());
        
        normalize();  // From AbstractDto
        validate();   // From AbstractDto
        
        Client client = convert();
        api.update(id, client);
    }

    private Client convert() {
        Client client = new Client();
        client.setName(getName());
        client.setContact(getContact());
        client.setEnabled(isEnabled());
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
    protected void validate() throws ApiException {
        if (getName() == null || getName().isEmpty()) {
            throw new ApiException("Client name cannot be empty");
        }
    }

    @Override
    protected String getPrefix() {
        return "Client: ";
    }

    // Getters and setters
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