package com.sirnoob.authservice.service;

import com.sirnoob.authservice.domain.Role;
import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.AuthResponse;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.RefreshTokenRequest;
import com.sirnoob.authservice.dto.SignUpRequest;
import com.sirnoob.authservice.repository.IUserRepository;
import com.sirnoob.authservice.security.JwtProvider;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements ReactiveUserDetailsService, IAuthService {

  private static final String USER_NOT_FOUND = "User Not Found!!";
  private static final String WRONG_PASSWORD = "Wrong Password!!";

  private final IRefreshTokenService iRefreshTokenService;
  private final IUserRepository iUserRepository;
  private final JwtProvider jwtProvider;
  private final PasswordEncoder passwordEncoder;

  @Transactional(readOnly = true)
  @Override
  public Mono<UserDetails> findByUsername(String username) {
    return iUserRepository.findByUserName(username)
                          .switchIfEmpty(getMonoError(HttpStatus.NOT_FOUND, USER_NOT_FOUND))
                          .cast(UserDetails.class);
  }

  @Transactional
  @Override
  public Mono<AuthResponse> signup(SignUpRequest signUpRequest) {
    return iUserRepository.save(mapSignUpRequestToUser(signUpRequest))
                          .flatMap(userDb -> iRefreshTokenService.generateRefreshToken()
                                                                  .flatMap(refreshToken -> createAuthResponse(userDb, refreshToken)));
  }

  @Transactional
  @Override
  public Mono<AuthResponse> login(LoginRequest loginRequest) {
    String password = loginRequest.getPassword();

    return findByUsername(loginRequest.getUserName()).cast(User.class)
                                                      .flatMap(user -> verifyPassword(user, password).flatMap(result -> iRefreshTokenService.generateRefreshToken()
                                                                                                      .flatMap(refreshToken -> createAuthResponse(user, refreshToken))));
  }

  @Override
  public Mono<AccountView> getCurrentUser() {
    return ReactiveSecurityContextHolder.getContext().map(sc -> sc.getAuthentication().getName())
                                                    .flatMap(name -> findByUsername(name))
                                                    .cast(User.class)
                                                    .map(user -> maptUserToAccountView(user));
  }

  @Transactional
  @Override
  public Mono<AuthResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
    return iRefreshTokenService.validateRefreshToken(refreshTokenRequest.getToken())
                                .flatMap(result -> findByUsername(refreshTokenRequest.getUserName())
                                .cast(User.class)
                                .flatMap(userDb -> createAuthResponse(userDb, result)));
  }

  private User mapSignUpRequestToUser(SignUpRequest signUpRequest) {
    return User.builder().userName(signUpRequest.getUserName())
                          .password(passwordEncoder.encode(signUpRequest.getPassword()))
                          .email(signUpRequest.getEmail())
                          .role(Role.CUSTOMER).build();
  }

  private Mono<AuthResponse> createAuthResponse(User user, String refreshToken) {
    return Mono.just(AuthResponse.builder().userName(user.getUsername())
                                            .authToken(jwtProvider.generateToken(user))
                                            .refreshToken(refreshToken)
                                            .expiresAt(jwtProvider.getJwtExpirationTime()).build());
  }

  private AccountView maptUserToAccountView (User user){
    return user.getRole().name().equals("CUSTOMER") ?  new AccountView(user.getUserId(), user.getUsername(), user.getEmail(), "")
                                                    :  new AccountView(user.getUserId(), user.getUsername(), user.getEmail(), user.getRole().name());
  }

  private Mono<Boolean> verifyPassword(User user, String password) {
    return passwordEncoder.matches(password, user.getPassword()) ? Mono.just(true)
                                                                 : getMonoError(HttpStatus.BAD_REQUEST, WRONG_PASSWORD);
  }

  private <T> Mono<T> getMonoError(HttpStatus status, String message) {
    return Mono.error(new ResponseStatusException(status, message));
  }
}
