package com.sirnoob.authservice.configuration;

import com.sirnoob.authservice.domain.Role;
import com.sirnoob.authservice.security.AuthenticationManager;
import com.sirnoob.authservice.security.SecurityContextRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

  private static final String ADMIN = Role.ADMIN.name();
  private static final String EMPLOYEE = Role.EMPLOYEE.name();

  private final AuthenticationManager authenticationManager;
  private final SecurityContextRepository securityContextRepository;

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity){
    //@formatter:off
    return serverHttpSecurity.csrf().disable()
                              .formLogin().disable()
                              .httpBasic().disable()
                              .exceptionHandling()
                              .authenticationEntryPoint(
                                  (swe, authex) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                              .accessDeniedHandler(
                                  (swe, accdex) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                              .and()
                              .authenticationManager(authenticationManager)
                              .securityContextRepository(securityContextRepository)
                              .authorizeExchange()
                              .pathMatchers(HttpMethod.GET, "/accounts").hasAuthority(ADMIN)
                              .pathMatchers(HttpMethod.POST, "/accounts").hasAuthority(ADMIN)
                              .pathMatchers(HttpMethod.PUT, "/accounts", "/accounts/**").authenticated()
                              .pathMatchers(HttpMethod.DELETE, "/accounts/**").authenticated()
                              .pathMatchers("/auth/users", "/auth/logout", "/auth/refresh-token").authenticated()
                              .pathMatchers("/auth/login", "/auth/signup").permitAll()
                              .pathMatchers(HttpMethod.POST, "/products").hasAuthority(ADMIN)
                              .pathMatchers(HttpMethod.PUT, "/products/**").hasAuthority(ADMIN)
                              .pathMatchers(HttpMethod.DELETE, "/products/**").hasAuthority(ADMIN)
                              .pathMatchers(HttpMethod.GET, "/products/responses", "/products/invoices").hasAnyAuthority(ADMIN, EMPLOYEE)
                              .pathMatchers(HttpMethod.POST, "/main-categories", "/sub-categories").hasAuthority(ADMIN)
                              .pathMatchers(HttpMethod.PUT, "/main-categories/**", "/sub-categories/**").hasAuthority(ADMIN)
                              .pathMatchers(HttpMethod.DELETE, "/main-categories/**", "/sub-categories/**").hasAuthority(ADMIN)
                              .pathMatchers(HttpMethod.GET, "/main-categories/{}", "/sub-categories/{}").hasAnyAuthority(ADMIN, EMPLOYEE)
                              .anyExchange().permitAll()
                              .and()
                              .build();
    //@formatter:on
  }

  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }
}
