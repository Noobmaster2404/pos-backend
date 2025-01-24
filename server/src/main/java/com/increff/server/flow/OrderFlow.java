package com.increff.server.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.increff.server.api.OrderApi;
import com.increff.server.entity.Order;
import com.increff.server.entity.OrderItem;
import com.increff.server.entity.Product;
import com.increff.server.entity.Inventory;
import com.increff.commons.exception.ApiException;

@Service
@Transactional(rollbackFor = ApiException.class)
public class OrderFlow {

    @Autowired
    private OrderApi orderApi;

    @Autowired
    private ProductFlow productFlow;

    @Autowired
    private InventoryFlow inventoryFlow;

    @Value("${invoice.baseUrl}")
    private String invoiceBaseUrl;

    public Order createOrder(Order order) throws ApiException {
        // Validate inventory and update quantities
        for (OrderItem item : order.getOrderItems()) {
            Product product = productFlow.getProductById(item.getProduct().getProductId());
            item.setProduct(product);
            
            Inventory inventory = inventoryFlow.getInventoryById(product.getProductId());
            if (inventory.getQuantity() < item.getQuantity()) {
                throw new ApiException("Insufficient inventory for product: " + product.getBarcode());
            }
            
            // Update inventory
            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
            inventoryFlow.updateInventoryById(product.getProductId(), inventory);
        }

        // Create order
        Order savedOrder = orderApi.createOrder(order);

        // Generate invoice asynchronously
        CompletableFuture.runAsync(() -> {
            try {
                generateAndSaveInvoice(savedOrder);
            } catch (ApiException e) {
                // Log error but don't fail the order creation
                e.printStackTrace();
            }
        });

        return savedOrder;
    }

    private void generateAndSaveInvoice(Order order) throws ApiException {
        try {
            // Call invoice service
            RestTemplate restTemplate = new RestTemplate();
            String invoiceBase64 = restTemplate.postForObject(
                invoiceBaseUrl + "/api/invoice/generate",
                order,
                String.class
            );

            // Save PDF locally
            String fileName = "invoice_" + order.getOrderId() + ".pdf";
            String filePath = "invoices/" + fileName;
            byte[] decodedBytes = Base64.getDecoder().decode(invoiceBase64);
            Files.write(Paths.get(filePath), decodedBytes);

            // Update order with invoice path
            orderApi.updateInvoicePath(order.getOrderId(), filePath);
        } catch (Exception e) {
            throw new ApiException("Failed to generate invoice: " + e.getMessage());
        }
    }

    public Order getOrderById(Integer orderId) throws ApiException {
        return orderApi.getOrderById(orderId);
    }

    public List<Order> getAllOrders() throws ApiException {
        return orderApi.getAllOrders();
    }
} 