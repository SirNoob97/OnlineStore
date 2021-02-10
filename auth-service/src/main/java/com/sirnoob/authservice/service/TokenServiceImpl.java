package com.sirnoob.authservice.service;

import com.sirnoob.authservice.domain.Token;
import com.sirnoob.authservice.repository.ITokenRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements ITokenService {

    private static final String TOKEN_NOT_FOUND = "Token Not Found!!";

    private final ITokenRepository iRefreshTokenRepository;

    @Transactional
    @Override
    public Mono<Token> persistToken(Token token) {
      return iRefreshTokenRepository.save(token);
    }

    @Override
    public Mono<Token> getTokensByRefreshToken(String token) {
      return iRefreshTokenRepository.findByRefreshToken(token)
             .switchIfEmpty(tokenNotFound());
    }

    @Transactional
    @Override
    public Mono<Void> deleteToken(String token) {
      return iRefreshTokenRepository.deleteByRefreshToken(token)
             .flatMap(num -> num > 0 ? Mono.empty() : tokenNotFound());
  }

  private <T> Mono<T> tokenNotFound(){
    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, TOKEN_NOT_FOUND));
  }
}
