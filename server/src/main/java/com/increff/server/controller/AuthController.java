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
    public LoginData signup(@RequestBody SignupForm form) throws ApiException {
        return userDto.signup(form);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginData login(@RequestBody LoginForm form) throws ApiException {
        return userDto.login(form);
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
    public LoginData getUserInfo() throws ApiException {
        return userDto.getUserInfo();
    }
} 