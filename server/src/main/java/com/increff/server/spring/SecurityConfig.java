package com.increff.server.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import java.util.Arrays;

import com.increff.server.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private CustomUserDetailsService userDetailsService = new CustomUserDetailsService();

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
                config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(Arrays.asList("*"));
                config.setAllowCredentials(true);
                return config;
            }))
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/auth/**").permitAll()
                
            // Supervisor-only endpoints
            .antMatchers(HttpMethod.POST, "/clients/**").hasRole("SUPERVISOR")
            .antMatchers(HttpMethod.PUT, "/clients/**").hasRole("SUPERVISOR")
            .antMatchers(HttpMethod.POST, "/products/**").hasRole("SUPERVISOR")
            .antMatchers(HttpMethod.PUT, "/products/**").hasRole("SUPERVISOR")
            .antMatchers(HttpMethod.POST, "/inventory/**").hasRole("SUPERVISOR")
            .antMatchers(HttpMethod.PUT, "/inventory/**").hasRole("SUPERVISOR")
            
            // Operator accessible endpoints
            .antMatchers(HttpMethod.GET, "/**").authenticated()
            
            // This should come last
            .anyRequest().authenticated()
            .and()
            .formLogin().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public HttpFirewall allowAllHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        firewall.setAllowSemicolon(true);
        firewall.setAllowBackSlash(true);
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setAllowUrlEncodedPeriod(true);
        return firewall;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.httpFirewall(allowAllHttpFirewall())
           .ignoring()
           .antMatchers("/v2/api-docs", 
                       "/configuration/ui", 
                       "/swagger-resources/**", 
                       "/configuration/security", 
                       "/swagger-ui.html", 
                       "/webjars/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 