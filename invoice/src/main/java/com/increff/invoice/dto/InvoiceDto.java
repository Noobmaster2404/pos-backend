package com.increff.invoice.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.increff.invoice.service.InvoiceGenerator;
import com.increff.commons.model.OrderData;
import com.increff.commons.exception.ApiException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class InvoiceDto {
    
    @Autowired
    private InvoiceGenerator invoiceGenerator;

    @Value("${invoice.storage.path}")
    private String invoiceStoragePath;

    public String generateInvoice(OrderData order) throws ApiException {
        try {
            byte[] pdfBytes = invoiceGenerator.generatePDF(order);
            Files.createDirectories(Paths.get(invoiceStoragePath));
            String fileName = "invoice_" + order.getOrderId() + ".pdf";
            Path filePath = Paths.get(invoiceStoragePath, fileName);
            Files.write(filePath, pdfBytes);
            
            return filePath.toString();
        } catch (Exception e) {
            throw new ApiException("Error generating invoice: " + e.getMessage());
        }
    }
} 