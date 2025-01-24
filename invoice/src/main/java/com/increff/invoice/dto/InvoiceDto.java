package com.increff.invoice.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.increff.invoice.service.InvoiceGenerator;
import com.increff.commons.model.OrderData;
import com.increff.commons.exception.ApiException;
import java.util.Base64;

@Component
public class InvoiceDto {
    
    @Autowired
    private InvoiceGenerator invoiceGenerator;

    public String generateInvoice(OrderData order) throws ApiException {
        try {
            byte[] pdfBytes = invoiceGenerator.generatePDF(order);
            return Base64.getEncoder().encodeToString(pdfBytes);
        } catch (Exception e) {
            throw new ApiException("Error generating invoice: " + e.getMessage());
        }
    }
} 