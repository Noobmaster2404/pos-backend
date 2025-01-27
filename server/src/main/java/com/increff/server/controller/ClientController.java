package com.increff.server.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.increff.commons.model.ClientData;
import com.increff.commons.model.ClientForm;
import com.increff.commons.exception.ApiException;
import com.increff.server.dto.ClientDto;

@Api(tags = "Client Management")
@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientDto dto;

    @ApiOperation(value = "Add a new client")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public ClientData addClient(@RequestBody ClientForm form) throws ApiException {
        //@RequestBody is used to bind the request body to the form object
        return dto.addClient(form);
    }

    @ApiOperation(value = "Get all clients")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public List<ClientData> getAllClients() throws ApiException {
        List<ClientData> clients = dto.getAllClients();
        return clients;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/{clientId}")
    public ClientData getClientById(@PathVariable Integer clientId) throws ApiException {
        ClientData client = dto.getClientById(clientId);
        return client;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, value = "/{clientId}")
    public ClientData updateClientById(@PathVariable Integer clientId, @RequestBody ClientForm form) throws ApiException {
        return dto.updateClientById(clientId, form);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/search")
    public List<ClientData> getClientsByName(@RequestParam String name) throws ApiException {
        List<ClientData> results = dto.getClientsByName(name);
        return results;
    }
}
