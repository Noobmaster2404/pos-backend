package com.increff.server.dao;

import com.increff.server.entity.Product;

import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class ProductDao extends AbstractDao<Product> {
    public ProductDao() {
        super(Product.class);
    }

    public Product selectByBarcode(String barcode) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(root).where(cb.equal(root.get("barcode"), barcode));
        
        return em.createQuery(cq)
                 .getResultList()
                 .stream()
                 .findFirst()
                 .orElse(null);
    }

    public List<Product> selectByNamePrefix(String prefix) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(root).where(
            cb.like(
                cb.lower(root.get("productName")), 
                prefix.toLowerCase() + "%"
            )
        );
        
        return em.createQuery(cq).getResultList();
    }

    public List<Product> selectByClientId(Integer clientId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(root).where(cb.equal(root.get("client").get("clientId"), clientId));
        return em.createQuery(cq).getResultList();
    }
}