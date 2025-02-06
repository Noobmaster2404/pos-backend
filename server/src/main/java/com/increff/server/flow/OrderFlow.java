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
import java.util.stream.Collectors;
import java.util.Map;
import java.util.ArrayList;

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
    private InventoryFlow inventoryFlow;

    @Autowired
    private InvoiceGenerator invoiceGenerator;

    @Value("${invoice.storage.path}")
    private String invoiceStoragePath;

    public Order addOrder(Order order) throws ApiException {
        // Get all product IDs
        List<Integer> productIds = order.getOrderItems().stream()
            .map(item -> item.getProduct().getProductId())
            .collect(Collectors.toList());
            
        // Get all inventories in a single call
        List<Inventory> inventories = inventoryFlow.getInventoriesByProductIds(productIds);
        Map<Integer, Inventory> productIdToInventory = inventories.stream()
            .collect(Collectors.toMap(
                inventory -> inventory.getProduct().getProductId(),
                inventory -> inventory
            ));
        
        // Validate and update inventories
        for (OrderItem item : order.getOrderItems()) {
            Inventory inventory = productIdToInventory.get(item.getProduct().getProductId());
            if (inventory.getQuantity() < item.getQuantity()) {
                throw new ApiException("Insufficient inventory for product: " + item.getProduct().getBarcode());
            }
            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
            
            // Set the order reference for each item
            item.setOrder(order);
        }
        
        // Bulk update inventories
        inventoryFlow.updateInventories(new ArrayList<>(productIdToInventory.values()));
        
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
    public List<Order> getOrdersByDateRange(ZonedDateTime startDate, ZonedDateTime endDate, Integer page) throws ApiException {
        if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
            throw new ApiException("Start date and end date cannot be null");
        }
        startDate = TimeZoneUtil.toUTC(startDate);
        endDate = TimeZoneUtil.toUTC(endDate);
        //TODO: move this to api layer and documnstion of isAfter
        if (startDate.isAfter(endDate)) {
            throw new ApiException("Start date cannot be after end date");
        }
        return orderApi.getOrdersByDateRange(startDate, endDate, page);
    }
} 