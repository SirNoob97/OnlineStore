package com.sirnoob.authservice.handler;

import static com.sirnoob.authservice.util.Provider.generateAuthResponse;
import static com.sirnoob.authservice.util.Provider.generateSignUpRequest;
import static org.mockito.ArgumentMatchers.any;

import com.sirnoob.authservice.dto.AuthResponse;
import com.sirnoob.authservice.dto.SignUpRequest;
import com.sirnoob.authservice.service.IAuthService;
import com.sirnoob.authservice.service.IRefreshTokenService;
import com.sirnoob.authservice.validator.ConstraintValidator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import reactor.core.publisher.Mono;

@WebFluxTest
class AuthHandlerTest {


  @MockBean
  private ConstraintValidator constraintValidator;

  @MockBean
  private IAuthService iAuthService;

  @MockBean
  private IRefreshTokenService iRefreshTokenService;

  @Autowired
  private WebTestClient webTestClient;


  @BeforeEach
  public void setUp(){
    Mono<AuthResponse> authResponse = Mono.just(generateAuthResponse());

    Mono<SignUpRequest> signUpRequest = Mono.just(generateSignUpRequest());

    BDDMockito.when(iAuthService.signup(any(SignUpRequest.class))).thenReturn(authResponse);

    BDDMockito.when(constraintValidator.validateRequest(any(SignUpRequest.class))).thenReturn(signUpRequest);
  }

  @Test
  public void name() {
    SignUpRequest signUpRequest = generateSignUpRequest();

    webTestClient.post()
                  .uri("/auth/signup")
                  .contentType(MediaType.APPLICATION_JSON)
                  .accept(MediaType.APPLICATION_JSON)
                  .body(BodyInserters.fromValue(signUpRequest))
                  .exchange()
                  .expectStatus().isCreated()
                  .expectBody(AuthResponse.class)
                  .value(authResponse -> {
                    Assertions.assertThat(authResponse).isNotNull();
                    Assertions.assertThat(authResponse.getUserName()).isEqualTo(signUpRequest.getUserName());
                  });
  }
}
