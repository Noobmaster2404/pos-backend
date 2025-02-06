package com.increff.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.MalformedURLException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import com.increff.commons.model.OrderData;
import com.increff.commons.model.OrderForm;
import com.increff.commons.exception.ApiException;
import com.increff.server.dto.OrderDto;
import com.increff.commons.model.OrderSearchForm;
import com.increff.commons.model.PaginatedData;

@Api(tags = "Order Management")
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderDto dto;

    //dummy
    @ApiOperation(value = "Get order by ID")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/{orderId}")
    public OrderData getOrder(@PathVariable Integer orderId) throws ApiException {
        return dto.getOrder(orderId);
    }

    @ApiOperation(value = "Create a new order")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public OrderData addOrder(@RequestBody OrderForm form) throws ApiException {
        return dto.addOrder(form);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST, value = "/search")
    @ApiOperation(value = "Get orders by date range")
    public PaginatedData<OrderData> getOrdersByDateRange(
        @RequestBody OrderSearchForm form,
        @RequestParam(defaultValue = "0") Integer page) throws ApiException {
        return dto.getOrdersByDateRange(form, page);
    }

    @ApiOperation(value = "Download order invoice")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/{orderId}/invoice")
    public ResponseEntity<Resource> downloadInvoice(@PathVariable Integer orderId) throws ApiException {
        OrderData order = dto.getOrder(orderId);
        
        if (Objects.isNull(order.getInvoicePath())) {
            throw new ApiException("Invoice not yet generated for order: " + orderId);
        }

        try {
            Path path = Paths.get(order.getInvoicePath());
            Resource resource = new UrlResource(path.toUri());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"invoice_" + orderId + ".pdf\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new ApiException("Error downloading invoice: " + e.getMessage());
        }
        //TODO: Dto, clean and send as base_64
    }

    @ApiOperation(value = "Generate invoice for order")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST, value = "/{orderId}/generate-invoice")
    public void generateInvoice(@PathVariable Integer orderId) throws ApiException {
        OrderData order = dto.getOrder(orderId);
        dto.generateInvoice(order);
        //Both in dto
    }
} 