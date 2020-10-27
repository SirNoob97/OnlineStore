package com.sirnoob.authservice.service;

import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.AuthResponse;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.RefreshTokenRequest;
import com.sirnoob.authservice.dto.SignUpRequest;

import reactor.core.publisher.Mono;

public interface IAuthService {

  public Mono<AuthResponse> signup(SignUpRequest signUpRequest);

  public Mono<AuthResponse> login(LoginRequest loginRequest);

  public Mono<AccountView> getCurrentUser();

  public Mono<AuthResponse> refreshToken(RefreshTokenRequest refreshTokenRequest);
}
