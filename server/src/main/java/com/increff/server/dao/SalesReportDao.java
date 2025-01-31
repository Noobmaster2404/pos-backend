package com.increff.server.dao;

import com.increff.server.entity.DailySales;

import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Repository
public class SalesReportDao extends AbstractDao<DailySales> {
    
    public SalesReportDao() {
        super(DailySales.class);
    }

    public DailySales selectByDate(LocalDate date) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DailySales> cq = cb.createQuery(DailySales.class);
        Root<DailySales> root = cq.from(DailySales.class);
        
        cq.where(cb.equal(root.get("date"), date));
        
        List<DailySales> reports = em.createQuery(cq).getResultList();
        return reports.isEmpty() ? null : reports.get(0);
    }

    public List<DailySales> selectByDateRange(LocalDate startDate, LocalDate endDate) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DailySales> cq = cb.createQuery(DailySales.class);
        Root<DailySales> root = cq.from(DailySales.class);
        
        cq.where(cb.between(root.get("date"), startDate, endDate));
        cq.orderBy(cb.asc(root.get("date")));
        
        return em.createQuery(cq).getResultList();
    }
} 