package com.increff.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.increff.server.dao.OrderDao;
import com.increff.server.entity.Order;
import com.increff.commons.exception.ApiException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackFor = Exception.class)
public class OrderApi {

    @Autowired
    private OrderDao orderDao;

    public Order addOrder(Order order) throws ApiException {
        orderDao.insert(order);
        // The order items will be automatically persisted due to CascadeType.ALL
        // No need to explicitly call orderItemApi.insertOrderItems()
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
    public List<Order> getOrdersByDateRange(ZonedDateTime startDate, ZonedDateTime endDate, Integer page) throws ApiException {
        // if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
        //     throw new ApiException("Start date and end date cannot be null");
        // }
        // startDate = TimeZoneUtil.toUTC(startDate);
        // endDate = TimeZoneUtil.toUTC(endDate);
        // if (startDate.isAfter(endDate)) {
        //     throw new ApiException("Start date cannot be after end date");
        // }
        return orderDao.selectByDateRange(startDate, endDate, page);
    }

    public List<Order> getOrdersByDateRange(ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException {
        return orderDao.selectByDateRange(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() throws ApiException {
        return orderDao.selectAll();
    }

    public Order updateOrder(Order order) throws ApiException {
        if (Objects.isNull(order.getOrderId())) {
            throw new ApiException("Order ID cannot be null");
        }
        
        Order existingOrder = getOrderById(order.getOrderId());
        existingOrder.setInvoicePath(order.getInvoicePath());
        existingOrder.setInvoiceGenerated(order.getInvoiceGenerated());
        orderDao.update(existingOrder);
        return existingOrder;
    }

    @Transactional(readOnly = true)
    public long getCountByDateRange(ZonedDateTime startDate, ZonedDateTime endDate) {
        return orderDao.countByDateRange(startDate, endDate);
    }
} 