package com.increff.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.increff.server.dao.OrderItemDao;
import com.increff.server.entity.OrderItem;
import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Order;

import java.util.List;
import java.util.Objects;

@Service
public class OrderItemApi {

    @Autowired
    private OrderItemDao dao;

    @Autowired
    private OrderApi orderApi;

    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) throws ApiException {
        Order order = orderApi.getOrderById(orderId);
        if (Objects.isNull(order)) {
            throw new ApiException("Order ID does not exist: " + orderId);
        }
        
        List<OrderItem> items = dao.selectByOrderId(orderId);
        return items;
    }
} 