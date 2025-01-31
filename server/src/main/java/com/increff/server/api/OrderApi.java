package com.increff.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.increff.server.dao.OrderDao;
import com.increff.server.entity.Order;
// import com.increff.server.entity.OrderItem;
import com.increff.commons.exception.ApiException;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

@Service
public class OrderApi {

    @Autowired
    private OrderDao orderDao;

    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Order order) throws ApiException {
        // validateOrder(order);
        order.setOrderTime(ZonedDateTime.now(ZoneOffset.UTC));
        orderDao.insert(order);
        return order;
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Integer orderId) throws ApiException {
        Order order = orderDao.selectWithItems(orderId);
        if (Objects.isNull(order)) {
            throw new ApiException("Order with ID " + orderId + " not found");
        }
        return order;
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByDateRange(ZonedDateTime startDate, ZonedDateTime endDate) {
        return orderDao.selectByDateRange(startDate, endDate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateInvoicePath(Integer orderId, String invoicePath) throws ApiException {
        Order order = getOrderById(orderId);
        order.setInvoicePath(invoicePath);
        orderDao.update(order);
    }

    public List<Order> getAllOrders() throws ApiException {
        return orderDao.selectAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public Order updateOrder(Order order) throws ApiException {
        if (Objects.isNull(order.getOrderId())) {
            throw new ApiException("Order ID cannot be null");
        }
        
        Order existingOrder = getOrderById(order.getOrderId());
        existingOrder.setInvoicePath(order.getInvoicePath());
        orderDao.update(existingOrder);
        return existingOrder;
    }

    // private void validateOrder(Order order) throws ApiException {
    //     if (Objects.isNull(order.getOrderItems()) || order.getOrderItems().isEmpty()) {
    //         throw new ApiException("Order must contain at least one item");
    //     }

    //     double total = 0.0;
    //     for (OrderItem item : order.getOrderItems()) {
    //         if (Objects.isNull(item.getProduct())) {
    //             throw new ApiException("Product cannot be null");
    //         }
    //         if (Objects.isNull(item.getQuantity()) || item.getQuantity() <= 0) {
    //             throw new ApiException("Invalid quantity for product: " + item.getProduct().getProductId());
    //         }
    //         if (Objects.isNull(item.getSellingPrice()) || item.getSellingPrice() < 0) {
    //             throw new ApiException("Invalid selling price for product: " + item.getProduct().getProductId());
    //         }
    //         total += item.getQuantity() * item.getSellingPrice();
    //     }
    //     order.setOrderTotal(total);
    // }
} 