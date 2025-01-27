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
import com.increff.commons.model.ClientData;
import com.increff.server.dto.ClientDto;

@Api(tags = "Product Management")
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductDto dto;

    @Autowired
    private ClientDto clientDto;

    @ApiOperation(value = "Add a new product")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public ProductData addProduct(@RequestBody ProductForm form) throws ApiException {
        return dto.addProduct(form);
    }

    @ApiOperation(value = "Get all products")
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

    @ApiOperation(value = "Search products by name or barcode")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/search")
    public List<ProductData> getProductsByNameOrBarcode(
            @RequestParam String query,
            @RequestParam(defaultValue = "name") String searchBy) throws ApiException {
        return dto.getProductsByNameOrBarcode(query, searchBy);
    }
    //variable name is query because it can either be name or barcode

    @ApiOperation(value = "Get products by client ID")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/clients/{clientId}/products")
    //api naming follows REST hierarchy, products belong to a client
    public List<ProductData> getProductsByClientId(@PathVariable Integer clientId) throws ApiException {
        return dto.getProductsByClientId(clientId);
    }

    // For client search suggestions
    @ApiOperation(value = "Search clients by name")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/clients/search")
    public List<ClientData> searchClients(@RequestParam String query) throws ApiException {
        return clientDto.getClientsByName(query);
    }
}
