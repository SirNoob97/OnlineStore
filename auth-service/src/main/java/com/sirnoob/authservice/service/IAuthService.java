package com.sirnoob.authservice.service;

import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.RefreshTokenRequest;
import com.sirnoob.authservice.dto.SignUpRequest;

import reactor.core.publisher.Mono;

public interface IAuthService {

  public Mono<String> signup(SignUpRequest signUpRequest);

  public Mono<String> login(LoginRequest loginRequest);

  public Mono<AccountView> getCurrentUser();

  public Mono<String> refreshToken(RefreshTokenRequest refreshTokenRequest);
}
