package com.increff.server.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.increff.server.entity.Client;

@Repository
public class ClientDao {
    //Use typed query only when you know the exact structure of the query
    
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void insert(Client p) {
        em.persist(p);
    }

    public List<Client> selectAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> root = cq.from(Client.class);
        cq.select(root);
        TypedQuery<Client> query = em.createQuery(cq);
        return query.getResultList();
    }

    @Transactional
    public void update(Client p) {
        em.merge(p);
    }

    public Client select(int id) {
        return em.find(Client.class, id);
    }

    public Client selectByName(String name) {
        // Using CriteriaBuilder to create a query for selecting a client by name
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> root = cq.from(Client.class);
        cq.select(root).where(cb.equal(root.get("name"), name));
        TypedQuery<Client> query = em.createQuery(cq);
        List<Client> clients = query.getResultList();
        return clients.isEmpty() ? null : clients.get(0);
    }
}
