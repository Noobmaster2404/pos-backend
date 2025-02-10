package com.increff.server.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.increff.commons.model.SalesReportData;
import com.increff.server.api.OrderApi;
import com.increff.server.entity.OrderItem;
import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Order;
import java.util.Objects;
import java.time.ZonedDateTime;

@Service
@Transactional(readOnly = true)
public class SalesReportFlow {

    @Autowired
    private OrderApi orderApi;

    public List<SalesReportData> generateSalesReport(ZonedDateTime startDate, ZonedDateTime endDate, Integer clientId) throws ApiException {
        List<Order> orders = orderApi.getOrdersByDateRange(startDate, endDate);
        
        if(Objects.isNull(orders)){
            return new ArrayList<>();
        }

        Map<String, SalesReportData> reportMap = aggregateOrderData(orders, clientId);
        
        return new ArrayList<>(reportMap.values());
    }
    
    private Map<String, SalesReportData> aggregateOrderData(List<Order> orders, Integer clientId) {
        Map<String, SalesReportData> reportMap = new HashMap<>();
        
        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                if (Objects.nonNull(clientId) && !item.getProduct().getClient().getClientId().equals(clientId)) {
                    continue;
                }
                
                String barcode = item.getProduct().getBarcode();
                SalesReportData reportData = reportMap.computeIfAbsent(barcode, 
                    k -> createNewReportData(item));
                
                int newQuantity = reportData.getQuantity() + item.getQuantity();
                double itemRevenue = item.getQuantity() * item.getSellingPrice();
                double newRevenue = reportData.getRevenue() + itemRevenue;
                
                reportData.setQuantity(newQuantity);
                reportData.setRevenue(newRevenue);
                reportData.setAverageSellingPrice(newRevenue / newQuantity);
            }
        }
        
        return reportMap;
    }
    
    private SalesReportData createNewReportData(OrderItem item) {
        SalesReportData data = new SalesReportData();
        data.setBarcode(item.getProduct().getBarcode());
        data.setProductName(item.getProduct().getProductName());
        data.setClientName(item.getProduct().getClient().getClientName());
        data.setQuantity(0);
        data.setRevenue(0.0);
        data.setAverageSellingPrice(0.0);
        return data;
    }
} 