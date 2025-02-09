package com.increff.server.dto;

import com.increff.server.service.UserService;
import com.increff.server.spring.SupervisorConfig;
import com.increff.commons.model.LoginData;
import com.increff.commons.model.LoginForm;
import com.increff.commons.model.SignupForm;
import com.increff.server.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Objects;
import java.util.Collections;
import com.increff.commons.exception.ApiException;
import org.springframework.security.core.Authentication;
import com.increff.server.helper.ConversionHelper;

@Service
public class UserDto extends AbstractDto {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SupervisorConfig supervisorConfig;

    public LoginData signup(SignupForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        User user = ConversionHelper.convertToUser(form, supervisorConfig, passwordEncoder);
        User addedUser = userService.add(user);

        return ConversionHelper.convertToLoginData(addedUser);
    }

    public LoginData login(LoginForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        String email = form.getEmail().toLowerCase().trim();
        User user = userService.getByEmail(email);
        if (Objects.isNull(user) || !passwordEncoder.matches(form.getPassword(), user.getPassword())) {
            throw new ApiException("Invalid credentials");
        }

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            user.getEmail(), 
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        return ConversionHelper.convertToLoginData(user);
    }

    public LoginData getUserInfo() throws ApiException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (Objects.isNull(auth) || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new ApiException("User not authenticated");
        }

        User user = userService.getByEmail(auth.getName());
        if (Objects.isNull(user)) {
            throw new ApiException("User not found");
        }

        return ConversionHelper.convertToLoginData(user);
    }
} 