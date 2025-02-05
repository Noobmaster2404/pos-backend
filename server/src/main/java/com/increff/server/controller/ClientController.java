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

    @ApiOperation(value = "Get all clients")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public List<ClientData> getAllClients() throws ApiException {
        return dto.getAllClients();
    }

    @ApiOperation(value = "Add a new client")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public ClientData addClient(@RequestBody ClientForm form) throws ApiException {
        return dto.addClient(form);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, value = "/{clientId}")
    public ClientData updateClientById(@PathVariable Integer clientId, @RequestBody ClientForm form) throws ApiException {
        return dto.updateClientById(clientId, form);
    }
}
