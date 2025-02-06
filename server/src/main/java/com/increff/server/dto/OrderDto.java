package com.increff.server.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.increff.server.flow.OrderFlow;
import com.increff.server.helper.ConversionHelper;
import com.increff.server.api.ProductApi;
import com.increff.commons.model.OrderData;
import com.increff.commons.model.OrderForm;
import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Order;
import com.increff.server.entity.OrderItem;
import com.increff.server.entity.Product;
import com.increff.commons.model.OrderItemForm;

@Service
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
            Product product = productApi.getCheckProductByBarcode(itemForm.getBarcode());
            OrderItem orderItem = ConversionHelper.convertToOrderItem(itemForm, product, order);
            
            orderTotal += itemForm.getQuantity() * itemForm.getSellingPrice();
            orderItems.add(orderItem);
        }
        //TODO: List api
        
        order.setOrderItems(orderItems);
        order.setOrderTotal(orderTotal);
        //TODO: set is done in api layer
        Order createdOrder = orderFlow.createOrder(order);
        //TODO: Move the above in flow and as much as possible to api layer
        
        return ConversionHelper.convertToOrderData(createdOrder);
    }

    public OrderData getOrder(Integer orderId) throws ApiException {
        Order order = orderFlow.getOrderById(orderId);
        return ConversionHelper.convertToOrderData(order);
    }

    public List<OrderData> getOrdersByDateRange(ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException {
        List<Order> orders = orderFlow.getOrdersByDateRange(startDate, endDate);
        return orders.stream()
                .map(order -> {
                    return ConversionHelper.convertToOrderData(order);
                })
                .collect(Collectors.toList());
    }

    public void generateInvoice(OrderData orderData) throws ApiException {
        
        Order order = orderFlow.getOrderById(orderData.getOrderId());
        orderFlow.generateAndSaveInvoice(order);
    }
} 