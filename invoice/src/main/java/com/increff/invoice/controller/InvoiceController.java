package com.increff.invoice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.increff.commons.model.OrderData;
import com.increff.commons.exception.ApiException;
import com.increff.invoice.dto.InvoiceDto;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceDto dto;

    @RequestMapping(method = RequestMethod.GET, value = "/generate")
    public String generateInvoice(@RequestBody OrderData order) throws ApiException {
        return dto.generateInvoice(order);
    }
} 