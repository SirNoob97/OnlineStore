package com.sirnoob.authservice.service;

import com.sirnoob.authservice.domain.Token;

import reactor.core.publisher.Mono;

public interface ITokenService {

  public Mono<Token> persistToken(Token token);

  public Mono<Token> getTokensByRefreshToken(String token);

  public Mono<Void> deleteToken(String token);
}
