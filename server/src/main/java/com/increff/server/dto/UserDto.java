package com.increff.server.dto;

import com.increff.server.service.UserService;
import com.increff.server.spring.SupervisorConfig;
import com.increff.commons.model.LoginData;
import com.increff.commons.model.LoginForm;
import com.increff.commons.model.SignupForm;
import com.increff.server.entity.User;
import com.increff.server.model.Role;
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

@Service
public class UserDto extends AbstractDto {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SupervisorConfig supervisorConfig;

    public User signup(SignupForm form) throws ApiException {
        checkValid(form);
        String email = form.getEmail().toLowerCase().trim();
        
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRole(supervisorConfig.isSupervisor(email) ? Role.SUPERVISOR : Role.OPERATOR);
        
        return userService.add(user);
    }

    public LoginData login(LoginForm form) throws ApiException {
        checkValid(form);

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
        
        return new LoginData(user.getEmail(), user.getRole().name());
        //TODO: Dto -> DtoApi
        //TODO: Flow -> FlowApi
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

        return new LoginData(user.getEmail(), user.getRole().name());
    }
} 