package com.increff.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.Resource;

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
        return dto.downloadInvoice(orderId);
    }

    @ApiOperation(value = "Generate invoice for order")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST, value = "/{orderId}/generate-invoice")
    public String generateInvoice(@PathVariable Integer orderId) throws ApiException {
        return dto.generateInvoice(orderId);
    }
} 