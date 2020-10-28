package com.sirnoob.authservice.repository;

import com.sirnoob.authservice.domain.RefreshToken;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import reactor.core.publisher.Mono;

@Repository
public interface IRefreshTokenRepository extends ReactiveCrudRepository<RefreshToken, Long>{

  public Mono<RefreshToken> findByToken(String token);

  @Transactional
  public Mono<Integer> deleteByToken(String token);
}
