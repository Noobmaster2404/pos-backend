package com.increff.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.increff.commons.model.ClientData;
import com.increff.commons.model.ClientForm;
import com.increff.server.api.ClientApi;
import com.increff.server.entity.Client;
import com.increff.commons.exception.ApiException;
import com.increff.server.dto.ClientDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientApi api;

    @PostMapping
    public void add(@RequestBody ClientForm form) throws ApiException {
        Client client = ClientDto.fromForm(form);
        api.add(client);
    }

    @GetMapping
    public List<ClientData> getAll() {
        return api.getAll().stream()
                 .map(ClientDto::toData)
                 .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ClientData get(@PathVariable int id) throws ApiException {
        return ClientDto.toData(api.get(id));
    }

    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody ClientForm form) throws ApiException {
        Client client = ClientDto.fromForm(form);
        api.update(id, client);
    }
}
