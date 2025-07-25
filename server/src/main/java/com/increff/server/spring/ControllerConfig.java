package com.increff.server.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableWebMvc
@EnableSwagger2
public class ControllerConfig extends WebMvcConfigurerAdapter {

	public static final String PACKAGE_CONTROLLER = "com.increff.server.controller";


	private ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)//
				.useDefaultResponseMessages(false)//
				.select().apis(RequestHandlerSelectors.basePackage(PACKAGE_CONTROLLER))//
				.paths(PathSelectors.regex("/api/.*"))//
				.build();
	}

	// Add configuration for Swagger
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
		registry.addResourceHandler("/static/**").addResourceLocations("/static/");
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setEnableSpringELCompiler(true);
		engine.setTemplateResolver(templateResolver());
		return engine;
	}

	@Bean
	public ThymeleafViewResolver viewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		resolver.setCharacterEncoding("UTF-8");
		return resolver;
	}
	
	@Bean
	public ITemplateResolver templateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setApplicationContext(applicationContext);
		resolver.setPrefix("/html/");
		resolver.setTemplateMode(TemplateMode.HTML);
		return resolver;
	}
	
	@Bean
	public ObjectMapper objectMapper() {
        	JavaTimeModule javaTimeModule = new JavaTimeModule();
        	javaTimeModule.addSerializer(ZonedDateTime.class,
                	new ZonedDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")));
        	return Jackson2ObjectMapperBuilder.json().featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // ISODate
                	.modules(javaTimeModule).build();
    	}
}
