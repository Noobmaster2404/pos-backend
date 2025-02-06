package com.increff.server.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import java.util.HashMap;

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
import com.increff.commons.model.OrderSearchForm;
import com.increff.commons.util.TimeZoneUtil;
import com.increff.commons.model.PaginatedData;
import com.increff.server.api.OrderApi;
import com.increff.server.api.OrderItemApi;

@Service
public class OrderDto extends AbstractDto {
    
    @Autowired
    private OrderFlow orderFlow;

    @Autowired
    private OrderApi orderApi;
    
    @Autowired
    private ProductApi productApi;

    @Autowired
    private OrderItemApi orderItemApi;

    @Value("${PAGE_SIZE}")
    private Integer PAGE_SIZE;

    public OrderData addOrder(OrderForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        
        List<String> barcodes = form.getOrderItems().stream()
            .map(OrderItemForm::getBarcode)
            .distinct()
            .collect(Collectors.toList());

        List<Product> products = productApi.getCheckProductsByBarcodes(barcodes);
        Map<String, Product> barcodeToProduct = products.stream()
            .collect(Collectors.toMap(Product::getBarcode, product -> product));

        Order order = ConversionHelper.convertToOrder(form, barcodeToProduct);
        
        Order createdOrder = orderFlow.addOrder(order);
        
        
        return ConversionHelper.convertToOrderData(createdOrder);
    }

    public OrderData getOrder(Integer orderId) throws ApiException {
        Order order = orderFlow.getOrderById(orderId);
        List<OrderItem> orderItems = orderItemApi.getOrderItemsByOrderId(orderId);

        List<Integer> productIds = orderItems.stream()
            .map(item -> item.getProduct().getProductId())
            .collect(Collectors.toList());

        List<Product> products = productApi.getCheckProductsByIds(productIds);
 
        return ConversionHelper.convertToOrderData(order, orderItems, products);
    }

    public PaginatedData<OrderData> getOrdersByDateRange(OrderSearchForm form, Integer page) throws ApiException {
        ZonedDateTime startDate = TimeZoneUtil.toUTC(form.getStartDate());
        ZonedDateTime endDate = TimeZoneUtil.toUTC(form.getEndDate());
        List<Order> orders = orderFlow.getOrdersByDateRange(startDate, endDate, page);

        List<Integer> orderIds = orders.stream()
            .map(Order::getOrderId)
            .collect(Collectors.toList());
            
        Map<Integer, List<OrderItem>> orderItemsMap = new HashMap<>();
        for (Integer orderId : orderIds) {
            orderItemsMap.put(orderId, orderItemApi.getOrderItemsByOrderId(orderId));
        }

        List<Integer> productIds = orderItemsMap.values().stream()
            .flatMap(List::stream)
            .map(item -> item.getProduct().getProductId())
            .distinct()
            .collect(Collectors.toList());
            
        List<Product> products = productApi.getCheckProductsByIds(productIds);
        Map<Integer, Product> productMap = products.stream()
            .collect(Collectors.toMap(Product::getProductId, product -> product));
        List<OrderData> orderDataList = ConversionHelper.convertToOrderData(orders, orderItemsMap, productMap);

        long totalCount = orderApi.getCountByDateRange(startDate, endDate);

        return new PaginatedData<>(orderDataList, page, totalCount, PAGE_SIZE);
    }

    public void generateInvoice(OrderData orderData) throws ApiException {
        
        Order order = orderFlow.getOrderById(orderData.getOrderId());
        orderFlow.generateAndSaveInvoice(order);
    }
} 