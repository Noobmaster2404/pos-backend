package com.increff.server.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import com.increff.server.entity.Inventory;
import com.increff.server.api.ProductApi;
import com.increff.server.api.InventoryApi;
import com.increff.commons.model.OrderItemData;
import com.increff.commons.model.OrderItemForm;

@Component
public class OrderDto extends AbstractDto {
    
    @Autowired
    private OrderFlow orderFlow;
    
    @Autowired
    private ProductFlow productFlow;

    public OrderData createOrder(OrderForm form) throws ApiException {
        checkValid(form);
        
        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (OrderItemForm itemForm : form.getOrderItems()) {
            Product product = productFlow.getProductById(itemForm.getProductId());
            
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemForm.getQuantity());
            item.setSellingPrice(itemForm.getSellingPrice());
            
            orderItems.add(item);
        }
        
        order.setOrderItems(orderItems);
        Order createdOrder = orderFlow.createOrder(order);
        
        return convertToOrderData(createdOrder);
    }

    public OrderData getOrder(Integer orderId) throws ApiException {
        Order order = orderFlow.getOrderById(orderId);
        return convertToOrderData(order);
    }

    // public List<OrderData> getOrdersByDateRange(ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException {
    //     return orderFlow.getOrdersByDateRange(startDate, endDate)
    //             .stream()
    //             .map(this::convertToOrderData)
    //             .collect(Collectors.toList());
    // }

    private OrderData convertToOrderData(Order order) {
        OrderData data = new OrderData();
        data.setOrderId(order.getOrderId());
        data.setOrderTime(order.getOrderTime());
        data.setOrderTotal(order.getOrderTotal());
        data.setInvoicePath(order.getInvoicePath());
        
        List<OrderItemData> itemDataList = order.getOrderItems()
                .stream()
                .map(this::convertToOrderItemData)
                .collect(Collectors.toList());
        
        data.setOrderItems(itemDataList);
        return data;
    }

    private OrderItemData convertToOrderItemData(OrderItem item) {
        OrderItemData data = new OrderItemData();
        data.setOrderItemId(item.getOrderItemId());
        data.setProductId(item.getProduct().getProductId());
        data.setProductName(item.getProduct().getProductName());
        data.setBarcode(item.getProduct().getBarcode());
        data.setQuantity(item.getQuantity());
        data.setSellingPrice(item.getSellingPrice());
        data.setItemTotal(item.getQuantity() * item.getSellingPrice());
        return data;
    }

    @Override
    protected String getPrefix() {
        return "Order: ";
    }
} 