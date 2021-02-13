package com.sirnoob.authservice.handler;

import static com.sirnoob.authservice.util.Provider.ADMIN;
import static com.sirnoob.authservice.util.Provider.JSON;
import static com.sirnoob.authservice.util.Provider.PASSWORD;
import static com.sirnoob.authservice.util.Provider.TEST;
import static com.sirnoob.authservice.util.Provider.TEST_EMAIL;
import static com.sirnoob.authservice.util.Provider.generateLoginRequest;
import static com.sirnoob.authservice.util.Provider.generateSignUpRequest;
import static com.sirnoob.authservice.util.Provider.generateTokenEntity;
import static com.sirnoob.authservice.util.Provider.generateUserStaticValuesForIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sirnoob.authservice.configuration.Router;
import com.sirnoob.authservice.configuration.SecurityConfig;
import com.sirnoob.authservice.domain.Token;
import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.SignUpRequest;
import com.sirnoob.authservice.mapper.IUserMapper;
import com.sirnoob.authservice.repository.ITokenRepository;
import com.sirnoob.authservice.repository.IUserRepository;
import com.sirnoob.authservice.security.AuthenticationManager;
import com.sirnoob.authservice.security.JwtProvider;
import com.sirnoob.authservice.security.SecurityContextRepository;
import com.sirnoob.authservice.service.AccountServiceImpl;
import com.sirnoob.authservice.service.AuthServiceImpl;
import com.sirnoob.authservice.service.TokenServiceImpl;
import com.sirnoob.authservice.validator.ConstraintValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

@WebFluxTest
@Import({ AuthServiceImpl.class, AccountServiceImpl.class, TokenServiceImpl.class, ConstraintValidator.class })
@ContextConfiguration(classes = { SecurityConfig.class, AuthenticationManager.class, SecurityContextRepository.class,
                                  Router.class, RouteLocatorBuilder.class, PathRoutePredicateFactory.class, AuthHandler.class, AccountHandler.class })
class AuthHandlerIntegrationTest {

  @MockBean
  private IUserRepository iUserRepository;

  @MockBean
  private IUserMapper iUserMapper;

  @MockBean
  private ITokenRepository iTokenRepository;

  @MockBean
  private PasswordEncoder passwordEncoder;

  @MockBean
  private JwtProvider jwtProvider;

  @Autowired
  private ApplicationContext applicationContext;

  private WebTestClient webTestClient;

  private static final User staticUser = generateUserStaticValuesForIT();
  private static final LoginRequest staticLoginRequest = generateLoginRequest();
  private static final AccountView staticAccountView = new AccountView(1L, TEST, TEST_EMAIL, ADMIN);
  private static final Token staticToken = generateTokenEntity();

  @BeforeEach
  public void setUp() {
    webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();

    Mono<User> user = Mono.just(staticUser);

    Mono<Token> token = Mono.just(staticToken);

    Mono<String> accessToken = Mono.just(staticToken.getAccessToken());

    BDDMockito.when(iUserRepository.findByUserName(anyString())).thenReturn(user);

    BDDMockito.when(iUserRepository.save(any(User.class))).thenReturn(user);

    BDDMockito.when(iUserMapper.mapSignUpRequestToUser(any(SignUpRequest.class))).thenReturn(staticUser);

    BDDMockito.when(iUserMapper.maptUserToAccountView(any(User.class))).thenReturn(staticAccountView);

    BDDMockito.when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);

    BDDMockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

    BDDMockito.when(iTokenRepository.save(any(Token.class))).thenReturn(token);

    BDDMockito.when(iTokenRepository.findByRefreshToken(anyString())).thenReturn(token);

    BDDMockito.when(iTokenRepository.deleteByRefreshToken(anyString())).thenReturn(Mono.just(1));

    BDDMockito.when(jwtProvider.validateToken(any(Token.class), anyString())).thenReturn(accessToken);

    BDDMockito.when(jwtProvider.getUsernameFromJwt(anyString())).thenReturn(TEST);

    BDDMockito.when(jwtProvider.generateAccessToken(any(User.class), anyString())).thenReturn(staticToken.getAccessToken());

    BDDMockito.when(jwtProvider.generateRefreshToken(anyString(), anyString())).thenReturn(staticToken.getRefreshToken());
  }

  @Test
  public void signup_ReturnAAuthResponseAnd201StatusCode_WhenSuccesful() {
    SignUpRequest signUpRequest = generateSignUpRequest();

    webTestClient.post()
                  .uri("/auth/signup")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(signUpRequest), SignUpRequest.class)
                  .exchange()
                  .expectStatus().isCreated()
                  .expectBody(Void.class);

    verify(iUserRepository, times(1)).save(any(User.class));
    verify(iTokenRepository, times(1)).save(any(Token.class));
    verify(iUserMapper, times(1)).mapSignUpRequestToUser(any(SignUpRequest.class));
  }

  @Test
  public void signup_Return400StatusCode_WhenSignUpRequestHasInvalidFields() {
    SignUpRequest invalidSignUpRequest = SignUpRequest.builder().userName("").email("").password("").build();

    webTestClient.post()
                  .uri("/auth/signup")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(invalidSignUpRequest), SignUpRequest.class)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(Void.class);

    verify(iUserRepository, times(0)).save(any(User.class));
    verify(iTokenRepository, times(0)).save(any(Token.class));
    verify(passwordEncoder, times(0)).encode(anyString());
  }

  @Test
  public void login_ReturnAuthResponseAnd200StatusCode_WhenSuccessful() {
    webTestClient.post()
                  .uri("/auth/login")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(staticLoginRequest), LoginRequest.class)
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody(Void.class);

    verify(iTokenRepository, times(1)).save(any(Token.class));
    verify(iUserRepository, times(1)).findByUserName(anyString());
    verify(passwordEncoder, times(1)).matches(anyString(), anyString());
  }

  @Test
  public void login_Return404StatusCode_WhenUserWasNotFoundInTheRegistry() {
    BDDMockito.when(iUserRepository.findByUserName(anyString())).thenReturn(Mono.empty());

    webTestClient.post()
                  .uri("/auth/login")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(staticLoginRequest), LoginRequest.class)
                  .exchange()
                  .expectStatus().isNotFound()
                  .expectBody(Void.class);

    verify(iUserRepository, times(1)).findByUserName(anyString());
    verify(iTokenRepository, times(0)).save(any(Token.class));
    verify(passwordEncoder, times(0)).matches(anyString(), anyString());
  }

  @Test
  public void login_Return400StatusCode_WhenPasswordsDoesNotMatch() {
    BDDMockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

    webTestClient.post()
                  .uri("/auth/login")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(staticLoginRequest), LoginRequest.class)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(Void.class);

    verify(iUserRepository, times(1)).findByUserName(anyString());
    verify(passwordEncoder, times(1)).matches(anyString(), anyString());
    verify(iTokenRepository, times(0)).save(any(Token.class));
  }

  @Test
  public void login_Return400StatusCode_WhenLoginRequestHasInvalidFields() {
    LoginRequest invalidLoginRequest = new LoginRequest("", "");
    webTestClient.post()
                  .uri("/auth/login")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(invalidLoginRequest), LoginRequest.class)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(Void.class);

    verify(iUserRepository, times(0)).findByUserName(anyString());
    verify(passwordEncoder, times(0)).matches(anyString(), anyString());
    verify(iTokenRepository, times(0)).save(any(Token.class));
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities = ADMIN)
  public void getCurrentUser_Return200StaticCodeAndMonoAccountView_WhenSuccessful() {
    webTestClient.get()
                  .uri("/auth/users")
                  .accept(JSON)
                  .exchange()
                  .expectStatus().isOk()
                  .expectHeader().contentType(JSON)
                  .expectBody(AccountView.class)
                  .value(ac -> {
                    assertThat(ac).isNotNull();
                    assertThat(ac.getRole()).isEqualTo(ADMIN);
                  });

    verify(iUserRepository, times(1)).findByUserName(anyString());
  }

  @Test
  public void getCurrentUser_Return403StaticCode_WhenUserIsNotAuthenticated() {
    webTestClient.get()
                  .uri("/auth/users")
                  .accept(JSON)
                  .exchange()
                  .expectStatus().isForbidden()
                  .expectBody(Void.class);

    verify(iUserRepository, times(0)).findByUserName(anyString());
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities = ADMIN)
  public void refreshToken_ReturnAuthResponseAnd200StatusCode_WhenSuccessful() {
    webTestClient.post()
                  .uri("/auth/refresh-token")
                  .cookie("JWT", staticToken.getAccessToken())
                  .cookie("RT", staticToken.getRefreshToken())
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody(Void.class);

    verify(iTokenRepository, times(1)).findByRefreshToken(anyString());
    verify(iUserRepository, times(1)).findByUserName(anyString());
  }

  @Test
  public void refreshToken_Return403StatusCode_WhenUserIsNotAuthenticated() {
    webTestClient.post()
                  .uri("/auth/refresh-token")
                  .cookie("JWT", staticToken.getAccessToken())
                  .cookie("RT", staticToken.getRefreshToken())
                  .exchange()
                  .expectStatus().isForbidden()
                  .expectBody(Void.class);

    verify(iTokenRepository, times(0)).save(any(Token.class));
    verify(iTokenRepository, times(0)).findByRefreshToken(anyString());
    verify(iUserRepository, times(0)).findByUserName(anyString());
    verify(passwordEncoder, times(0)).matches(anyString(), anyString());
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities = ADMIN)
  public void refreshToken_Return400StatusCode_WhenRefreshTokenRequestHasInvalidFields() {
    webTestClient.post()
                  .uri("/auth/refresh-token")
                  .cookie("JWT", "")
                  .cookie("RT", "")
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectBody(Void.class);

    verify(iTokenRepository, times(0)).findByRefreshToken(anyString());
    verify(iUserRepository, times(0)).findByUserName(anyString());
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities = ADMIN)
  public void logout_Return204StatusCode_WhenSuccessful() {
    webTestClient.post()
                  .uri("/auth/logout")
                  .cookie("JWT", staticToken.getAccessToken())
                  .cookie("RT", staticToken.getRefreshToken())
                  .exchange()
                  .expectStatus().isNoContent()
                  .expectBody(Void.class);

    verify(iTokenRepository, times(1)).deleteByRefreshToken(anyString());
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities = ADMIN)
  public void logout_Return404StatusCode_WhenRefreshTokenWasNotFound() {
    BDDMockito.when(iTokenRepository.deleteByRefreshToken(anyString())).thenReturn(Mono.just(0));

    webTestClient.post()
                  .uri("/auth/logout")
                  .cookie("JWT", staticToken.getAccessToken())
                  .cookie("RT", staticToken.getRefreshToken())
                  .exchange()
                  .expectStatus().isNotFound()
                  .expectBody(Void.class);

    verify(iTokenRepository, times(1)).deleteByRefreshToken(anyString());
  }

  @Test
  public void logout_Return403StatusCode_WhenUserIsNotAuthenticated() {
    webTestClient.post()
                  .uri("/auth/logout")
                  .cookie("JWT", staticToken.getAccessToken())
                  .cookie("RT", staticToken.getRefreshToken())
                  .exchange()
                  .expectStatus().isForbidden()
                  .expectBody(Void.class);

    verify(iTokenRepository, times(0)).deleteByRefreshToken(anyString());
  }
}
