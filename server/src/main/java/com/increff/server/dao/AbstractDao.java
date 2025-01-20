package com.increff.server.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public abstract class AbstractDao<T> {

    @PersistenceContext
    protected EntityManager em;

    private Class<T> entityClass;

    protected AbstractDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Transactional
    public void insert(T entity) {
        em.persist(entity);
    }

    public List<T> selectAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root);
        return em.createQuery(cq).getResultList();
    }

    @Transactional
    public void update(T entity) {
        em.merge(entity);
    }

    public T select(int id) {
        return em.find(entityClass, id);
    }

    public T selectByName(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root).where(cb.equal(root.get("name"), name));
        
        return em.createQuery(cq)
                 .getResultList()
                 .stream()
                 .findFirst()
                 .orElse(null);
    }
}
