package com.increff.server.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.increff.server.dao.SalesReportDao;
import com.increff.server.entity.DailySales;
import com.increff.commons.exception.ApiException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class SalesReportApi{

    @Autowired
    private SalesReportDao dao;

    @Transactional(rollbackFor = Exception.class)
    public DailySales add(DailySales report) throws ApiException {
        DailySales existing = dao.selectByDate(report.getDate());
        if (Objects.nonNull(existing)) {
            throw new ApiException("Report already exists for date: " + report.getDate());
        }
        dao.insert(report);
        return report;
    }

    @Transactional(readOnly = true)
    public List<DailySales> getByDateRange(LocalDate startDate, LocalDate endDate) throws ApiException {
        List<DailySales> reports = dao.selectByDateRange(startDate, endDate);
        return reports;
        //handle empty in UI
    }
} 