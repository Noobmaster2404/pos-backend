package com.increff.server.controller;

import com.increff.commons.model.LoginForm;
import com.increff.commons.model.SignupForm;
import com.increff.commons.model.LoginData;
import com.increff.server.dto.UserDto;
import com.increff.commons.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;
import javax.servlet.http.HttpSession;
import io.swagger.annotations.Api;

@Api(tags = "Authentication")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserDto userDto;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<String> signup(@RequestBody SignupForm form) throws ApiException {
        userDto.signup(form);
        return ResponseEntity.ok("User created successfully");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginData> login(@RequestBody LoginForm form) throws ApiException {
        LoginData loginData = userDto.login(form);
        return ResponseEntity.ok(loginData);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out successfully");
    }

    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public ResponseEntity<LoginData> getUserInfo(HttpSession session) throws ApiException {
        if (Objects.isNull(session) || Objects.isNull(session.getAttribute("SPRING_SECURITY_CONTEXT"))) {
            throw new ApiException("User not authenticated");
        }
        String email = (String) session.getAttribute("user_email");
        String role = (String) session.getAttribute("user_role");
        return ResponseEntity.ok(new LoginData(email, role));
    }
} 