package com.increff.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.increff.server.dao.OrderItemDao;
import com.increff.server.entity.OrderItem;
import com.increff.commons.exception.ApiException;

import java.util.List;

@Service
public class OrderItemApi {

    @Autowired
    private OrderItemDao dao;

    @Transactional(rollbackFor = Exception.class)
    public void insertOrderItems(List<OrderItem> orderItems) throws ApiException {
        for (OrderItem item : orderItems) {
            if (item.getQuantity() <= 0) {
                throw new ApiException("Quantity must be positive for product: " + item.getProduct().getBarcode());
            }
            if (item.getSellingPrice() < 0) {
                throw new ApiException("Selling price cannot be negative for product: " + item.getProduct().getBarcode());
            }
            dao.insert(item);
        }
    }

    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
        return dao.selectByOrderId(orderId);
    }
} 