package com.increff.server.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;
import java.util.Map;

import com.increff.server.api.OrderApi;
import com.increff.server.entity.Order;
import com.increff.server.entity.OrderItem;
import com.increff.commons.exception.ApiException;
import com.increff.server.api.InventoryApi;
import com.increff.server.entity.Inventory;

@Service
@Transactional(rollbackFor = Exception.class)
public class OrderFlow {

    @Autowired
    private OrderApi orderApi;

    @Autowired
    private InventoryApi inventoryApi;

    @Value("${invoice.storage.path}")
    private String invoiceStoragePath;

    public Order addOrder(Order order) throws ApiException {
        List<Integer> productIds = order.getOrderItems().stream()
            .map(item -> item.getProduct().getProductId())
            .collect(Collectors.toList());

        List<Inventory> inventories = inventoryApi.getCheckInventoriesByProductIds(productIds);
        Map<Integer, Inventory> productIdToInventory = inventories.stream()
            .collect(Collectors.toMap(
                inventory -> inventory.getProduct().getProductId(),
                inventory -> inventory
            ));

        for (OrderItem item : order.getOrderItems()) {
            Inventory inventory = productIdToInventory.get(item.getProduct().getProductId());
            if (inventory.getQuantity() < item.getQuantity()) {
                throw new ApiException("Insufficient inventory for product with barcode: " + item.getProduct().getBarcode());
            }
            inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
        }

        for (Inventory inventory : productIdToInventory.values()) {
            inventoryApi.updateInventoryById(inventory.getInventoryId(), inventory);
        }
        Order savedOrder = orderApi.addOrder(order);
        
        return savedOrder;
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
        return orderApi.getOrdersByDateRange(startDate, endDate, page);
    }
} 