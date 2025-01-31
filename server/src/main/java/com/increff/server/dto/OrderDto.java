package com.increff.server.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.increff.server.flow.OrderFlow;
import com.increff.server.api.ProductApi;
import com.increff.commons.model.OrderData;
import com.increff.commons.model.OrderForm;
import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Order;
import com.increff.server.entity.OrderItem;
import com.increff.server.entity.Product;
import com.increff.commons.model.OrderItemForm;

@Component
public class OrderDto extends AbstractDto {
    
    @Autowired
    private OrderFlow orderFlow;
    
    @Autowired
    private ProductApi productApi;

    public OrderData createOrder(OrderForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        
        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        double orderTotal = 0.0;
        
        for (OrderItemForm itemForm : form.getOrderItems()) {
            try {
                Product product = productApi.getProductByBarcode(itemForm.getBarcode());
                OrderItem orderItem = ConversionHelper.convertToOrderItem(itemForm, product, order);
                
                orderTotal += itemForm.getQuantity() * itemForm.getSellingPrice();
                orderItems.add(orderItem);
            } catch (ApiException e) {
                throw new ApiException(getPrefix() + e.getMessage());
            }
        }
        
        order.setOrderItems(orderItems);
        order.setOrderTotal(orderTotal);
        Order createdOrder = orderFlow.createOrder(order);
        
        return ConversionHelper.convertToOrderData(createdOrder);
    }

    @Transactional(readOnly = true)
    public OrderData getOrder(Integer orderId) throws ApiException {
        try {
            Order order = orderFlow.getOrderById(orderId);
            return ConversionHelper.convertToOrderData(order);
        } catch (ApiException e) {
            throw new ApiException(getPrefix() + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<OrderData> getOrdersByDateRange(ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException {
        try {
            List<Order> orders = orderFlow.getOrdersByDateRange(startDate, endDate);
            return orders.stream()
                    .map(order -> {
                        return ConversionHelper.convertToOrderData(order);
                    })
                    .collect(Collectors.toList());
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ApiException) {
                throw (ApiException) e.getCause();
            }
            throw new ApiException(getPrefix() + e.getMessage());
        }
    }

    public void generateInvoice(OrderData orderData) throws ApiException {
        
        Order order = orderFlow.getOrderById(orderData.getOrderId());
        orderFlow.generateAndSaveInvoice(order);
    }

    @Override
    protected String getPrefix() {
        return "Order: ";
    }
} 