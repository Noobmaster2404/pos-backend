package com.increff.server.service;

import com.increff.server.dao.UserDao;
import com.increff.server.entity.User;
import com.increff.commons.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Service
public class UserService {
    
    @Autowired
    private UserDao dao;

    @Transactional(rollbackFor = ApiException.class)
    public User add(User user) throws ApiException {
        User existing = dao.selectByEmail(user.getEmail());
        if (Objects.nonNull(existing)) {
            throw new ApiException("User with email " + user.getEmail() + " already exists");
        }
        dao.insert(user);
        return user;
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return dao.selectByEmail(email);
    }
} 