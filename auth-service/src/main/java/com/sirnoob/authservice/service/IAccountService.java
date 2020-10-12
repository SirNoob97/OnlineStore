package com.sirnoob.authservice.service;

import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.PasswordUpdateDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAccountService {

  public Mono<String> persistAccount(User user);

  public Mono<Void> updatePassword(PasswordUpdateDto password);

  public Mono<Void> deleteAccount(Long userId);

  public Flux<AccountView> getAllAccounts();
}
