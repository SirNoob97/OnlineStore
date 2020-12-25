package com.sirnoob.authservice.configuration;

import com.sirnoob.authservice.domain.Role;
import com.sirnoob.authservice.security.AuthenticationManager;
import com.sirnoob.authservice.security.SecurityContextRepository;

import org.springframework.context.annotation.Bean;
import static org.springframework.http.HttpMethod.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.HeaderWriterServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.web.server.header.ClearSiteDataServerHttpHeadersWriter;
import org.springframework.security.web.server.header.ClearSiteDataServerHttpHeadersWriter.Directive;
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy;

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

    //ServerLogoutHandler securityContext = new SecurityContextServerLogoutHandler();
    //ClearSiteDataServerHttpHeadersWriter writer = new ClearSiteDataServerHttpHeadersWriter(Directive.ALL);
    //ServerLogoutHandler clearSiteData = new HeaderWriterServerLogoutHandler(writer);
    //DelegatingServerLogoutHandler logoutHandler = new DelegatingServerLogoutHandler(securityContext, clearSiteData);
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
                              .pathMatchers("/**/actuator/**", "/actuator/**").hasAuthority(ADMIN)
                              .pathMatchers(GET, "/accounts").hasAuthority(ADMIN)
                              .pathMatchers(POST, "/accounts").hasAuthority(ADMIN)
                              .pathMatchers(PUT, "/accounts", "/accounts/**").authenticated()
                              .pathMatchers(DELETE, "/accounts/**").authenticated()
                              .pathMatchers("/auth/users", "/auth/logout", "/auth/refresh-token").authenticated()
                              .pathMatchers("/auth/login", "/auth/signup").permitAll()
                              .pathMatchers(POST, "/products").hasAuthority(ADMIN)
                              .pathMatchers(PUT, "/products/**").hasAuthority(ADMIN)
                              .pathMatchers(DELETE, "/products/**").hasAuthority(ADMIN)
                              .pathMatchers(GET, "/products/responses", "/products/invoices").hasAnyAuthority(ADMIN, EMPLOYEE)
                              .pathMatchers(POST, "/main-categories", "/sub-categories").hasAuthority(ADMIN)
                              .pathMatchers(PUT, "/main-categories/**", "/sub-categories/**").hasAuthority(ADMIN)
                              .pathMatchers(DELETE, "/main-categories/**", "/sub-categories/**").hasAuthority(ADMIN)
                              .pathMatchers(GET, "/main-categories/{}", "/sub-categories/{}").hasAnyAuthority(ADMIN, EMPLOYEE)
                              .pathMatchers(POST, "/invoices").authenticated()
                              .pathMatchers(DELETE, "/invoices/**").hasAnyAuthority(ADMIN, EMPLOYEE)
                              .anyExchange().permitAll()
                              .and()
                              .headers(headers -> headers.referrerPolicy(reffer -> reffer.policy(ReferrerPolicy.SAME_ORIGIN)))
                              //.logout().logoutHandler(logoutHandler)
                                        //.logoutUrl("/auth/logout")
                              //.and()
                              .build();
    //@formatter:on
  }

  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }
}
