package com.sam.config;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.sam.model.AuthList;


@Configuration
@EnableWebMvc
@EnableWebSecurity
@ComponentScan(basePackages="com.sam")
public class SpringJavaConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
	private final String AUTH_HEADER = "Authorization";
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		APIKeyAuthFilter filter = new APIKeyAuthFilter(AUTH_HEADER);
        filter.setAuthenticationManager(new AuthenticationManager() {

            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String apiKey = (String) authentication.getPrincipal();
                authentication.setAuthenticated(AuthList.authCheck(apiKey));
                return authentication;
            }
            
        });
        
		http
		.addFilter(filter) // for API key check
		.csrf().disable()
	    .exceptionHandling()
	    .authenticationEntryPoint(restAuthenticationEntryPoint)
	    .and()
	    .sessionManagement()
	    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	    .and()
	    .authorizeRequests()
	    .antMatchers("/calc").authenticated();
	}
	
}
