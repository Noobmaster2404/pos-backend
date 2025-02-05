package com.increff.server.dao;

import org.springframework.stereotype.Repository;
import com.increff.server.entity.Client;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

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
                 .getResultList()
                 .stream()
                 .findFirst()
                 .orElse(null);
    }

    public List<Client> selectByNamePrefix(String prefix) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> root = cq.from(Client.class);
        cq.select(root).where(
            cb.like(
                cb.lower(root.get("clientName")), 
                prefix.toLowerCase() + "%"
            )
        );

        return em.createQuery(cq).getResultList();
    }

    public List<String> selectNamesByIds(List<Integer> clientIds) {      
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Client> root = cq.from(Client.class);
        cq.select(root.get("clientName"))
          .where(root.get("clientId").in(clientIds));
        
        return em.createQuery(cq).getResultList();
    }

    public List<Client> selectByIds(List<Integer> clientIds) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> root = cq.from(Client.class);
        cq.select(root).where(root.get("clientId").in(clientIds));
        
        return em.createQuery(cq).getResultList();
    }
}
