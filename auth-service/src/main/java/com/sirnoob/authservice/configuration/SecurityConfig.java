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
                                  (swe, authex) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
                              )
                              .accessDeniedHandler(
                                  (swe, accdex) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                              )
                              .and()
                              .authenticationManager(authenticationManager)
                              .securityContextRepository(securityContextRepository)
                              .authorizeExchange()
                              .pathMatchers(HttpMethod.GET, "/accounts").hasAuthority(Role.ADMIN.name())
                              .pathMatchers(HttpMethod.POST, "/accounts").hasAuthority(Role.ADMIN.name())
                              .pathMatchers(HttpMethod.PUT, "/accounts", "/accounts/**").authenticated()
                              .pathMatchers(HttpMethod.DELETE, "/accounts/{userId}").authenticated()
                              .pathMatchers("/auth/users", "/auth/logout", "/auth/refresh-token").authenticated()
                              .pathMatchers("/auth/login", "/auth/signup").permitAll()
                              .and().build();
    //@formatter:on
  }

  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }
}
