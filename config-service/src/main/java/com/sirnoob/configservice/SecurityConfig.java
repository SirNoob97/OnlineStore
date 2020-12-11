package com.sirnoob.configservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final JwtAuthFilter jwtAuthFilter;
  private final UserDetailsService userDetailsService;

  Logger log = LoggerFactory.getLogger(SecurityConfig.class);

  public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsService userDetailsService){
    this.jwtAuthFilter = jwtAuthFilter;
    this.userDetailsService = userDetailsService;
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
        .anyRequest().hasAnyAuthority("product-service", "ADMIN");

    http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
  }


  @Bean(BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Autowired
  public void configAuthManager(AuthenticationManagerBuilder authenticationManagerBuilder){
    try {
      authenticationManagerBuilder.userDetailsService(userDetailsService);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }
}
