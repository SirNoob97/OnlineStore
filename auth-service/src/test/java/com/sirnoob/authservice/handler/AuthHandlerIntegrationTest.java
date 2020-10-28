package com.sirnoob.authservice.handler;

import static com.sirnoob.authservice.util.Provider.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sirnoob.authservice.configuration.Router;
import com.sirnoob.authservice.configuration.SecurityConfig;
import com.sirnoob.authservice.domain.RefreshToken;
import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.AuthResponse;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.RefreshTokenRequest;
import com.sirnoob.authservice.dto.SignUpRequest;
import com.sirnoob.authservice.mapper.IUserMapper;
import com.sirnoob.authservice.repository.IRefreshTokenRepository;
import com.sirnoob.authservice.repository.IUserRepository;
import com.sirnoob.authservice.security.AuthenticationManager;
import com.sirnoob.authservice.security.JwtProvider;
import com.sirnoob.authservice.security.SecurityContextRepository;
import com.sirnoob.authservice.service.AccountServiceImpl;
import com.sirnoob.authservice.service.AuthServiceImpl;
import com.sirnoob.authservice.service.RefreshTokenServiceImpl;
import com.sirnoob.authservice.validator.ConstraintValidator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

@WebFluxTest
@Import({ AuthServiceImpl.class, AccountServiceImpl.class, RefreshTokenServiceImpl.class, JwtProvider.class, ConstraintValidator.class })
@ContextConfiguration(classes = { SecurityConfig.class, AuthenticationManager.class, SecurityContextRepository.class,
                                  Router.class, AuthHandler.class, AccountHandler.class })
class AuthHandlerIntegrationTest {

  @MockBean
  private IUserRepository iUserRepository;

  @MockBean
  private IUserMapper iUserMapper;

  @MockBean
  private IRefreshTokenRepository iRefreshTokenRepository;

  @MockBean
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ApplicationContext applicationContext;

  private WebTestClient webTestClient;

  private static final User staticUser = generateUserStaticValuesForIT();
  private static final LoginRequest staticLoginRequest = generateLoginRequest();
  private static final RefreshTokenRequest staticRefreshTokenRequest = new RefreshTokenRequest(TOKEN, TEST);
  private static final AccountView staticAccountView = new AccountView(1L, TEST, TEST_EMAIL, ADMIN);

  @BeforeEach
  public void setUp() {
    webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();

    Mono<User> user = Mono.just(staticUser);

    Mono<RefreshToken> refreshToken = Mono.just(generateRefreshTokenForIT());

    BDDMockito.when(iUserMapper.mapSignUpRequestToUser(any(SignUpRequest.class))).thenReturn(staticUser);

    BDDMockito.when(iUserRepository.save(any(User.class))).thenReturn(user);

    BDDMockito.when(iRefreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

    BDDMockito.when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);

    BDDMockito.when(iUserMapper.maptUserToAccountView(any(User.class))).thenReturn(staticAccountView);

    BDDMockito.when(iUserRepository.findByUserName(anyString())).thenReturn(user);

    BDDMockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

    BDDMockito.when(iRefreshTokenRepository.findByToken(anyString())).thenReturn(refreshToken);

    BDDMockito.when(iRefreshTokenRepository.deleteByToken(anyString())).thenReturn(Mono.just(1));
  }

  @Test
  @DisplayName("signup return an AuthResponse and 201 status code when successful")
  public void signup_ReturnAAuthResponseAnd201StatusCode_WhenSuccesful() {
    SignUpRequest signUpRequest = generateSignUpRequest();

    webTestClient.post()
                  .uri("/auth/signup")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(signUpRequest), SignUpRequest.class)
                  .exchange()
                  .expectStatus().isCreated()
                  .expectHeader().contentType(JSON)
                  .expectBody(AuthResponse.class)
                  .value(authResponse -> {
                    assertThat(authResponse).isNotNull();
                    assertThat(authResponse.getAuthToken()).isNotNull();
                    assertThat(authResponse.getUserName()).isEqualTo(signUpRequest.getUserName());
                    assertThat(authResponse.getUserName()).isEqualTo(staticUser.getUsername());
                  });

    verify(iUserRepository, times(1)).save(any(User.class));
    verify(iRefreshTokenRepository, times(1)).save(any(RefreshToken.class));
    verify(iUserMapper, times(1)).mapSignUpRequestToUser(any(SignUpRequest.class));
  }

  @Test
  @DisplayName("signup return 400 status code when SignUpRequest has invalid fields")
  public void signup_Return400StatusCode_WhenSignUpRequestHasInvalidFields() {
    SignUpRequest invalidSignUpRequest = SignUpRequest.builder().userName("").email("").password("").build();

    webTestClient.post()
                  .uri("/auth/signup")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(invalidSignUpRequest), SignUpRequest.class)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectHeader().contentType(JSON)
                  .expectBody(Void.class);

    verify(iUserRepository, times(0)).save(any(User.class));
    verify(iRefreshTokenRepository, times(0)).save(any(RefreshToken.class));
    verify(passwordEncoder, times(0)).encode(anyString());
  }

  @Test
  @DisplayName("login return AuthResponseand 200 status code when successful")
  public void login_ReturnAuthResponseAnd200StatusCode_WhenSuccessful() {
    webTestClient.post()
                  .uri("/auth/login")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(staticLoginRequest), LoginRequest.class)
                  .exchange()
                  .expectStatus().isOk()
                  .expectHeader().contentType(JSON)
                  .expectBody(AuthResponse.class)
                  .value(authResponse -> {
                    assertThat(authResponse).isNotNull();
                    assertThat(authResponse.getAuthToken()).isNotNull();
                    assertThat(authResponse.getUserName()).isEqualTo(staticLoginRequest.getUserName());
                    assertThat(authResponse.getUserName()).isEqualTo(staticUser.getUsername());
                  });

    verify(iRefreshTokenRepository, times(1)).save(any(RefreshToken.class));
    verify(iUserRepository, times(1)).findByUserName(anyString());
    verify(passwordEncoder, times(1)).matches(anyString(), anyString());
  }

  @Test
  @DisplayName("login return 404 status code when user was not found in the registry")
  public void login_Return404StatusCode_WhenUserWasNotFoundInTheRegistry() {
    BDDMockito.when(iUserRepository.findByUserName(anyString())).thenReturn(Mono.empty());

    webTestClient.post()
                  .uri("/auth/login")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(staticLoginRequest), LoginRequest.class)
                  .exchange()
                  .expectStatus().isNotFound()
                  .expectHeader().contentType(JSON)
                  .expectBody(Void.class);

    verify(iUserRepository, times(1)).findByUserName(anyString());
    verify(iRefreshTokenRepository, times(0)).save(any(RefreshToken.class));
    verify(passwordEncoder, times(0)).matches(anyString(), anyString());
  }

  @Test
  @DisplayName("login return 400 status code when passwords does not match")
  public void login_Return400StatusCode_WhenPasswordsDoesNotMatch() {
    BDDMockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

    webTestClient.post()
                  .uri("/auth/login")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(staticLoginRequest), LoginRequest.class)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectHeader().contentType(JSON)
                  .expectBody(Void.class);

    verify(iUserRepository, times(1)).findByUserName(anyString());
    verify(passwordEncoder, times(1)).matches(anyString(), anyString());
    verify(iRefreshTokenRepository, times(0)).save(any(RefreshToken.class));
  }

  @Test
  @DisplayName("login return 400 status code when LoginRequest has invalid fields")
  public void login_Return400StatusCode_WhenLoginRequestHasInvalidFields() {
    LoginRequest invalidLoginRequest = new LoginRequest("", "");
    webTestClient.post()
                  .uri("/auth/login")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(invalidLoginRequest), LoginRequest.class)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectHeader().contentType(JSON)
                  .expectBody(Void.class);

    verify(iUserRepository, times(0)).findByUserName(anyString());
    verify(passwordEncoder, times(0)).matches(anyString(), anyString());
    verify(iRefreshTokenRepository, times(0)).save(any(RefreshToken.class));
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities = ADMIN)
  @DisplayName("getCurrentUser return 200 status code and MonoAccountView when successful")
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
  @DisplayName("getCurrentUser return 403 status code when user is not authenticated")
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
  @DisplayName("refreshToken return AuthResponse and 200 status code when successful")
  public void refreshToken_ReturnAuthResponseAnd200StatusCode_WhenSuccessful() {
    webTestClient.post()
                  .uri("/auth/refresh-token")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(staticRefreshTokenRequest), RefreshTokenRequest.class)
                  .exchange()
                  .expectStatus().isOk()
                  .expectHeader().contentType(JSON)
                  .expectBody(AuthResponse.class)
                  .value(authR -> {
                    assertThat(authR).isNotNull();
                    assertThat(authR.getUserName()).isNotNull();
                    assertThat(authR.getUserName()).isEqualTo(staticLoginRequest.getUserName());
                    assertThat(authR.getAuthToken()).isNotNull();
                  });

    verify(iRefreshTokenRepository, times(1)).findByToken(anyString());
    verify(iUserRepository, times(1)).findByUserName(anyString());
  }

  @Test
  @DisplayName("refreshToken return 403 status code when user is not authenticated")
  public void refreshToken_Return403StatusCode_WhenUserIsNotAuthenticated() {
    webTestClient.post()
                  .uri("/auth/refresh-token")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(staticRefreshTokenRequest), RefreshTokenRequest.class)
                  .exchange()
                  .expectStatus().isForbidden()
                  .expectBody(Void.class);

    verify(iRefreshTokenRepository, times(0)).save(any(RefreshToken.class));
    verify(iRefreshTokenRepository, times(0)).findByToken(anyString());
    verify(iUserRepository, times(0)).findByUserName(anyString());
    verify(passwordEncoder, times(0)).matches(anyString(), anyString());
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities = ADMIN)
  @DisplayName("refreshToken return 400 status code when RefreshTokenRequest has invalid fields")
  public void refreshToken_Return400StatusCode_WhenRefreshTokenRequestHasInvalidFields() {
    webTestClient.post()
                  .uri("/auth/refresh-token")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(new RefreshTokenRequest()), RefreshTokenRequest.class)
                  .exchange()
                  .expectStatus().isBadRequest()
                  .expectHeader().contentType(JSON)
                  .expectBody(Void.class);

    verify(iRefreshTokenRepository, times(0)).findByToken(anyString());
    verify(iUserRepository, times(0)).findByUserName(anyString());
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities = ADMIN)
  @DisplayName("logout return 204 status code when successful")
  public void logout_Return204StatusCode_WhenSuccessful() {
    webTestClient.post()
                  .uri("/auth/logout")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(staticRefreshTokenRequest), RefreshTokenRequest.class)
                  .exchange()
                  .expectStatus().isNoContent()
                  .expectBody(Void.class);

    verify(iRefreshTokenRepository, times(1)).deleteByToken(anyString());
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities = ADMIN)
  @DisplayName("logout return 404 status code when refresh token was not found")
  public void logout_Return404StatusCode_WhenRefreshTokenWasNotFound() {
    BDDMockito.when(iRefreshTokenRepository.deleteByToken(anyString())).thenReturn(Mono.just(0));

    webTestClient.post()
                  .uri("/auth/logout")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(staticRefreshTokenRequest), RefreshTokenRequest.class)
                  .exchange()
                  .expectStatus().isNotFound()
                  .expectBody(Void.class);

    verify(iRefreshTokenRepository, times(1)).deleteByToken(anyString());
  }

  @Test
  @DisplayName("logout return 403 status code when user is not authenticated")
  public void logout_Return403StatusCode_WhenUserIsNotAuthenticated() {
    webTestClient.post()
                  .uri("/auth/logout")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(staticRefreshTokenRequest), RefreshTokenRequest.class)
                  .exchange()
                  .expectStatus().isForbidden()
                  .expectBody(Void.class);

    verify(iRefreshTokenRepository, times(0)).deleteByToken(anyString());
  }
}
