package com.sirnoob.authservice.service;

import static com.sirnoob.authservice.util.Provider.PASSWORD;
import static com.sirnoob.authservice.util.Provider.TEST;
import static com.sirnoob.authservice.util.Provider.generateTokenEntity;
import static com.sirnoob.authservice.util.Provider.generateLoginRequest;
import static com.sirnoob.authservice.util.Provider.generateSignUpRequest;
import static com.sirnoob.authservice.util.Provider.generateUserForSignUpTest;
import static com.sirnoob.authservice.util.Provider.getJwtExpirationTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.sirnoob.authservice.domain.Token;
import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.SignUpRequest;
import com.sirnoob.authservice.mapper.IUserMapper;
import com.sirnoob.authservice.repository.IUserRepository;
import com.sirnoob.authservice.security.JwtProvider;

import org.junit.jupiter.api.BeforeEach;
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
  private ITokenService iTokenService;

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
  private static final Token staticToken = generateTokenEntity();
  private static final LoginRequest staticLoginRequest = generateLoginRequest();
  private static final SignUpRequest staticSignUpRequest = generateSignUpRequest();

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    iAuthService = new AuthServiceImpl(iTokenService, iUserRepository, iUserMapper, jwtProvider, passwordEncoder);

    Mono<User> monoUser = Mono.just(staticUser);

    Mono<Token> token = Mono.just(staticToken);

    Mono<String> accessToken = Mono.just(staticToken.getAccessToken());

    BDDMockito.when(iUserMapper.mapSignUpRequestToUser(any(SignUpRequest.class))).thenReturn(staticUser);

    BDDMockito.when(iUserRepository.save(any(User.class))).thenReturn(monoUser);

    BDDMockito.when(iUserRepository.findByUserName(anyString())).thenReturn(monoUser);

    BDDMockito.when(iTokenService.persistToken(any(Token.class))).thenReturn(token);

    BDDMockito.when(iTokenService.getTokensByRefreshToken(anyString())).thenReturn(token);

    BDDMockito.when(iTokenService.deleteToken(anyString())).thenReturn(Mono.empty());

    BDDMockito.when(jwtProvider.generateAccessToken(any(User.class), anyString())).thenReturn(staticToken.getAccessToken());

    BDDMockito.when(jwtProvider.getJwtExpirationTime()).thenReturn(getJwtExpirationTime());

    BDDMockito.when(jwtProvider.validateToken(any(Token.class), anyString())).thenReturn(accessToken);

    BDDMockito.when(jwtProvider.getUsernameFromJwt(anyString())).thenReturn(TEST);

    BDDMockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

    BDDMockito.when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);
  }

  @Test
  public void signup_ReturnAMonoTokenEntity_WhenSuccessful(){
    StepVerifier.create(iAuthService.signup(staticSignUpRequest))
                .expectSubscription()
                .assertNext(token -> {
                  assertThat(token.getClass()).isEqualTo(Token.class);
                  assertThat(token.getAccessToken()).isEqualTo(staticToken.getAccessToken());
                  assertThat(token.getRefreshToken()).isEqualTo(staticToken.getRefreshToken());
                }).verifyComplete();
  }

  @Test
  public void login_ReturnAMonoTokenEntity_WhenSuccessful(){
    StepVerifier.create(iAuthService.login(staticLoginRequest))
                .expectSubscription()
                .assertNext(token -> {
                  assertThat(token.getClass()).isEqualTo(Token.class);
                  assertThat(token.getAccessToken()).isEqualTo(staticToken.getAccessToken());
                  assertThat(token.getRefreshToken()).isEqualTo(staticToken.getRefreshToken());
                }).verifyComplete();
  }

  @Test
  public void login_ReturnAMonoErrorResponseStatusException_WhenTheRepositoryReturnsAnMonoEmpty(){
    BDDMockito.when(iUserRepository.findByUserName(anyString())).thenReturn(Mono.empty());

    StepVerifier.create(iAuthService.login(staticLoginRequest))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
  }

  @Test
  public void login_ReturnAMonoErrorResponseStatusException_WhenPasswordsDoNotMatch(){
    BDDMockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

    StepVerifier.create(iAuthService.login(staticLoginRequest))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
  }

  @Test
  public void refreshToken_ReturnAMonoToken_WhenSuccessful() {
    StepVerifier.create(iAuthService.refreshToken(staticToken))
                .expectSubscription()
                .assertNext(token -> {
                  assertThat(token.getClass()).isEqualTo(Token.class);
                  assertThat(token.getAccessToken()).isEqualTo(staticToken.getAccessToken());
                  assertThat(token.getRefreshToken()).isEqualTo(staticToken.getRefreshToken());
                }).verifyComplete();
  }
}
