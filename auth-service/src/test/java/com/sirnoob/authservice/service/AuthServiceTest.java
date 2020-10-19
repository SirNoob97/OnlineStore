package com.sirnoob.authservice.service;

import static com.sirnoob.authservice.util.Provider.PASSWORD;
import static com.sirnoob.authservice.util.Provider.TEST;
import static com.sirnoob.authservice.util.Provider.generateRefreshToken;
import static com.sirnoob.authservice.util.Provider.generateRefreshTokenRequest;
import static com.sirnoob.authservice.util.Provider.generateSignUpRequest;
import static com.sirnoob.authservice.util.Provider.generateLoginRequest;
import static com.sirnoob.authservice.util.Provider.generateUserForSignUpTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.Instant;

import com.sirnoob.authservice.domain.RefreshToken;
import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AuthResponse;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.RefreshTokenRequest;
import com.sirnoob.authservice.dto.SignUpRequest;
import com.sirnoob.authservice.exception.WrongPasswordException;
import com.sirnoob.authservice.repository.IUserRepository;
import com.sirnoob.authservice.security.JwtProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class AuthServiceTest {

  @Value("${jwt.expiration.time}")
  private Long jwtExpirationInMillis;

  @Mock
  private IRefreshTokenService iRefreshTokenService;

  @Mock
  private IUserRepository iUserRepository;

  @Mock
  private JwtProvider jwtProvider;

  @Mock
  private PasswordEncoder passwordEncoder;

  private IAuthService iAuthService;


  private static final RefreshToken staticRefreshToken = generateRefreshToken();
  private static final LoginRequest staticLoginRequest = generateLoginRequest();
  private static final SignUpRequest staticSignUpRequest = generateSignUpRequest();
  private static final RefreshTokenRequest staticRefreshTokenRequest = generateRefreshTokenRequest();

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    iAuthService = new AuthServiceImpl(iRefreshTokenService, iUserRepository, jwtProvider, passwordEncoder);

    Mono<User> staticUser = Mono.just(generateUserForSignUpTest());

    Mono<String> token = Mono.just(staticRefreshToken.getToken());

    BDDMockito.when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);

    BDDMockito.when(iUserRepository.save(any(User.class))).thenReturn(staticUser);

    BDDMockito.when(iRefreshTokenService.generateRefreshToken()).thenReturn(token);

    BDDMockito.when(jwtProvider.generateToken(any(User.class))).thenReturn("TOKEN");

    BDDMockito.when(jwtProvider.getJwtExpirationTime()).thenReturn(Instant.now().plusMillis(jwtExpirationInMillis));

    BDDMockito.when(iUserRepository.findByUserName(anyString())).thenReturn(staticUser);

    BDDMockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

    BDDMockito.when(iRefreshTokenService.validateRefreshToken(anyString())).thenReturn(token);
  }

  @Test
  @DisplayName("signup return a Mono of AuthResponse when successful")
  public void signup_ReturnAMonoAuthResponse_WhenSuccessful(){
    StepVerifier.create(iAuthService.signup(staticSignUpRequest))
                .expectSubscription()
                .assertNext(authResponse -> {
                  assertThat(authResponse.getClass()).isEqualTo(AuthResponse.class);
                  assertThat(authResponse.getUserName()).isEqualTo(TEST);
                  assertThat(authResponse.getRefreshToken()).isEqualTo(staticRefreshToken.getToken());
                }).verifyComplete();
  }

  @Test
  @DisplayName("login return a Mono of AuthResponse when successful")
  public void login_ReturnAMonoAuthResponse_WhenSuccessful(){
    StepVerifier.create(iAuthService.login(staticLoginRequest))
                .expectSubscription()
                .assertNext(authResponse -> {
                  assertThat(authResponse.getClass()).isEqualTo(AuthResponse.class);
                  assertThat(authResponse.getUserName()).isEqualTo(TEST);
                  assertThat(authResponse.getRefreshToken()).isEqualTo(staticRefreshToken.getToken());
                }).verifyComplete();
  }

  @Test
  @DisplayName("login return a MonoError UsernameNotFoundException when the repository returns an MonoEmpty")
  public void login_ReturnAMonoErrorUserNameNotFoundException_WhenTheRepositoryReturnsAnMonoEmpty(){
    BDDMockito.when(iUserRepository.findByUserName(anyString())).thenReturn(Mono.empty());

    StepVerifier.create(iAuthService.login(staticLoginRequest))
                .expectSubscription()
                .expectError(UsernameNotFoundException.class)
                .verify();
  }

  @Test
  @DisplayName("login return a MonoError when passwords do not match")
  public void login_ReturnAMonoError_WhenPasswordsDoNotMatch(){
    BDDMockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

    StepVerifier.create(iAuthService.login(staticLoginRequest))
                .expectSubscription()
                .expectError(WrongPasswordException.class)
                .verify();
  }

  @Test
  @DisplayName("refreshTokenRequest return a MonoAuthResponse when successful")
  public void refreshTokenRequest_ReturnAMonoAuthResponse_WhenSuccessful() {
    StepVerifier.create(iAuthService.refreshToken(staticRefreshTokenRequest))
                .assertNext(authResponse -> {
                  assertThat(authResponse.getClass()).isEqualTo(AuthResponse.class);
                  assertThat(authResponse.getUserName()).isEqualTo(TEST);
                  assertThat(authResponse.getRefreshToken()).isEqualTo(staticRefreshToken.getToken());
                }).verifyComplete();
  }
}
