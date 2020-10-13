package com.sirnoob.authservice.repository;

import com.sirnoob.authservice.domain.User;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Mono;

public interface IUserRepository extends ReactiveCrudRepository<User, Long>{

  public Mono<User> findByUserName(String userName);

  public Mono<Integer> deleteByUserId(Long userId);

  @Modifying
  @Query("UPDATE users SET users.password = :password WHERE users.id = :userId")
  public Mono<Integer> updatePasswordById(Long userId, String password);
}
