package com.sirnoob.authservice.repository;

import com.sirnoob.authservice.domain.User;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface IUserRepository extends R2dbcRepository<User, Long> {

  public Mono<User> findByUserName(String userName);

  public Mono<Integer> deleteByUserId(Long userId);

  @Modifying
  @Query("UPDATE users SET users.password = :password WHERE users.id = :userId")
  public Mono<Integer> updatePasswordById(Long userId, String password);
}
