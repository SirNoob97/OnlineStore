package com.sirnoob.gatewayservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity){
    return serverHttpSecurity.csrf().disable()
                              .formLogin().disable()
                              .httpBasic().disable()
                              .exceptionHandling()
                              .authenticationEntryPoint(
                                  (swe, authex) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                              .accessDeniedHandler(
                                  (swe, accdex) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                              .and()
                              .authorizeExchange()
                              .pathMatchers("/auth/**", "/accounts/**").permitAll()
                              .and()
                              .build();
  }
}
