package com.sirnoob.authservice.service;

import com.sirnoob.authservice.domain.Token;

import reactor.core.publisher.Mono;

public interface ITokenService {

  public Mono<String> persistToken(Token token);

  public Mono<String> validateRefreshToken(String token);

  public Mono<Void> deleteToken(String token);
}
