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
import com.sirnoob.authservice.dto.AuthResponse;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.SignUpRequest;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

@WebFluxTest
@Import({ AuthServiceImpl.class, AccountServiceImpl.class, RefreshTokenServiceImpl.class, JwtProvider.class, ConstraintValidator.class })
@ContextConfiguration(classes = { SecurityConfig.class, AuthenticationManager.class, SecurityContextRepository.class,
    Router.class, AuthHandler.class, AccountHandler.class })
class AuthHandlerTest {

  @MockBean
  private IUserRepository iUserRepository;

  @MockBean
  private IRefreshTokenRepository iRefreshTokenRepository;

  @MockBean
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ApplicationContext applicationContext;

  private WebTestClient webTestClient;

  private static final User staticUser = generateUserStaticValuesForIT();

  @BeforeEach
  public void setUp() {
    webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();

    Mono<User> user = Mono.just(staticUser);

    Mono<RefreshToken> refreshToken = Mono.just(generateRefreshTokenForIT());

    BDDMockito.when(iUserRepository.save(any(User.class))).thenReturn(user);

    BDDMockito.when(iRefreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

    BDDMockito.when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);

    BDDMockito.when(iUserRepository.findByUserName(anyString())).thenReturn(user);

    BDDMockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

    BDDMockito.when(iRefreshTokenRepository.findByToken(anyString())).thenReturn(refreshToken);
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
    verify(passwordEncoder, times(1)).encode(anyString());
  }

  @Test
  @DisplayName("login return AuthResponseand 200 status code when successful")
  public void login_ReturnAuthResponseAnd200StatusCode_WhenSuccessful() {
    LoginRequest loginRequest = generateLoginRequest();

    webTestClient.post()
                  .uri("/auth/login")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(loginRequest), LoginRequest.class)
                  .exchange()
                  .expectStatus().isOk()
                  .expectHeader().contentType(JSON)
                  .expectBody(AuthResponse.class)
                  .value(authResponse -> {
                    assertThat(authResponse).isNotNull();
                    assertThat(authResponse.getAuthToken()).isNotNull();
                    assertThat(authResponse.getUserName()).isEqualTo(loginRequest.getUserName());
                    assertThat(authResponse.getUserName()).isEqualTo(staticUser.getUsername());
                  });

    verify(iRefreshTokenRepository, times(1)).save(any(RefreshToken.class));
    verify(iUserRepository, times(1)).findByUserName(anyString());
    verify(passwordEncoder, times(1)).matches(anyString(), anyString());
  }
}
