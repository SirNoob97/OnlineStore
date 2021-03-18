package com.sirnoob.authservice.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

  private final JwtProvider jwtProvider;

  @SuppressWarnings("unchecked")
  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
    String authToken = authentication.getCredentials().toString();

    String userName = jwtProvider.getUsernameFromJwt(authToken);

    if (jwtProvider.checkExpiration(authToken) && jwtProvider.checkHeaders(authToken)) {
      Claims claims = jwtProvider.getClaims(authToken);

      List<String> role = claims.get("role", List.class);

      var authorities = role.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

      var authenticationToken = new UsernamePasswordAuthenticationToken(userName, null, authorities);

      return Mono.just(authenticationToken);
    }

    return Mono.empty();
  }

}
