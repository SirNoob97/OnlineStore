package com.sirnoob.authservice.repository;

import com.sirnoob.authservice.domain.User;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

public interface IUserRepository extends ReactiveCrudRepository<User, Long>{

  public Mono<User> findByUserName(String userName);
}
