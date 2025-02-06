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
import com.increff.commons.model.PaginatedData;

@Api(tags = "Inventory Management")
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryDto dto;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Add a new inventory item")
    public InventoryData addInventory(@RequestBody InventoryForm form) throws ApiException {
        return dto.addInventory(form);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "Get all inventory items")
    public PaginatedData<InventoryData> getAllInventory(
            @RequestParam(defaultValue = "0") Integer page) throws ApiException {
        return dto.getAllInventory(page);
    }

    //removed endpoint
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/search")
    @ApiOperation(value = "Get inventory by product ID")
    public InventoryData getInventoryById(@RequestParam Integer productId) throws ApiException {
        return dto.getInventoryById(productId);
    }
    
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/search-barcode")
    @ApiOperation(value = "Get inventory by barcode")
    public InventoryData getInventoryByBarcode(@RequestParam String barcode) throws ApiException {
        return dto.getInventoryByBarcode(barcode);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, value = "/{inventoryId}")
    @ApiOperation(value = "Update inventory by ID")
    public InventoryData updateInventoryById(@PathVariable Integer inventoryId, @RequestBody InventoryForm form) throws ApiException {
        return dto.updateInventoryById(inventoryId, form);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, value = "/bulk")
    @ApiOperation(value = "Bulk create inventory items")
    public List<InventoryData> bulkAddInventory(@RequestBody List<InventoryForm> forms) throws ApiException {
        return dto.bulkAddInventory(forms);
    }
}
