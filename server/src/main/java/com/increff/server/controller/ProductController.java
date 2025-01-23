package com.increff.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.increff.commons.model.ProductData;
import com.increff.commons.model.ProductForm;
import com.increff.commons.exception.ApiException;
import com.increff.server.dto.ProductDto;

@Api(tags = "Product Management")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductDto dto;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public ProductData addProduct(@RequestBody ProductForm form) throws ApiException {
        return dto.addProduct(form);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public List<ProductData> getAllProducts() throws ApiException {
        return dto.getAllProducts();
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/{productId}")
    public ProductData getProductById(@PathVariable Integer productId) throws ApiException {
        return dto.getProductById(productId);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, value = "/{productId}")
    public ProductData updateProductById(@PathVariable Integer productId, @RequestBody ProductForm form) throws ApiException {
        return dto.updateProductById(productId, form);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, value = "/bulk")
    @ApiOperation(value = "Bulk create products from JSON data")
    public List<ProductData> bulkAddProducts(@RequestBody List<ProductForm> forms) throws ApiException {
        return dto.bulkAddProducts(forms);
    }

    // @RequestMapping(method = RequestMethod.GET, value = "/client/{clientId}")
    // public List<ProductData> getByClient(@PathVariable Integer clientId) throws ApiException {
    //     return dto.getByClient(clientId);
    // }
}
