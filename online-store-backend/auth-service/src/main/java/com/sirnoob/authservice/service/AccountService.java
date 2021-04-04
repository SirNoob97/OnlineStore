package com.sirnoob.authservice.service;

import com.sirnoob.authservice.dto.AccountPayload;
import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.PasswordUpdateDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {

  public Mono<String> persist(AccountPayload accountPayload);

  public Mono<Void> updatePassword(PasswordUpdateDto password);

  public Mono<Void> delete(Long userId);

  public Flux<AccountView> getAll();
}
