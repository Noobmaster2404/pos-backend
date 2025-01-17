package com.increff.server.controller;

// import java.util.ArrayList;
import java.util.List;
// import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.increff.commons.model.ClientData;
import com.increff.commons.model.ClientForm;
// import com.increff.server.api.ClientApi;
// import com.increff.server.entity.Client;
import com.increff.commons.exception.ApiException;
import com.increff.server.dto.ClientDto;

import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientDto dto;

    @PostMapping
    public void add(@RequestBody ClientForm form) throws ApiException {
        //@RequestBody is used to bind the request body to the form object
        dto.add(form);
    }

    @GetMapping
    public List<ClientData> getAll() {
        return dto.getAll();
    }

    @GetMapping("/{id}")
    public ClientData get(@PathVariable int id) throws ApiException {
        return dto.get(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody ClientForm form) throws ApiException {
        dto.update(id, form);
    }
}
