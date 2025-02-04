package com.increff.server.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Component
public class SupervisorConfig {

    @Value("${supervisor.emails}")
    private String supervisorEmails;

    public boolean isSupervisor(String email) {
        List<String> emails = Arrays.asList(supervisorEmails.split(","));
        return emails.contains(email.trim().toLowerCase());
    }
} 