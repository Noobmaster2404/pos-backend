package com.increff.server.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

import com.increff.commons.model.SalesReportForm;
import com.increff.commons.util.TimeZoneUtil;
import com.increff.commons.model.SalesReportData;
import com.increff.commons.exception.ApiException;
import com.increff.server.flow.SalesReportFlow;
import com.increff.server.helper.ConversionHelper;
import com.increff.commons.model.DailySalesData;
import com.increff.server.api.SalesReportApi;
import com.increff.commons.model.DailySalesForm;
import com.increff.server.entity.DailySales;

@Service
public class SalesReportDto extends AbstractDto {
    
    @Autowired
    private SalesReportFlow reportFlow;

    @Autowired
    private SalesReportApi salesReportApi;
    
    public List<SalesReportData> getSalesReport(SalesReportForm form) throws ApiException {
        checkValid(form);
        ZonedDateTime startDate = TimeZoneUtil.getStartOfDay(form.getStartDate());
        ZonedDateTime endDate = TimeZoneUtil.getEndOfDay(form.getEndDate());
        Integer clientId = form.getClientId();
        return reportFlow.generateSalesReport(startDate, endDate, clientId);
    }
    
    public List<DailySalesData> getDailySalesReport(DailySalesForm form) throws ApiException {
        checkValid(form);
        ZonedDateTime startDate = TimeZoneUtil.getStartOfDay(form.getStartDate());
        ZonedDateTime endDate = TimeZoneUtil.getEndOfDay(form.getEndDate());
        List<DailySales> reports = salesReportApi.getByDateRange(startDate, endDate);
        return ConversionHelper.convertToDailySalesData(reports);
    }
} 