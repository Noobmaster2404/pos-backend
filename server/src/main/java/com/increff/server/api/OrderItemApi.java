package com.increff.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.increff.server.dao.OrderItemDao;
import com.increff.server.entity.OrderItem;

import java.util.List;

@Service
public class OrderItemApi {

    @Autowired
    private OrderItemDao dao;

    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItemsByOrderId(Integer orderId) {
        return dao.selectByOrderId(orderId);
    }
} 