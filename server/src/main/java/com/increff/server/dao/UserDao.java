package com.increff.server.dao;

import com.increff.server.entity.User;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;

@Repository
public class UserDao extends AbstractDao<User> {

    @PersistenceContext
    private EntityManager em;

    public UserDao() {
        super(User.class);
    }

    public User selectByEmail(String email) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        
        Predicate emailPredicate = cb.equal(root.get("email"), email);
        cq.where(emailPredicate);
        
        return em.createQuery(cq)
                 .setMaxResults(1)
                 .getResultList()
                 .stream()
                 .findFirst()
                 .orElse(null);
    }
} 