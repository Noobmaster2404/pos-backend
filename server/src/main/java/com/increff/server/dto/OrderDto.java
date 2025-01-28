package com.increff.server.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.increff.server.flow.OrderFlow;
import com.increff.server.flow.ProductFlow;
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
    private ProductFlow productFlow;

    public OrderData createOrder(OrderForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        
        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (OrderItemForm itemForm : form.getOrderItems()) {
            Product product = productFlow.getProductByBarcode(itemForm.getBarcode());
            
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemForm.getQuantity());
            item.setSellingPrice(itemForm.getSellingPrice());
            
            orderItems.add(item);
        }
        
        order.setOrderItems(orderItems);
        Order createdOrder = orderFlow.createOrder(order);
        
        return ConversionClass.convertToOrderData(createdOrder);
    }

    @Transactional(readOnly = true)
    public OrderData getOrder(Integer orderId) throws ApiException {
        try {
            Order order = orderFlow.getOrderById(orderId);
            return ConversionClass.convertToOrderData(order);
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
                        try {
                            return ConversionClass.convertToOrderData(order);
                        } catch (ApiException e) {
                            throw new RuntimeException(e);
                        }
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