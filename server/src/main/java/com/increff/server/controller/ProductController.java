package com.increff.server.controller;

import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.increff.commons.model.ProductData;
import com.increff.commons.model.ProductForm;
import com.increff.commons.exception.ApiException;
import com.increff.server.dto.ProductDto;

@Api
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductDto dto;

    @RequestMapping(method = RequestMethod.POST)
    public void add(@Valid @RequestBody ProductForm form) throws ApiException {
        dto.add(form);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<ProductData> getAll() throws ApiException {
        return dto.getAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{productId}")
    public ProductData get(@PathVariable Integer productId) throws ApiException {
        return dto.get(productId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{productId}")
    public ProductData update(@PathVariable Integer productId, @Valid @RequestBody ProductForm form) throws ApiException {
        return dto.update(productId, form);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/bulk")
    @ApiOperation(value = "Bulk create products from JSON data")
    public void bulkUpload(@Valid @RequestBody List<ProductForm> forms) throws ApiException {
        dto.bulkAdd(forms);
    }

    // @RequestMapping(method = RequestMethod.GET, value = "/client/{clientId}")
    // public List<ProductData> getByClient(@PathVariable Integer clientId) throws ApiException {
    //     return dto.getByClient(clientId);
    // }
}
