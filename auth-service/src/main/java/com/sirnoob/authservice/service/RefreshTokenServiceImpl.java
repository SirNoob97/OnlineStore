package com.sirnoob.authservice.service;

import java.util.UUID;

import com.sirnoob.authservice.domain.RefreshToken;
import com.sirnoob.authservice.repository.IRefreshTokenRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements IRefreshTokenService {

    private static final String TOKEN_NOT_FOUND = "Token Not Found!!";

    private final IRefreshTokenRepository iRefreshTokenRepository;

    @Transactional
    @Override
    public Mono<String> generateRefreshToken() {
      return iRefreshTokenRepository.save(RefreshToken.builder().token(UUID.randomUUID().toString()).build())
                                    .map(RefreshToken::getToken);
    }

    @Override
    public Mono<String> validateRefreshToken(String token) {
      return iRefreshTokenRepository.findByToken(token)
                                    .switchIfEmpty(tokenNotFound())
                                    .map(RefreshToken::getToken);
    }

    @Transactional
    @Override
    public Mono<Void> deleteRefreshToken(String token) {
      return iRefreshTokenRepository.deleteByToken(token)
                                    .flatMap(num -> num > 0 ? Mono.empty() : tokenNotFound());
  }

  private <T> Mono<T> tokenNotFound(){
    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, TOKEN_NOT_FOUND));
  }

}
