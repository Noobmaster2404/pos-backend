package com.increff.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.increff.commons.model.SalesReportForm;
import com.increff.commons.model.SalesReportData;
import com.increff.server.dto.SalesReportDto;
import com.increff.commons.exception.ApiException;
import com.increff.commons.model.ClientData;
import com.increff.commons.model.DailySalesData;
import com.increff.commons.model.DailySalesForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Report Management")
@RestController
@RequestMapping("/reports")
public class SalesReportController {
    
    @Autowired
    private SalesReportDto dto;
    
    @ApiOperation(value = "Get Sales Report")
    @RequestMapping(path = "/sales", method = RequestMethod.POST)
    public List<SalesReportData> getSalesReport(@RequestBody SalesReportForm form) throws ApiException {
        return dto.getSalesReport(form);
    }

    @ApiOperation(value = "Search clients for sales report")
    @RequestMapping(path = "/sales/clients/search", method = RequestMethod.GET)
    public List<ClientData> getClientsByName(@RequestParam String clientNamePrefix) throws ApiException {
        return dto.getClientsByName(clientNamePrefix);
    }

    @ApiOperation(value = "Get Daily Sales Report")
    @RequestMapping(path = "/daily-sales", method = RequestMethod.GET)
    public List<DailySalesData> getDailySalesReport(@RequestBody DailySalesForm form) throws ApiException {
        return dto.getDailySalesReport(form);
    }
} 