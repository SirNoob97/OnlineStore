package com.sirnoob.authservice.service;

import reactor.core.publisher.Mono;

public interface IRefreshTokenService {

  public Mono<String> generateRefreshToken();

  public Mono<String> validateRefreshToken(String token);

  public Mono<Void> deleteRefreshToken(String token);
}
