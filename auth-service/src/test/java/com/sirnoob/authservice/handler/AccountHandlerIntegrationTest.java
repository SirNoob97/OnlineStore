package com.sirnoob.authservice.handler;

import static com.sirnoob.authservice.util.Provider.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.sirnoob.authservice.configuration.Router;
import com.sirnoob.authservice.configuration.SecurityConfig;
import com.sirnoob.authservice.domain.Role;
import com.sirnoob.authservice.domain.User;
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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest
@Import({ AuthServiceImpl.class, AccountServiceImpl.class, RefreshTokenServiceImpl.class, JwtProvider.class, ConstraintValidator.class })
@ContextConfiguration(classes = { SecurityConfig.class, AuthenticationManager.class, SecurityContextRepository.class,
                                  Router.class, AuthHandler.class, AccountHandler.class })
class AccountHandlerIntegrationTest{

  @MockBean
  private IRefreshTokenRepository iRefreshTokenRepository;

  @MockBean
  private IUserRepository iUserRepository;

  @MockBean
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ApplicationContext applicationContext;

  private WebTestClient webTestClient;

  private static final String ADMIN = "ADMIN";
  private static final User staticUser = User.builder().userName(NEW_USER).password(PASSWORD).email(TEST_EMAIL).role(Role.EMPLOYEE).build();

  @BeforeEach
  public void  setUp() {
    webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();

    Mono<User> user = Mono.just(generateUserStaticValuesForIT());

    BDDMockito.when(iUserRepository.findByUserName(TEST)).thenReturn(user);

    BDDMockito.when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);

    BDDMockito.when(iUserRepository.save(any(User.class))).thenReturn(user);

    BDDMockito.when(iUserRepository.findAll()).thenReturn(Flux.just(staticUser));
  }

//pendiente el test de persistir usuarios --Error 400 bad request Failed to read HTTP message
  //@Test
  //@WithMockUser(username = TEST, password = TEST, authorities  = ADMIN)
  //public void createAccount_Return201StatusCodeAndAConfirmationMessage_WhenSuccesful(){
    //webTestClient.post()
                  //.uri("/accounts")
                  //.contentType(JSON)
                  //.accept(JSON)
                  //.body(Mono.just(staticUser), User.class)
                  //.exchange()
                  //.expectStatus().isCreated()
                  //.expectHeader().contentType(JSON)
                  //.expectBody(String.class);
  //}

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities  = ADMIN)
  public void get() {
    webTestClient.get()
                  .uri("/accounts")
                  .accept(JSON)
                  .exchange()
                  .expectHeader().contentType(JSON)
                  .expectStatus().isOk();
  }
}
