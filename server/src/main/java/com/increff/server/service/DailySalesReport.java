package com.increff.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.increff.server.api.SalesReportApi;
import com.increff.server.api.OrderApi;
import com.increff.server.entity.DailySales;
import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Order;
import com.increff.server.entity.OrderItem;
import com.increff.commons.util.TimeZoneUtil;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DailySalesReport {
    
    @Autowired
    private SalesReportApi reportApi;
    
    @Autowired
    private OrderApi orderApi;

    // Run at 12:00 AM every day
    @Scheduled(cron = "0 0 0 * * ?")
    public void generateDailyReport() {
        try {
            ZonedDateTime currentUTCTime = TimeZoneUtil.getCurrentUTCDateTime();
            ZonedDateTime startOfDay = TimeZoneUtil.getStartOfDay(currentUTCTime.minusDays(1));
            ZonedDateTime endOfDay = TimeZoneUtil.getEndOfDay(currentUTCTime.minusDays(1));
            
            // Get orders for yesterday
            List<Order> orders = orderApi.getOrdersByDateRange(startOfDay, endOfDay).stream()
                .filter(order -> Objects.nonNull(order.getInvoicePath()))
                .collect(Collectors.toList());
            
            DailySales dailyReport = new DailySales();
            dailyReport.setDate(currentUTCTime.minusDays(1));
            dailyReport.setInvoicedOrders(orders.size());

            int totalItems = 0;
            double totalRevenue = 0.0;
            
            for (Order order : orders) {
                totalItems += order.getOrderItems().stream()
                    .mapToInt(OrderItem::getQuantity)
                    .sum();
                totalRevenue += order.getOrderTotal();
            }
            
            dailyReport.setTotalItems(totalItems);
            dailyReport.setTotalRevenue(totalRevenue);
            
            reportApi.add(dailyReport);
            
        } catch (ApiException e) {
            // Log the error but don't throw it since this is a scheduled task
            e.printStackTrace();
        }
    }
}
