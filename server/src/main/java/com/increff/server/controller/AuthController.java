package com.increff.server.controller;

import com.increff.commons.model.LoginForm;
import com.increff.commons.model.SignupForm;
import com.increff.commons.model.LoginData;
import com.increff.server.dto.UserDto;
import com.increff.commons.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.Map;
import java.util.HashMap;

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
    public ResponseEntity<Object> logout(HttpSession session) {
        session.invalidate();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok().body(response);
    }

    @ApiOperation(value = "Get current user information")
    @RequestMapping(value = "/user-info", method = RequestMethod.GET)
    public ResponseEntity<LoginData> getUserInfo() throws ApiException {
        return ResponseEntity.ok(userDto.getUserInfo());
    }
} 