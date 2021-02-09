package com.sirnoob.authservice.repository;

import com.sirnoob.authservice.domain.Token;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface ITokenRepository extends ReactiveCrudRepository<Token, Long>{

  public Mono<Token> findByRefreshToken(String token);

  public Mono<Integer> deleteByRefreshToken(String token);
}
