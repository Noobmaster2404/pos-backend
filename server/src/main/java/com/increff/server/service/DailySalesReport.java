package com.increff.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Scheduled(cron = "0 30 18 * * ?")  // 18:30 UTC = 00:00 IST
    @Transactional(rollbackFor = Exception.class)
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
            dailyReport.setInvoicedOrdersCount(orders.size());

            int totalItems = 0;
            double totalRevenue = 0.0;
            
            for (Order order : orders) {
                totalItems += order.getOrderItems().stream()
                    .mapToInt(OrderItem::getQuantity)
                    .sum();
                totalRevenue += order.getOrderTotal();
            }
            
            dailyReport.setItemCount(totalItems);
            dailyReport.setTotalRevenue(totalRevenue);
            
            reportApi.add(dailyReport);
            
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
