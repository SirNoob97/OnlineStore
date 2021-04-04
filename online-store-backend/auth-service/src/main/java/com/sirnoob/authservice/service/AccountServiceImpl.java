package com.sirnoob.authservice.service;

import com.sirnoob.authservice.dto.AccountPayload;
import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.PasswordUpdateDto;
import com.sirnoob.authservice.mapper.UserMapper;
import com.sirnoob.authservice.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

  private static final String USER_NOT_FOUND = "User Not Found!!";
  private static final String NO_USERS_FOUND = "No Users Found!!";
  private static final String USER_SUCCESSFULLY_PERSISTED ="User was successfully persisted!";

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Transactional
  @Override
  public Mono<String> persist(AccountPayload accountPayload) {
    return userRepository.save(userMapper.accountPayloadToUser(accountPayload)).flatMap(u -> Mono.just(USER_SUCCESSFULLY_PERSISTED));
  }

  @Transactional
  @Override
  public Mono<Void> updatePassword(PasswordUpdateDto password) {
    return userRepository.updatePasswordById(password.getUserId(), passwordEncoder.encode(password.getPassword()))
      .flatMap(num -> verifyOperation(num));
  }

  @Transactional
  @Override
  public Mono<Void> delete(Long userId) {
    return userRepository.deleteByUserId(userId)
      .flatMap(num -> verifyOperation(num));
  }

  @Override
  public Flux<AccountView> getAll(){
    return userRepository.findAll()
           .switchIfEmpty(Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, NO_USERS_FOUND)))
           .map(user -> userMapper.userToAccountView(user));
  }

  private <T> Mono<T> verifyOperation(Integer num){
    return num > 0 ? Mono.empty() : Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
  }
}
