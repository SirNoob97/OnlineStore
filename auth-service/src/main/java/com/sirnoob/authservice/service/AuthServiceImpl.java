package com.sirnoob.authservice.service;

import com.sirnoob.authservice.domain.Role;
import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AuthResponse;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.RefreshTokenRequest;
import com.sirnoob.authservice.dto.SignUpRequest;
import com.sirnoob.authservice.exception.WrongPasswordException;
import com.sirnoob.authservice.repository.IUserRepository;
import com.sirnoob.authservice.security.JwtProvider;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements ReactiveUserDetailsService, IAuthService {

  private final IRefreshTokenService iRefreshTokenService;
  private final IUserRepository iUserRepository;
  private final JwtProvider jwtProvider;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Mono<UserDetails> findByUsername(String username) {
    return iUserRepository.findByUserName(username)
                          .switchIfEmpty(Mono.error(new UsernameNotFoundException("User Not Found!")))
                          .cast(UserDetails.class);
  }

  @Override
  public Mono<AuthResponse> signup(SignUpRequest signUpRequest){
    return iUserRepository.save(mapSignUpRequestToUser(signUpRequest))
                          .flatMap(userDb -> iRefreshTokenService.generateRefreshToken()
                                                  .flatMap(refreshToken -> createAuthResponse(userDb, refreshToken)));
  }

  @Override
  public Mono<AuthResponse> login(LoginRequest loginRequest){
    String password = loginRequest.getPassword();

    return findByUsername(loginRequest.getUserName())
            .cast(User.class)
            .flatMap(user -> verifyPassword(user, password).flatMap(result -> iRefreshTokenService.generateRefreshToken()
                                                                                                  .flatMap(refreshToken -> createAuthResponse(user, refreshToken))));
  }

  @Override
  public Mono<AuthResponse> refreshToken(RefreshTokenRequest refreshTokenRequest){
    return iRefreshTokenService.validateRefreshToken(refreshTokenRequest.getToken())
                                .flatMap(result -> iUserRepository.findByUserName(refreshTokenRequest.getUserName())
                                                                  .flatMap(userDb -> createAuthResponse(userDb, result)));
  }



  private User mapSignUpRequestToUser(SignUpRequest signUpRequest){
    return User.builder()
               .userName(signUpRequest.getUserName())
               .password(passwordEncoder.encode(signUpRequest.getPassword()))
               .email(signUpRequest.getEmail())
               .role(Role.CUSTOMER).build();
  }

  private Mono<AuthResponse> createAuthResponse(User user, String refreshToken) {
    return Mono.just(AuthResponse.builder()
                                  .userName(user.getUsername())
                                  .authToken(jwtProvider.generateToken(user))
                                  .refreshToken(refreshToken)
                                  .expiresAt(jwtProvider.getJwtExpirationTime()).build());
  }

  private Mono<Boolean> verifyPassword(User user, String password){
    return passwordEncoder.matches(password, user.getPassword()) ? Mono.just(true) : Mono.error(new WrongPasswordException());
  }
}
