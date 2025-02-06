package com.increff.server.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.increff.commons.model.SalesReportForm;
import com.increff.commons.model.SalesReportData;
import com.increff.server.api.OrderApi;
import com.increff.server.entity.OrderItem;
import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Order;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class SalesReportFlow {

    @Autowired
    private OrderApi orderApi;

    public List<SalesReportData> generateSalesReport(SalesReportForm form) throws ApiException {
        List<Order> orders = orderApi.getOrdersByDateRange(form.getStartDate(), form.getEndDate());

        if (Objects.nonNull(form.getClientName())) {
            orders = filterOrdersByClientName(orders, form.getClientName());
        }
        if(Objects.isNull(orders)){
            return new ArrayList<>();
        }
        
        // Aggregate data by product
        Map<String, SalesReportData> reportMap = aggregateOrderData(orders);
        
        return new ArrayList<>(reportMap.values());
    }
    
    private List<Order> filterOrdersByClientName(List<Order> orders, String clientName) {
        return orders.stream()
            .filter(order -> order.getOrderItems().stream()
                .anyMatch(item -> 
                    item.getProduct().getClient().getClientName()
                        .equals(clientName)
                ))
            .collect(Collectors.toList());
    }
    
    private Map<String, SalesReportData> aggregateOrderData(List<Order> orders) {
        Map<String, SalesReportData> reportMap = new HashMap<>();
        
        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                String barcode = item.getProduct().getBarcode();
                
                // Get or create report data
                SalesReportData reportData = reportMap.computeIfAbsent(barcode, 
                    k -> createNewReportData(item));
                
                // Update quantity and revenue in one go
                int newQuantity = reportData.getQuantity() + item.getQuantity();
                double itemRevenue = item.getQuantity() * item.getSellingPrice();
                double newRevenue = reportData.getRevenue() + itemRevenue;
                
                // Set all values at once
                reportData.setQuantity(newQuantity);
                reportData.setRevenue(newRevenue);
                reportData.setAverageSellingPrice(newRevenue / newQuantity);
            }
        }
        
        return reportMap;
    }
    //TODO: Selling price not greater than mrp
    
    private SalesReportData createNewReportData(OrderItem item) {
        SalesReportData data = new SalesReportData();
        data.setBarcode(item.getProduct().getBarcode());
        data.setProductName(item.getProduct().getProductName());
        data.setClientName(item.getProduct().getClient().getClientName());
        data.setQuantity(0);
        data.setRevenue(0.0);
        data.setAverageSellingPrice(0.0);
        return data;
        //TODO: date after and before
    }
} 