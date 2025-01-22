package com.increff.server.spring;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.lang.NonNull;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * This class is a hook for <b>Servlet 3.0</b> specification, to initialize
 * Spring configuration without any <code>web.xml</code> configuration. Note
 * that {@link #getServletConfigClasses} method returns {@link SpringConfig},
 * which is the starting point for Spring configuration <br>
 * <b>Note:</b> You can also implement the {@link WebApplicationInitializer }
 * interface for more control
 */

public class WebInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(@NonNull ServletContext container) {
		// Create the Spring application context
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(SpringConfig.class);
		
		// Manage the lifecycle of the root application context
		container.addListener(new ContextLoaderListener(rootContext));
		
		// Register and map the dispatcher servlet
		ServletRegistration.Dynamic dispatcher = container.addServlet(
				"dispatcher", new DispatcherServlet(rootContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
	}

}