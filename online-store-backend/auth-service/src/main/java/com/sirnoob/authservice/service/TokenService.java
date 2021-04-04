package com.sirnoob.authservice.service;

import com.sirnoob.authservice.domain.Token;

import reactor.core.publisher.Mono;

public interface TokenService {

  public Mono<Token> persist(Token token);

  public Mono<Token> getByRefreshToken(String token);

  public Mono<Void> delete(String token);
}
