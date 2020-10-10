package com.sirnoob.authservice.service;

import com.sirnoob.authservice.repository.IUserRepository;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class AuthService implements ReactiveUserDetailsService {

  private final IUserRepository iUserRepository;

  @Override
  public Mono<UserDetails> findByUsername(String username) {
    return iUserRepository.findByUserName(username).cast(UserDetails.class);
  }

}
