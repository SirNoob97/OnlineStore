package com.sirnoob.authservice.service;

import static com.sirnoob.authservice.util.Provider.PASSWORD;
import static com.sirnoob.authservice.util.Provider.TEST;
import static com.sirnoob.authservice.util.Provider.TOKEN;
import static com.sirnoob.authservice.util.Provider.generateLoginRequest;
import static com.sirnoob.authservice.util.Provider.generateRefreshToken;
import static com.sirnoob.authservice.util.Provider.generateRefreshTokenRequest;
import static com.sirnoob.authservice.util.Provider.generateSignUpRequest;
import static com.sirnoob.authservice.util.Provider.generateUserForSignUpTest;
import static com.sirnoob.authservice.util.Provider.getJwtExpirationTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.sirnoob.authservice.domain.Token;
import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AuthResponse;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.RefreshTokenRequest;
import com.sirnoob.authservice.dto.SignUpRequest;
import com.sirnoob.authservice.mapper.IUserMapper;
import com.sirnoob.authservice.repository.IUserRepository;
import com.sirnoob.authservice.security.JwtProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class AuthServiceTest {

  @Mock
  private ITokenService iRefreshTokenService;

  @Mock
  private IUserRepository iUserRepository;

  @Mock
  private IUserMapper iUserMapper;

  @Mock
  private JwtProvider jwtProvider;

  @Mock
  private PasswordEncoder passwordEncoder;

  private IAuthService iAuthService;


  private static final User staticUser = generateUserForSignUpTest();
  private static final Token staticRefreshToken = generateRefreshToken();
  private static final LoginRequest staticLoginRequest = generateLoginRequest();
  private static final SignUpRequest staticSignUpRequest = generateSignUpRequest();
  private static final RefreshTokenRequest staticRefreshTokenRequest = generateRefreshTokenRequest();

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    iAuthService = new AuthServiceImpl(iRefreshTokenService, iUserRepository, iUserMapper, jwtProvider, passwordEncoder);

    Mono<User> monoUser = Mono.just(staticUser);

    Mono<String> token = Mono.just(staticRefreshToken.getToken());

    BDDMockito.when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);

    BDDMockito.when(iUserMapper.mapSignUpRequestToUser(any(SignUpRequest.class))).thenReturn(staticUser);

    BDDMockito.when(iUserRepository.save(any(User.class))).thenReturn(monoUser);

    BDDMockito.when(iRefreshTokenService.generateRefreshToken()).thenReturn(token);

    BDDMockito.when(jwtProvider.generateAccessToken(any(User.class))).thenReturn(TOKEN);

    BDDMockito.when(jwtProvider.getJwtExpirationTime()).thenReturn(getJwtExpirationTime());

    BDDMockito.when(iUserRepository.findByUserName(anyString())).thenReturn(monoUser);

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
  @DisplayName("login return a MonoError ResponseStatusException when the repository returns an MonoEmpty")
  public void login_ReturnAMonoErrorResponseStatusException_WhenTheRepositoryReturnsAnMonoEmpty(){
    BDDMockito.when(iUserRepository.findByUserName(anyString())).thenReturn(Mono.empty());

    StepVerifier.create(iAuthService.login(staticLoginRequest))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
  }

  @Test
  @DisplayName("login return a MonoError ResponseStatusException when passwords do not match")
  public void login_ReturnAMonoErrorResponseStatusException_WhenPasswordsDoNotMatch(){
    BDDMockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

    StepVerifier.create(iAuthService.login(staticLoginRequest))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
  }

  @Test
  @DisplayName("refreshTokenRequest return a MonoAuthResponse when successful")
  public void refreshTokenRequest_ReturnAMonoAuthResponse_WhenSuccessful() {
    StepVerifier.create(iAuthService.refreshToken(staticRefreshTokenRequest))
                .expectSubscription()
                .assertNext(authResponse -> {
                  assertThat(authResponse.getClass()).isEqualTo(AuthResponse.class);
                  assertThat(authResponse.getUserName()).isEqualTo(TEST);
                  assertThat(authResponse.getRefreshToken()).isEqualTo(staticRefreshToken.getToken());
                }).verifyComplete();
  }
}
