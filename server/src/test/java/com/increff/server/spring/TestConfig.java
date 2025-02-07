package com.increff.server.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.mockito.Mockito;

import com.increff.invoice.dto.InvoiceDto;
import com.increff.invoice.service.InvoiceGenerator;

@Configuration
@ComponentScan(
    basePackages = {
        "com.increff.server"
    },
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = EnableSwagger2.class)
    }
)
@PropertySource("classpath:test.properties")
@Import(MockServletContextConfig.class)
public class TestConfig {
    
    @Bean
    public InvoiceGenerator invoiceGenerator() {
        return Mockito.mock(InvoiceGenerator.class);
    }

    @Bean
    public InvoiceDto invoiceDto() {
        return Mockito.mock(InvoiceDto.class);
    }
} 