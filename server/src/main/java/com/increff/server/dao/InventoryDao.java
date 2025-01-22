package com.increff.server.dao;

import com.increff.server.entity.Inventory;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

@Repository
public class InventoryDao extends AbstractDao<Inventory> {

    public InventoryDao() {
        super(Inventory.class);
    }

    public Optional<Inventory> selectByProductId(Integer productId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Inventory> cq = cb.createQuery(Inventory.class);
        Root<Inventory> root = cq.from(Inventory.class);
        cq.select(root).where(cb.equal(root.get("product").get("productId"), productId));
        
        return em.createQuery(cq)
                 .getResultList()
                 .stream()
                 .findFirst();
    }
}
