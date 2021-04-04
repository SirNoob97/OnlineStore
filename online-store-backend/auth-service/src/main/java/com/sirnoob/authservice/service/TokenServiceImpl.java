package com.sirnoob.authservice.service;

import com.sirnoob.authservice.domain.Token;
import com.sirnoob.authservice.repository.TokenRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements TokenService {

    private static final String TOKEN_NOT_FOUND = "Token Not Found!!";

    private final TokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public Mono<Token> persist(Token token) {
      return refreshTokenRepository.save(token);
    }

    @Override
    public Mono<Token> getByRefreshToken(String token) {
      return refreshTokenRepository.findByRefreshToken(token)
             .switchIfEmpty(tokenNotFound());
    }

    @Transactional
    @Override
    public Mono<Void> delete(String token) {
      return refreshTokenRepository.deleteByRefreshToken(token)
             .flatMap(num -> num > 0 ? Mono.empty() : tokenNotFound());
  }

  private <T> Mono<T> tokenNotFound(){
    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, TOKEN_NOT_FOUND));
  }
}
