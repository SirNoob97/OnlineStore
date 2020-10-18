package com.sirnoob.authservice.service;

import java.util.UUID;

import com.sirnoob.authservice.domain.RefreshToken;
import com.sirnoob.authservice.exception.RefreshTokenNotFoundException;
import com.sirnoob.authservice.repository.IRefreshTokenRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Transactional
@Service
public class RefreshTokenServiceImpl implements IRefreshTokenService {

    private static final String TOKEN_NOT_FOUND = "Token Not Found!!";

    private final IRefreshTokenRepository iRefreshTokenRepository;

    @Override
    public Mono<String> generateRefreshToken() {
      return iRefreshTokenRepository.save(RefreshToken.builder().token(UUID.randomUUID().toString()).build())
                                    .map(RefreshToken::getToken);
    }

    @Override
    public Mono<String> validateRefreshToken(String token) {
      return iRefreshTokenRepository.findByToken(token)
                                    .switchIfEmpty(Mono.error(new RefreshTokenNotFoundException(TOKEN_NOT_FOUND)))
                                    .map(RefreshToken::getToken);
    }

    @Override
    public Mono<Void> deleteRefreshToken(String token) {
      return iRefreshTokenRepository.deleteByToken(token)
                                    .flatMap(num -> {
                                      return num > 0 ? Mono.empty()
                                                      : Mono.error(new RefreshTokenNotFoundException(TOKEN_NOT_FOUND));
                                  });
  }

}
