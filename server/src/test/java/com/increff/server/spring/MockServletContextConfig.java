package com.increff.server.spring;

import org.springframework.mock.web.MockServletContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.servlet.ServletContext;

@Configuration
public class MockServletContextConfig {
    
    @Bean
    public ServletContext servletContext() {
        return new MockServletContext();
    }
}