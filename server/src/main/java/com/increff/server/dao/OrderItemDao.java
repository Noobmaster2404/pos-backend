package com.increff.server.dao;

import org.springframework.stereotype.Repository;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

import com.increff.server.entity.OrderItem;

@Repository
public class OrderItemDao extends AbstractDao<OrderItem> {
    
    public OrderItemDao() {
        super(OrderItem.class);
    }

    public List<OrderItem> selectByOrderId(Integer orderId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OrderItem> cq = cb.createQuery(OrderItem.class);
        Root<OrderItem> root = cq.from(OrderItem.class);
        
        cq.select(root).where(
            cb.equal(root.get("order").get("orderId"), orderId)
        );
        
        return em.createQuery(cq).getResultList();
    }
} 