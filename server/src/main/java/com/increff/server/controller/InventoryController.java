package com.increff.server.controller;

import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import com.increff.commons.model.InventoryData;
import com.increff.commons.model.InventoryForm;
import com.increff.commons.exception.ApiException;
import com.increff.server.dto.InventoryDto;

@Api
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryDto dto;

    @RequestMapping(method = RequestMethod.POST)
    public void add(@Valid @RequestBody InventoryForm form) throws ApiException {
        dto.add(form);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<InventoryData> getAll() throws ApiException {
        return dto.getAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{inventoryId}")
    public InventoryData get(@PathVariable Integer inventoryId) throws ApiException {
        return dto.get(inventoryId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{inventoryId}")
    public void update(@PathVariable Integer inventoryId, @Valid @RequestBody InventoryForm form) throws ApiException {
        dto.update(inventoryId, form);
    }
}
