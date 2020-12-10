package com.sirnoob.configservice;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final JwtAuthFilter jwtAuthFilter;

  public SecurityConfig(JwtAuthFilter jwtAuthFilter){
    this.jwtAuthFilter = jwtAuthFilter;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .httpBasic().disable()
        .formLogin().disable()
        .authorizeRequests()
        .antMatchers("/registry-service/**").hasAnyAuthority("registry-service", "ADMIN")
        .antMatchers("/auth-service/**").hasAnyAuthority("auth-service", "ADMIN")
        .antMatchers("/product-service/**").hasAnyAuthority("product-service", "ADMIN")
        .antMatchers("/shopping-service/**").hasAnyAuthority("shopping-service", "ADMIN")
        .anyRequest().hasRole("ADMIN");

    http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    http.cors();
  }
}
