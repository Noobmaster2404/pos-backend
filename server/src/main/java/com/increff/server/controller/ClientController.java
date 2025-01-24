package com.increff.server.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import com.increff.commons.model.ClientData;
import com.increff.commons.model.ClientForm;
import com.increff.commons.exception.ApiException;
import com.increff.server.dto.ClientDto;

@Api(tags = "Client Management")
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private ClientDto dto;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public ClientData addClient(@RequestBody ClientForm form) throws ApiException {
        //@RequestBody is used to bind the request body to the form object
        return dto.addClient(form);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public List<ClientData> getAllClients() throws ApiException {
        logger.info("Getting all clients");
        List<ClientData> clients = dto.getAllClients();
        logger.info("Found {} clients", clients.size());
        return clients;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/{clientId}")
    public ClientData getClientById(@PathVariable Integer clientId) throws ApiException {
        logger.info("Getting client with ID: {}", clientId);
        ClientData client = dto.getClientById(clientId);
        logger.info("Found client: {}", client.getClientName());
        return client;
    }

    //can also do this
    // @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    // public ClientData get(@PathVariable("id") Integer clientId) throws ApiException {
    //     return dto.get(clientId);
    // }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, value = "/{clientId}")
    public ClientData updateClientById(@PathVariable Integer clientId, @RequestBody ClientForm form) throws ApiException {
        return dto.updateClientById(clientId, form);
    }
}
