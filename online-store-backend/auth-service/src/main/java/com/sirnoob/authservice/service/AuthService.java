package com.sirnoob.authservice.service;

import com.sirnoob.authservice.domain.Token;
import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.SignUpRequest;

import reactor.core.publisher.Mono;

public interface AuthService {

  public Mono<Token> signup(SignUpRequest signUpRequest);

  public Mono<Token> login(LoginRequest loginRequest);

  public Mono<AccountView> currentUser();

  public Mono<Token> refreshToken(Token token);
}
