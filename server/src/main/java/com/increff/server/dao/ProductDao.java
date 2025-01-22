package com.increff.server.dao;

import com.increff.server.entity.Product;

import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Repository
public class ProductDao extends AbstractDao<Product> {
    public ProductDao() {
        super(Product.class);
    }

    public Product selectByBarcode(String barcode) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(root).where(cb.equal(root.get("productBarcode"), barcode));
        
        return em.createQuery(cq)
                 .getResultList()
                 .stream()
                 .findFirst()
                 .orElse(null);
    }

    // @Transactional
    // public List<Product> selectByClient(Integer clientId) {
    //     TypedQuery<Product> query = getQuery(SELECT_BY_CLIENT, Product.class);
    //     query.setParameter("clientId", clientId);
    //     return query.getResultList();
    // }
}