package com.increff.server.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

import com.increff.commons.model.SalesReportForm;
import com.increff.commons.model.SalesReportData;
import com.increff.commons.exception.ApiException;
import com.increff.server.flow.SalesReportFlow;
import com.increff.server.api.ClientApi;
import com.increff.commons.model.ClientData;
import com.increff.server.entity.Client;
import com.increff.commons.model.DailySalesData;
import com.increff.server.api.SalesReportApi;
import com.increff.commons.model.DailySalesForm;
import com.increff.server.entity.DailySales;

@Component
public class SalesReportDto extends AbstractDto {
    
    @Autowired
    private SalesReportFlow reportFlow;
    
    @Autowired
    private ClientApi clientApi;

    @Autowired
    private SalesReportApi salesReportApi;

    public List<ClientData> getClientsByName(String clientNamePrefix) throws ApiException {
        List<Client> clients = clientApi.getClientsByName(clientNamePrefix);
        return ConversionHelper.convertToClientData(clients);
    }
    
    public List<SalesReportData> getSalesReport(SalesReportForm form) throws ApiException {
        checkValid(form);
        return reportFlow.generateSalesReport(form);
    }
    
    public List<DailySalesData> getDailySalesReport(DailySalesForm form) throws ApiException {
        checkValid(form);
        List<DailySales> reports = salesReportApi.getByDateRange(form.getStartDate(), form.getEndDate());
        return ConversionHelper.convertToDailySalesData(reports);
    }
        
    @Override
    protected <T> void checkValid(T form) throws ApiException {
        super.checkValid(form);
        if (form instanceof SalesReportForm) {
            SalesReportForm reportForm = (SalesReportForm) form;
            if (reportForm.getStartDate().isAfter(reportForm.getEndDate())) {
                throw new ApiException("Start date cannot be after end date");
            }
        }
        else if (form instanceof DailySalesForm) {
            DailySalesForm dailySalesForm = (DailySalesForm) form;
            if (dailySalesForm.getStartDate().isAfter(dailySalesForm.getEndDate())) {
                throw new ApiException("Start date cannot be after end date");
            }
        }
    }
} 