package com.increff.server.dao;

import com.increff.server.entity.Product;

import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

@Repository
public class ProductDao extends AbstractDao<Product> {
    @Value("${PAGE_SIZE}")
    private int PAGE_SIZE;

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

    public List<Product> selectByNamePrefix(String prefix, Integer pageNo) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(root).where(
            cb.like(
                cb.lower(root.get("productName")), 
                prefix.toLowerCase() + "%"
            )
        );
        
        return em.createQuery(cq)
                 .setFirstResult(pageNo * PAGE_SIZE)
                 .setMaxResults(PAGE_SIZE)
                 .getResultList();
    }

    public List<Product> selectByClientId(Integer clientId, Integer pageNo) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(root).where(cb.equal(root.get("client").get("clientId"), clientId));
        
        return em.createQuery(cq)
                 .setFirstResult(pageNo * PAGE_SIZE)
                 .setMaxResults(PAGE_SIZE)
                 .getResultList();
    }

    public long count() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(cb.count(root));
        return em.createQuery(cq).getSingleResult();
    }

    public long countByNamePrefix(String prefix) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(cb.count(root)).where(
            cb.like(
                cb.lower(root.get("productName")), 
                prefix.toLowerCase() + "%"
            )
        );
        return em.createQuery(cq).getSingleResult();
    }

    public long countByClientId(Integer clientId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Product> root = cq.from(Product.class);
        cq.select(cb.count(root))
          .where(cb.equal(root.get("client").get("clientId"), clientId));
        return em.createQuery(cq).getSingleResult();
    }
}
