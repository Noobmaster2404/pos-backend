package com.increff.server.dao;

import com.increff.server.entity.Product;
import org.springframework.stereotype.Repository;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class ProductDao extends AbstractDao {

    private static final String SELECT_BY_BARCODE = "select p from ProductPojo p where p.barcode=:barcode";
    private static final String SELECT_BY_CLIENT = "select p from ProductPojo p where p.client.id=:clientId";

    @Transactional
    public void insert(Product product) {
        em.persist(product);
    }

    public Product select(Integer id) {
        return em.find(Product.class, id);
    }

    public List<Product> selectAll() {
        return select(Product.class);
    }

    public Product selectByBarcode(String barcode) {
        TypedQuery<Product> query = getQuery(SELECT_BY_BARCODE, Product.class);
        query.setParameter("barcode", barcode);
        return getSingle(query);
    }

    public List<Product> selectByClient(Integer clientId) {
        TypedQuery<Product> query = getQuery(SELECT_BY_CLIENT, Product.class);
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }

    public void update(Product product) {
        em.merge(product);
    }
}