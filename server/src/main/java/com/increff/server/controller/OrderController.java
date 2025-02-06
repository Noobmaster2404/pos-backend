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
import java.time.ZonedDateTime;
import java.util.List;
//TODO: Use intelliJ for imports

import com.increff.commons.model.OrderData;
import com.increff.commons.model.OrderForm;
import com.increff.commons.exception.ApiException;
import com.increff.server.dto.OrderDto;
import com.increff.commons.util.TimeZoneUtil;
import com.increff.commons.model.OrderSearchForm;

@Api(tags = "Order Management")
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderDto dto;

    @ApiOperation(value = "Create a new order")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public OrderData createOrder(@RequestBody OrderForm form) throws ApiException {
        return dto.createOrder(form);
    }

    @ApiOperation(value = "Get order by ID")
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET, value = "/{orderId}")
    public OrderData getOrder(@PathVariable Integer orderId) throws ApiException {
        return dto.getOrder(orderId);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST, value = "/search")
    @ApiOperation(value = "Get orders by date range")
    public List<OrderData> getOrdersByDateRange(@RequestBody OrderSearchForm form) throws ApiException {
        ZonedDateTime startDate = TimeZoneUtil.toUTC(form.getStartDate());
        ZonedDateTime endDate = TimeZoneUtil.toUTC(form.getEndDate());
        return dto.getOrdersByDateRange(startDate, endDate);
    }
    //TODO: remove logic above

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