package com.increff.server.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

@Transactional
public abstract class AbstractDao<T> {

    @PersistenceContext
    protected EntityManager em;

    @Value("${PAGE_SIZE}")
    private Integer PAGE_SIZE;

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
                .setMaxResults(PAGE_SIZE)
                .getResultList();
    }

    public void update(T entity) {
        em.merge(entity);
    }

    public T select(Integer id) {
        return em.find(entityClass, id);
    }

    public long count() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(entityClass);
        cq.select(cb.count(root));
        return em.createQuery(cq).getSingleResult();
    }
}
