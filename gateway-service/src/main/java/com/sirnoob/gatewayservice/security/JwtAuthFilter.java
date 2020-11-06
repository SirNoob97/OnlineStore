package com.sirnoob.gatewayservice.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;

import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;

//@Component
public class JwtAuthFilter implements ReactiveAuthenticationManager{

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
    String authToken = authentication.getCredentials().toString();
		return null;
  }

}
