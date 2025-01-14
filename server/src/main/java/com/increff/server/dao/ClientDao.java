package com.increff.server.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.increff.server.entity.Client;

@Repository
public class ClientDao {

    private static final String SELECT_ALL = "SELECT p FROM Client p";

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void insert(Client p) {
        em.persist(p);
    }

    public List<Client> selectAll() {
        TypedQuery<Client> query = em.createQuery(SELECT_ALL, Client.class);
        return query.getResultList();
    }

    @Transactional
    public void update(Client p) {
        em.merge(p);
    }

    public Client select(int id) {
        return em.find(Client.class, id);
    }
}
