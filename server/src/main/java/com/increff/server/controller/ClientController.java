package com.increff.server.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.increff.commons.model.ClientData;
import com.increff.commons.model.ClientForm;
import com.increff.server.entity.Client;
import com.increff.commons.exception.ApiException;
import com.increff.server.service.ClientService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping("/api/client")
public class ClientController {

    @Autowired
    private ClientService service;

    @ApiOperation(value = "Adds a client")
    @PostMapping("/add")
    public void add(@RequestBody ClientForm form) throws ApiException {
        Client p = convert(form);
        service.add(p);
    }

    @ApiOperation(value = "Gets a list of all clients")
    @GetMapping("/get")
    public List<ClientData> getAll() {
        List<Client> list = service.getAll();
        List<ClientData> list2 = new ArrayList<>();
        for (Client p : list) {
            list2.add(convert(p));
        }
        return list2;
    }

    @ApiOperation(value = "Updates a client")
    @PutMapping("/update/{id}")
    public void update(@PathVariable int id, @RequestBody ClientForm form) throws ApiException {
        Client p = convert(form);
        service.update(id, p);
    }

    private static ClientData convert(Client p) {
        ClientData d = new ClientData();
        d.setId(p.getId());
        d.setName(p.getName());
        d.setContact(p.getContact());
        d.setEnabled(p.isEnabled());
        d.setCreatedAt(p.getCreatedAt());
        d.setUpdatedAt(p.getUpdatedAt());
        d.setVersion(p.getVersion());
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
