package com.increff.server.dao;

import org.springframework.stereotype.Repository;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.ZonedDateTime;
import java.util.List;

import com.increff.server.entity.Order;

@Repository
public class OrderDao extends AbstractDao<Order> {
    
    public OrderDao() {
        super(Order.class);
    }

    public List<Order> selectByDateRange(ZonedDateTime startDate, ZonedDateTime endDate) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> root = cq.from(Order.class);
        
        cq.select(root).where(
            cb.and(
                cb.greaterThanOrEqualTo(root.get("orderTime"), startDate),
                cb.lessThanOrEqualTo(root.get("orderTime"), endDate)
            )
        );
        
        return em.createQuery(cq).getResultList();
    }
} 