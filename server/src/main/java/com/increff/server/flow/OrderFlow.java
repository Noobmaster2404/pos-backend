package com.increff.server.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.io.File;
import java.time.ZonedDateTime;
import java.util.Objects;
import com.increff.commons.util.TimeZoneUtil;

import com.increff.server.api.OrderApi;
import com.increff.server.entity.Order;
import com.increff.commons.model.OrderData;
import com.increff.server.entity.OrderItem;
import com.increff.server.entity.Product;
import com.increff.server.helper.ConversionHelper;
import com.increff.server.entity.Inventory;
import com.increff.commons.exception.ApiException;
import com.increff.invoice.service.InvoiceGenerator;

@Service
@Transactional(rollbackFor = Exception.class)
public class OrderFlow {

    @Autowired
    private OrderApi orderApi;

    @Autowired
    private ProductFlow productFlow;

    @Autowired
    private InventoryFlow inventoryFlow;

    @Autowired
    private InvoiceGenerator invoiceGenerator;

    @Value("${invoice.baseUrl}")
    private String invoiceBaseUrl;

    @Value("${invoice.storage.path}")
    private String invoiceStoragePath;

    public Order createOrder(Order order) throws ApiException {
        //TODO: Map of productId to product with valid checks
        //TODO: Use list api for inventpry as well
        //delay writes as much as possible
        for (OrderItem item : order.getOrderItems()) {
            Product product = productFlow.getProductByBarcode(item.getProduct().getBarcode());
            item.setProduct(product);
            //TODO: Use id not barcode
            
            Inventory inventory = inventoryFlow.getInventoryById(product.getProductId());
            if (inventory.getQuantity() < item.getQuantity()) {
                throw new ApiException("Insufficient inventory for product: " + product.getBarcode());
            }
            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
            inventoryFlow.updateInventoryById(product.getProductId(), inventory);
        }
        Order savedOrder = orderApi.createOrder(order);

        // Generate invoice synchronously for now to debug
        generateAndSaveInvoice(savedOrder);
        //TODO:publc or private?
        
        return savedOrder;
    }

    public void generateAndSaveInvoice(Order order) throws ApiException {
        try {
            // Convert Order to OrderData
            OrderData orderData = ConversionHelper.convertToOrderData(order);
            //remove orderData from here
            // Generate PDF bytes
            byte[] pdfBytes = invoiceGenerator.generatePDF(orderData);
            
            // Create directory if it doesn't exist
            File directory = new File(invoiceStoragePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Save PDF file
            String fileName = "invoice_" + order.getOrderId() + ".pdf";
            String filePath = invoiceStoragePath + File.separator + fileName;
            Files.write(Paths.get(filePath), pdfBytes);
            
            // Update order with invoice path
            order.setInvoicePath(filePath);
            orderApi.updateOrder(order);
            
        } catch (Exception e) {
            throw new ApiException("Error generating invoice: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Integer orderId) throws ApiException {
        return orderApi.getOrderById(orderId);
    }

    public List<Order> getAllOrders() throws ApiException {
        return orderApi.getAllOrders();
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByDateRange(ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException {
        if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
            throw new ApiException("Start date and end date cannot be null");
        }
        startDate = TimeZoneUtil.toUTC(startDate);
        endDate = TimeZoneUtil.toUTC(endDate);
        //TODO: move this to api layer and documnstion of isAfter
        if (startDate.isAfter(endDate)) {
            throw new ApiException("Start date cannot be after end date");
        }
        return orderApi.getOrdersByDateRange(startDate, endDate);
    }
} 