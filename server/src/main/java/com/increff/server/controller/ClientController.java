package com.increff.server.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.increff.commons.model.ClientData;
import com.increff.commons.model.ClientForm;
import com.increff.server.api.ClientApi;
import com.increff.server.entity.Client;
import com.increff.commons.exception.ApiException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientApi api;

    @ApiOperation(value = "Create a new client")
    @PostMapping("/clients")
    public void add(@RequestBody ClientForm form) throws ApiException {
        Client p = convert(form);
        api.add(p);
    }

    @ApiOperation(value = "Get all clients")
    @GetMapping("/clients")
    public List<ClientData> getAll() {
        List<Client> list = api.getAll();
        List<ClientData> list2 = new ArrayList<>();
        for (Client p : list) {
            list2.add(convert(p));
        }
        return list2;
    }

    @ApiOperation(value = "Get client by ID")
    @GetMapping("/client/{id}")
    public ClientData get(@PathVariable int id) throws ApiException {
        Client client = api.get(id);
        if (client == null) {
            throw new ApiException("Client with id " + id + " not found");
        }
        return convert(client);
    }

    @ApiOperation(value = "Update a client")
    @PutMapping("/client/{id}")
    public void update(@PathVariable int id, @RequestBody ClientForm form) throws ApiException {
        Client p = convert(form);
        api.update(id, p);
    }

    private static ClientData convert(Client p) {
        ClientData d = new ClientData();
        d.setId(p.getId());
        d.setName(p.getName());
        d.setContact(p.getContact());
        d.setEnabled(p.isEnabled());
        return d;
    }

    private static Client convert(ClientForm f) {
        Client p = new Client();
        p.setName(f.getName());
        p.setContact(f.getContact());
        p.setEnabled(f.isEnabled());
        return p;
    }
}
