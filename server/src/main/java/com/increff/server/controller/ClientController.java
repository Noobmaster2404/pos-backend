package com.increff.server.controller;

import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import com.increff.commons.model.ClientData;
import com.increff.commons.model.ClientForm;
import com.increff.commons.exception.ApiException;
import com.increff.server.dto.ClientDto;

@Api
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientDto dto;

    @RequestMapping(method = RequestMethod.POST)
    public void add(@Valid @RequestBody ClientForm form) throws ApiException {
        //@RequestBody is used to bind the request body to the form object
        //@Valid is used to check the annotations in the form object
        dto.add(form);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ClientData> getAll() throws ApiException {
        return dto.getAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ClientData get(@PathVariable int id) throws ApiException {
        return dto.get(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public void update(@PathVariable int id, @Valid @RequestBody ClientForm form) throws ApiException {
        dto.update(id, form);
    }
}
