package com.sirnoob.authservice.handler;

import static com.sirnoob.authservice.util.Provider.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import com.sirnoob.authservice.configuration.Router;
import com.sirnoob.authservice.configuration.SecurityConfig;
import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AccountPayload;
import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.PasswordUpdateDto;
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
import org.springframework.cloud.gateway.handler.predicate.HostRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest
@Import({ AuthServiceImpl.class, AccountServiceImpl.class, TokenServiceImpl.class, JwtProvider.class, ConstraintValidator.class })
@ContextConfiguration(classes = { SecurityConfig.class, AuthenticationManager.class, SecurityContextRepository.class,
                                  Router.class, RouteLocatorBuilder.class, PathRoutePredicateFactory.class, HostRoutePredicateFactory.class,
                                  AuthHandler.class, AccountHandler.class })
class AccountHandlerIntegrationTest{

  @MockBean
  private ITokenRepository iRefreshTokenRepository;

  @MockBean
  private IUserRepository iUserRepository;

  @MockBean
  private PasswordEncoder passwordEncoder;

  @MockBean
  private IUserMapper iUserMapper;

  @Autowired
  private ApplicationContext applicationContext;

  private WebTestClient webTestClient;

  private static final User staticUser = generateUserStaticValuesForIT();
  private static final AccountView staticAccountView = new AccountView(1L, TEST, TEST_EMAIL, EMPLOYEE);
  private static final PasswordUpdateDto staticPasswordUpdateDto = new PasswordUpdateDto(1L, PASSWORD);
  private static final AccountPayload staticAccountPayload = AccountPayload.builder().userName(NEW_USER).password(PASSWORD).email(TEST_EMAIL).role(EMPLOYEE).build();

  @BeforeEach
  public void  setUp() {
    webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();

    Mono<User> user = Mono.just(staticUser);

    BDDMockito.when(iUserRepository.findByUserName(TEST)).thenReturn(user);

    BDDMockito.when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);

    BDDMockito.when(iUserMapper.mapAccountPayloadToUser(any(AccountPayload.class))).thenReturn(staticUser);

    BDDMockito.when(iUserRepository.save(any(User.class))).thenReturn(user);

    BDDMockito.when(iUserMapper.maptUserToAccountView(any(User.class))).thenReturn(staticAccountView);

    BDDMockito.when(iUserRepository.findAll()).thenReturn(Flux.just(staticUser));

    BDDMockito.when(iUserRepository.deleteByUserId(anyLong())).thenReturn(Mono.just(1));

    BDDMockito.when(iUserRepository.updatePasswordById(anyLong(), anyString())).thenReturn(Mono.just(1));
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities  = ADMIN)
  public void createAccount_Return201StatusCodeAndAConfirmationMessage_WhenSuccesful(){
    webTestClient.post()
                  .uri("/accounts")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(staticAccountPayload), User.class)
                  .exchange()
                  .expectStatus().isCreated()
                  .expectHeader().contentType(JSON)
                  .expectBody(String.class);
  }

  @Test
  public void createAccount_Return403StatusCode_WhenUserIsNotAuhenticated(){
    webTestClient.post()
                  .uri("/accounts")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(staticAccountPayload), User.class)
                  .exchange()
                  .expectStatus().isForbidden();
  }


  @Test
  @WithMockUser(username = TEST, password = TEST, authorities  = EMPLOYEE)
  public void createAccount_Return401StatusCode_WhenAccessIsDenied(){
    webTestClient.post()
                  .uri("/accounts")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(staticAccountPayload), User.class)
                  .exchange()
                  .expectStatus().isUnauthorized();
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities  = ADMIN)
  public void updateAccount_Return200StatusCodeAndAConfirmationMessage_WhenSuccesful(){
    webTestClient.put()
                  .uri("/accounts")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(staticAccountPayload), User.class)
                  .exchange()
                  .expectStatus().isOk()
                  .expectHeader().contentType(JSON)
                  .expectBody(String.class);
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities  = ADMIN)
  public void updatePassword_Return204StatusCode_WhenSuccessful() {
    webTestClient.put()
                  .uri("/accounts/passwords")
                  .contentType(JSON)
                  .body(Mono.just(staticPasswordUpdateDto), PasswordUpdateDto.class)
                  .exchange()
                  .expectStatus().isNoContent();
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities  = ADMIN)
  public void updatePassword_Return404StatusCode_WhenRepositoryUpdateOperationReturnZero() {
    BDDMockito.when(iUserRepository.updatePasswordById(anyLong(), anyString())).thenReturn(Mono.just(0));

    webTestClient.put()
                  .uri("/accounts/passwords")
                  .contentType(JSON)
                  .body(Mono.just(staticPasswordUpdateDto), PasswordUpdateDto.class)
                  .exchange()
                  .expectStatus().isNotFound()
                  .expectHeader().contentType(JSON)
                  .expectBody(Void.class);
  }

  @Test
  public void updatePassword_Return403StatusCode_WhenUserIsNotAuthenticated() {
    webTestClient.put()
                  .uri("/accounts/passwords")
                  .contentType(JSON)
                  .body(Mono.just(staticPasswordUpdateDto), PasswordUpdateDto.class)
                  .exchange()
                  .expectStatus().isForbidden();
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities  = ADMIN)
  public void deleteByUserId_Return204StatusCode_WhenSuccessful() {
    webTestClient.delete()
                  .uri("/accounts/1")
                  .exchange()
                  .expectStatus().isNoContent();
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities  = ADMIN)
  public void deleteByUserId_Return404StatusCode_WhenRepositoryDeleteOperationReturnZero() {
    BDDMockito.when(iUserRepository.deleteByUserId(anyLong())).thenReturn(Mono.just(0));

    webTestClient.delete()
                  .uri("/accounts/1")
                  .exchange()
                  .expectStatus().isNotFound()
                  .expectHeader().contentType(JSON)
                  .expectBody(Void.class);
  }

  @Test
  public void deleteByUserId_Return403StatusCode_WhenUserIsNotAuthenticated() {
    webTestClient.delete()
                  .uri("/accounts/1")
                  .exchange()
                  .expectStatus().isForbidden();
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities  = ADMIN)
  public void getAllAccounts_Return200StatusCodeAndFluxUser_WhenSuccesful() {
    webTestClient.get()
                  .uri("/accounts")
                  .accept(JSON)
                  .exchange()
                  .expectHeader().contentType(JSON)
                  .expectStatus().isOk();
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities  = ADMIN)
  public void getAllAccounts_Return404StatusCode_WhenFluxUserIsEmpty() {
    BDDMockito.when(iUserRepository.findAll()).thenReturn(Flux.empty());

    webTestClient.get()
                  .uri("/accounts")
                  .accept(JSON)
                  .exchange()
                  .expectHeader().contentType(JSON)
                  .expectStatus().isNotFound()
                  .expectBody(Void.class);
  }

  @Test
  public void getAllAccounts_Return403StatusCode_WhenUserIsNotAuthenticated() {
    webTestClient.get()
                  .uri("/accounts")
                  .accept(JSON)
                  .exchange()
                  .expectStatus().isForbidden();
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities  = EMPLOYEE)
  public void getAllAccounts_Return401StatusCode_WhenAccesIsDenied() {
    BDDMockito.when(iUserRepository.findAll()).thenReturn(Flux.empty());

    webTestClient.get()
                  .uri("/accounts")
                  .accept(JSON)
                  .exchange()
                  .expectStatus().isUnauthorized();
  }
}
