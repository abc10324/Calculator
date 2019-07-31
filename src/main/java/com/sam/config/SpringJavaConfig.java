package com.sam.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.mongodb.MongoClient;
import com.sam.model.AuthList;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;


@Configuration
@EnableWebMvc
@EnableWebSecurity
@EnableCaching
//@EnableScheduling
//@EnableSchedulerLock(defaultLockAtMostFor="PT30S")
@EnableAspectJAutoProxy
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
		
		// set this resource need to be access by https
		http.requiresChannel().antMatchers("/calc").requiresSecure();
	}
	
	@Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
          new ConcurrentMapCache("calcResult")));
        return cacheManager;
    }
	
}
