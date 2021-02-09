package com.sirnoob.authservice.service;

import java.util.UUID;

import com.sirnoob.authservice.domain.Token;
import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.RefreshTokenRequest;
import com.sirnoob.authservice.dto.SignUpRequest;
import com.sirnoob.authservice.mapper.IUserMapper;
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

  private final ITokenService iTokenService;
  private final IUserRepository iUserRepository;
  private final IUserMapper iUserMapper;
  private final JwtProvider jwtProvider;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Mono<UserDetails> findByUsername(String username) {
    return iUserRepository.findByUserName(username)
           .switchIfEmpty(getMonoError(HttpStatus.NOT_FOUND, USER_NOT_FOUND))
           .cast(UserDetails.class);
  }

  @Transactional
  @Override
  public Mono<String> signup(SignUpRequest signUpRequest) {
    return iUserRepository.save(iUserMapper.mapSignUpRequestToUser(signUpRequest))
           .flatMap(user -> persistToken(user, UUID.randomUUID().toString()));
  }

  @Override
  public Mono<String> login(LoginRequest loginRequest) {
    String password = loginRequest.getPassword();

    return findByUsername(loginRequest.getUserName()).cast(User.class)
           .flatMap(user -> verifyPassword(user, password))
           .flatMap(user -> persistToken(user, UUID.randomUUID().toString()));
  }

  @Override
  public Mono<AccountView> getCurrentUser() {
    return ReactiveSecurityContextHolder.getContext().map(sc -> sc.getAuthentication().getName())
           .flatMap(name -> findByUsername(name))
           .cast(User.class)
           .map(user -> iUserMapper.maptUserToAccountView(user));
  }

  @Override
  public Mono<String> refreshToken(RefreshTokenRequest refreshTokenRequest) {
    return iTokenService.validateRefreshToken(refreshTokenRequest.getToken())
           .flatMap(result -> findByUsername(refreshTokenRequest.getUserName())
           .cast(User.class)
           .flatMap(userDb -> persistToken(userDb, UUID.randomUUID().toString())));
  }

  private Mono<String> persistToken(User user, String issuer) {
    return Mono.just(buildTokenEntity(user, issuer))
          .flatMap(token -> iTokenService.persistToken(token));
  }

  private Token buildTokenEntity(User user, String issuer) {
  return Token.builder()
        .accessToken(jwtProvider.generateAccessToken(user, issuer))
        .refreshToken(jwtProvider.generateRefreshToken(user.getUsername(), issuer))
        .build();
  }

  private Mono<User> verifyPassword(User user, String password) {
    return passwordEncoder.matches(password, user.getPassword()) ? Mono.just(user)
                                                                 : getMonoError(HttpStatus.BAD_REQUEST, WRONG_PASSWORD);
  }

  private <T> Mono<T> getMonoError(HttpStatus status, String message) {
    return Mono.error(new ResponseStatusException(status, message));
  }
}
