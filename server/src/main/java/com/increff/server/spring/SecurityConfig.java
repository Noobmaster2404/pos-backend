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

import com.increff.server.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private CustomUserDetailsService userDetailsService = new CustomUserDetailsService();

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
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
            // .exceptionHandling()
            // .authenticationEntryPoint((request, response, authException) -> {
            //     response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            //     response.getWriter().write("Unauthorized: Authentication is required");
            // })
            // .and()
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