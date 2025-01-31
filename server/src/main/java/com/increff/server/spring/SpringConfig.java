package com.increff.server.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackages = {
    "com.increff.server",
    "com.increff.invoice",
    "com.increff.commons"
})
@EnableWebMvc
@EnableSwagger2
@PropertySources({ //
		@PropertySource(value = "file:./pos.properties", ignoreResourceNotFound = true) //
})
@EnableScheduling
public class SpringConfig implements WebMvcConfigurer {

	@Value("${app.baseUrl}")
	private String baseUrl;

	@Override
	public void configureDefaultServletHandling(@NonNull DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
}
