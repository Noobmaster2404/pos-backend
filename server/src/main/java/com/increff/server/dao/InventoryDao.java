package com.increff.server.dao;

import com.increff.server.entity.Inventory;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class InventoryDao extends AbstractDao<Inventory> {

    public InventoryDao() {
        super(Inventory.class);
    }

    public Inventory selectByProductId(Integer productId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Inventory> cq = cb.createQuery(Inventory.class);
        Root<Inventory> root = cq.from(Inventory.class);
        cq.select(root).where(cb.equal(root.get("product").get("productId"), productId));
        
        return em.createQuery(cq)
                 .getResultList()
                 .stream()
                 .findFirst()
                 .orElse(null);
    }

    public Inventory selectByBarcode(String barcode) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Inventory> cq = cb.createQuery(Inventory.class);
        Root<Inventory> root = cq.from(Inventory.class);
        cq.select(root).where(cb.equal(root.get("barcode"), barcode));
        
        return em.createQuery(cq)
                 .getResultList()
                 .stream()
                 .findFirst()
                 .orElse(null);
    }

    public List<Inventory> selectByProductIds(List<Integer> productIds) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Inventory> cq = cb.createQuery(Inventory.class);
        Root<Inventory> root = cq.from(Inventory.class);
        cq.select(root).where(root.get("product").get("productId").in(productIds));
        return em.createQuery(cq).getResultList();
    }
    //TODO: Add funciton for join here
}
