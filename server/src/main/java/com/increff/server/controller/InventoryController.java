package com.increff.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.increff.commons.model.InventoryData;
import com.increff.commons.model.InventoryForm;
import com.increff.commons.exception.ApiException;
import com.increff.server.dto.InventoryDto;

@Api(tags = "Inventory Management")
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryDto dto;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public void addInventory(@RequestBody InventoryForm form) throws ApiException {
        dto.addInventory(form);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public List<InventoryData> getAllInventory() throws ApiException {
        return dto.getAllInventory();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/{productId}")
    public InventoryData getInventoryById(@PathVariable Integer productId) throws ApiException {
        return dto.getInventoryById(productId);
    }
    //replace this with getInventoryByBarcode

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, value = "/{productId}")
    public InventoryData updateInventoryById(@PathVariable Integer productId, @RequestBody InventoryForm form) throws ApiException {
        return dto.updateInventoryById(productId, form);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, value = "/bulk")
    @ApiOperation(value = "Bulk create products from JSON data")
    public List<InventoryData> bulkAddInventory(@RequestBody List<InventoryForm> forms) throws ApiException {
        return dto.bulkAddInventory(forms);
    }
}
