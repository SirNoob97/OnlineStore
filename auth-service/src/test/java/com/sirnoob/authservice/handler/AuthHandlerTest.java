package com.sirnoob.authservice.handler;

import static com.sirnoob.authservice.util.Provider.PASSWORD;
import static com.sirnoob.authservice.util.Provider.TOKEN;
import static com.sirnoob.authservice.util.Provider.generateRefreshTokenForIT;
import static com.sirnoob.authservice.util.Provider.generateSignUpRequest;
import static com.sirnoob.authservice.util.Provider.generateUserStaticValuesForIT;
import static com.sirnoob.authservice.util.Provider.getJwtExpirationTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Instant;

import com.sirnoob.authservice.configuration.Router;
import com.sirnoob.authservice.configuration.SecurityConfig;
import com.sirnoob.authservice.domain.RefreshToken;
import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AuthResponse;
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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

@TestInstance(Lifecycle.PER_CLASS)
@WebFluxTest
@Import({ SecurityConfig.class, AuthenticationManager.class, SecurityContextRepository.class, AuthServiceImpl.class,
    AccountServiceImpl.class, RefreshTokenServiceImpl.class, ConstraintValidator.class })
@ContextConfiguration(classes = { Router.class, AuthHandler.class, AccountHandler.class })
class AuthHandlerTest {

  @MockBean
  private IUserRepository iUserRepository;

  @MockBean
  private IRefreshTokenRepository iRefreshTokenRepository;

  @MockBean
  private JwtProvider jwtProvider;

  @MockBean
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ApplicationContext applicationContext;

  private WebTestClient webTestClient;

  @BeforeAll
  public void setUp() {
    webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    Mono<User> user = Mono.just(generateUserStaticValuesForIT());

    Mono<RefreshToken> refreshToken = Mono.just(generateRefreshTokenForIT());

    Instant expirationTime = getJwtExpirationTime();

    BDDMockito.when(iUserRepository.save(any(User.class))).thenReturn(user);

    BDDMockito.when(iRefreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

    BDDMockito.when(jwtProvider.generateToken(any(User.class))).thenReturn(TOKEN);

    BDDMockito.when(jwtProvider.getJwtExpirationTime()).thenReturn(expirationTime);

    BDDMockito.when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);
  }

  @Test
  @DisplayName("signup return an AuthResponse and 201 status when successful")
  public void signup_ReturnAAuthResponseAnd201Status_WhenSuccesful() {
    SignUpRequest signUpRequest = generateSignUpRequest();

    webTestClient.post()
                  .uri("/auth/signup")
                  .contentType(MediaType.APPLICATION_JSON)
                  .body(Mono.just(signUpRequest), SignUpRequest.class)
                  .exchange()
                  .expectStatus().isCreated()
                  .expectBody(AuthResponse.class)
                  .value(authResponse -> {
                    assertThat(authResponse).isNotNull();
                    assertThat(authResponse.getUserName()).isEqualTo(signUpRequest.getUserName());
                  });

    verify(iUserRepository, times(1)).save(any(User.class));
    verify(iRefreshTokenRepository, times(1)).save(any(RefreshToken.class));
    verify(jwtProvider, times(1)).generateToken(any(User.class));
    verify(jwtProvider, times(1)).getJwtExpirationTime();
    verify(passwordEncoder, times(1)).encode(anyString());
  }
}
