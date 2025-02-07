package com.increff.server.api;

import static org.junit.Assert.*;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.server.entity.DailySales;
import com.increff.commons.exception.ApiException;
import com.increff.server.AbstractUnitTest;
import com.increff.commons.util.TimeZoneUtil;

public class SalesReportApiTest extends AbstractUnitTest {

    @Autowired
    private SalesReportApi salesReportApi;

    private DailySales createTestDailySales(ZonedDateTime date, Integer orderCount, Integer itemCount, Double revenue) {
        DailySales dailySales = new DailySales();
        dailySales.setDate(date);
        dailySales.setInvoicedOrdersCount(orderCount);
        dailySales.setItemCount(itemCount);
        dailySales.setTotalRevenue(revenue);
        return dailySales;
    }

    @Test
    public void testAdd() throws ApiException {
        ZonedDateTime date = TimeZoneUtil.getCurrentUTCDateTime();
        DailySales dailySales = createTestDailySales(date, 10, 20, 1000.0);
        
        DailySales added = salesReportApi.add(dailySales);
        assertNotNull(added);
        assertEquals(Integer.valueOf(10), added.getInvoicedOrdersCount());
        assertEquals(Integer.valueOf(20), added.getItemCount());
        assertEquals(Double.valueOf(1000.0), added.getTotalRevenue());
    }

    @Test(expected = ApiException.class)
    public void testAddDuplicate() throws ApiException {
        ZonedDateTime date = TimeZoneUtil.getCurrentUTCDateTime();
        DailySales dailySales1 = createTestDailySales(date, 10, 20, 1000.0);
        DailySales dailySales2 = createTestDailySales(date, 15, 25, 1500.0);
        
        salesReportApi.add(dailySales1);
        salesReportApi.add(dailySales2); // Should throw ApiException
    }

    @Test
    public void testGetByDateRange() throws ApiException {
        ZonedDateTime date1 = TimeZoneUtil.getCurrentUTCDateTime();
        ZonedDateTime date2 = date1.plusDays(1);
        
        DailySales dailySales1 = createTestDailySales(date1, 10, 20, 1000.0);
        DailySales dailySales2 = createTestDailySales(date2, 15, 25, 1500.0);
        
        salesReportApi.add(dailySales1);
        salesReportApi.add(dailySales2);
        
        List<DailySales> reports = salesReportApi.getByDateRange(
            date1.minusDays(1), 
            date2.plusDays(1)
        );
        
        assertEquals(2, reports.size());
    }

    @Test
    public void testGetByDateRangeEmpty() throws ApiException {
        ZonedDateTime startDate = TimeZoneUtil.getCurrentUTCDateTime();
        ZonedDateTime endDate = startDate.plusDays(1);
        
        List<DailySales> reports = salesReportApi.getByDateRange(startDate, endDate);
        assertTrue(reports.isEmpty());
    }

    @Test(expected = ApiException.class)
    public void testGetByInvalidDateRange() throws ApiException {
        ZonedDateTime startDate = TimeZoneUtil.getCurrentUTCDateTime();
        ZonedDateTime endDate = startDate.minusDays(1);
        
        salesReportApi.getByDateRange(startDate, endDate);
    }

    @Test
    public void testGetByExactDate() throws ApiException {
        ZonedDateTime date = TimeZoneUtil.getCurrentUTCDateTime();
        DailySales dailySales = createTestDailySales(date, 10, 20, 1000.0);
        salesReportApi.add(dailySales);
        
        List<DailySales> reports = salesReportApi.getByDateRange(date, date);
        assertEquals(1, reports.size());
    }
} 