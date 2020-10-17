package com.sirnoob.authservice.service;

import com.sirnoob.authservice.domain.Role;
import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AuthResponse;
import com.sirnoob.authservice.dto.LoginRequest;
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

  private final IUserRepository iUserRepository;
  private final JwtProvider jwtProvider;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Mono<UserDetails> findByUsername(String username) {
    return iUserRepository.findByUserName(username).cast(UserDetails.class);
  }

  @Override
  public Mono<AuthResponse> signup(SignUpRequest signUpRequest){
    User user = User.builder()
      .userName(signUpRequest.getUserName())
      .password(passwordEncoder.encode(signUpRequest.getPassword()))
      .email(signUpRequest.getEmail())
      .role(Role.CUSTOMER).build();

    return iUserRepository.save(user)
      .flatMap(result ->
          Mono.just(AuthResponse.builder().userName(result.getUsername()).authToken(jwtProvider.generateToken(result)).build())
      );
  }

  @Override
  public Mono<AuthResponse> login(LoginRequest loginRequest){
    String password = loginRequest.getPassword();

    return findByUsername(loginRequest.getUserName())
            .switchIfEmpty(Mono.error(new UsernameNotFoundException("User Not Found!")))
            .cast(User.class)
            .flatMap(user -> verifyPassword(user, password));
  }

  public Mono<AuthResponse> verifyPassword(User user, String password){
    if (passwordEncoder.matches(password, user.getPassword())) {
      return Mono.just(AuthResponse.builder().authToken(jwtProvider.generateToken(user)).userName(user.getUsername()).build());
    } else {
      return Mono.error(new WrongPasswordException());
    }
  }
}
