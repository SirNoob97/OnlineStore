package com.sirnoob.authservice.service;

import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.PasswordUpdateDto;
import com.sirnoob.authservice.exception.UserNotFoundException;
import com.sirnoob.authservice.repository.IUserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Transactional
@Service
public class AccountServiceImpl implements IAccountService {

  private final PasswordEncoder passwordEncoder;
  private final IUserRepository iUserRepository;

  @Override
  public Mono<String> persistAccount(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return iUserRepository.save(user).flatMap(u -> Mono.just("User " + u.getUsername() + " was successfully persisted!"));
  }

  @Override
  public Mono<Void> updatePassword(PasswordUpdateDto password) {
    return iUserRepository.updatePasswordById(password.getUserId(), passwordEncoder.encode(password.getPassword()))
                          .flatMap(num -> verifyOperation(num));
  }

  @Override
  public Mono<Void> deleteAccount(Long userId) {
    return iUserRepository.deleteByUserId(userId).flatMap(num -> verifyOperation(num));
  }

  @Transactional(readOnly = true)
  @Override
  public Flux<AccountView> getAllAccounts(){
    return iUserRepository.findAll().map(user -> new AccountView(user.getUserId(), user.getUsername(), user.getEmail(), user.getRole()));
  }

  public Mono<Void> verifyOperation(Integer num){
    if (num > 0) return Mono.empty();
    return Mono.error(new UserNotFoundException("User Not Found!"));
  }
}
