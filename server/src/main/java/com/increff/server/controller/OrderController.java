package com.increff.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import java.net.MalformedURLException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.time.ZonedDateTime;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.increff.commons.model.OrderData;
import com.increff.commons.model.OrderForm;
import com.increff.commons.exception.ApiException;
import com.increff.server.dto.OrderDto;

@Api(tags = "Order Management")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderDto dto;

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Create a new order")
    public OrderData createOrder(@RequestBody OrderForm form) throws ApiException {
        return dto.createOrder(form);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/{orderId}")
    @ApiOperation(value = "Get order by ID")
    public OrderData getOrder(@PathVariable Integer orderId) throws ApiException {
        return dto.getOrder(orderId);
    }

    // @ResponseStatus(HttpStatus.OK)
    // @RequestMapping(method = RequestMethod.GET)
    // @ApiOperation(value = "Get orders by date range")
    // public List<OrderData> getOrdersByDateRange(
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
    //         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate) 
    //         throws ApiException {
    //     return dto.getOrdersByDateRange(startDate, endDate);
    // }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/{orderId}/invoice")
    @ApiOperation(value = "Download order invoice")
    public ResponseEntity<Resource> downloadInvoice(@PathVariable Integer orderId) throws ApiException {
        OrderData order = dto.getOrder(orderId);
        
        if (order.getInvoicePath() == null) {
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
    }
} 