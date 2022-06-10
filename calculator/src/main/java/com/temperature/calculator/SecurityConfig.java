package com.temperature.calculator;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/h2-console/**").hasRole("ADMIN").and().formLogin();

        http.authorizeRequests()
                .antMatchers("/**").hasRole("USER").and().formLogin();

        http.csrf().disable();
        http.headers().frameOptions().disable();
    }
}
