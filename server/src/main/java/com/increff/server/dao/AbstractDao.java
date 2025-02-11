package com.increff.server.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Transactional
public abstract class AbstractDao<T> {

    @PersistenceContext
    protected EntityManager em;

    private Integer PAGE_SIZE = 10;

    private Class<T> entityClass;

    protected AbstractDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

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

    public List<T> selectAll(Integer page) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root);
        return em.createQuery(cq)
                .setFirstResult(page * PAGE_SIZE)
                .setMaxResults(PAGE_SIZE+1)
                .getResultList();
    }

    public void update(T entity) {
        em.merge(entity);
    }

    public T select(Integer id) {
        return em.find(entityClass, id);
    }
}
