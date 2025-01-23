package com.increff.server.dao;

import org.springframework.stereotype.Repository;
import com.increff.server.entity.Client;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Repository
public class ClientDao extends AbstractDao<Client> {

    public ClientDao() {
        super(Client.class);
    }

    public Client selectByName(String clientName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> root = cq.from(Client.class);
        cq.select(root).where(cb.equal(root.get("clientName"), clientName));

        return em.createQuery(cq)
//                .setFirstResult()
//                .setMaxResults()
                 .getResultList()
                 .stream()
                 .findFirst()
                 .orElse(null);
    }
}
