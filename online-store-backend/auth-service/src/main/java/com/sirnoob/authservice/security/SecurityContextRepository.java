package com.sirnoob.authservice.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

  private final String JWT_COOKIE_NAME = "JWT";

  private final AuthenticationManager authenticationManager;

  @Override
  public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
    throw new IllegalStateException("save method not supported");
  }

  @Override
  public Mono<SecurityContext> load(ServerWebExchange exchange) {
    var cookies = exchange.getRequest().getCookies();

    if (cookies != null && cookies.getFirst(JWT_COOKIE_NAME) != null) {
      var token = cookies.getFirst(JWT_COOKIE_NAME).getValue();
      var authentication = new UsernamePasswordAuthenticationToken(token, token);

      return authenticationManager.authenticate(authentication).map(SecurityContextImpl::new);
    }
    return Mono.empty();
  }

}
