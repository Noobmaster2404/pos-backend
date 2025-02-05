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
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductDto dto;

    @ApiOperation(value = "Get all products")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public List<ProductData> getAllProducts() throws ApiException {
        return dto.getAllProducts();
    }

    @ApiOperation(value = "Get products by name")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/search")
    public List<ProductData> getProductsByNamePrefix(
            @RequestParam String productName) throws ApiException {
        return dto.getProductsByNamePrefix(productName);
    }

    @ApiOperation(value = "Get products by barcode")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/search-barcode")
    public ProductData getProductByBarcode(
            @RequestParam String barcode) throws ApiException {
        return dto.getProductByBarcode(barcode);
    }

    @ApiOperation(value = "Add a new product")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public ProductData addProduct(@RequestBody ProductForm form) throws ApiException {
        return dto.addProduct(form);
    }

    @ApiOperation(value = "Update product by barcode")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT, value = "/{barcode}")
    public ProductData updateProductByBarcode(@PathVariable String barcode, @RequestBody ProductForm form) throws ApiException {
        return dto.updateProductByBarcode(barcode, form);
    }

    @ApiOperation(value = "Bulk create products from JSON data")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, value = "/bulk")
    public List<ProductData> bulkAddProducts(@RequestBody List<ProductForm> forms) throws ApiException {
        return dto.bulkAddProducts(forms);
    }

    @ApiOperation(value = "Get products by client ID")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/by-client")
    public List<ProductData> getProductsByClientId(
            @RequestParam(value = "clientId", required = true) Integer clientId) throws ApiException {
        return dto.getProductsByClientId(clientId);
    }
}
