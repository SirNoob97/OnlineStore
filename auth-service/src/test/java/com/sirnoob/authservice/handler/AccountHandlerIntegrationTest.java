package com.sirnoob.authservice.handler;

import static com.sirnoob.authservice.util.Provider.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import com.sirnoob.authservice.configuration.Router;
import com.sirnoob.authservice.configuration.SecurityConfig;
import com.sirnoob.authservice.domain.Role;
import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AccountPayload;
import com.sirnoob.authservice.dto.PasswordUpdateDto;
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
  private static final PasswordUpdateDto staticPasswordUpdateDto = new PasswordUpdateDto(1L, PASSWORD);
  private static final AccountPayload staticAccountPayload = AccountPayload.builder().userName(NEW_USER).password(PASSWORD).email(TEST_EMAIL).role("EMPLOYEE").build();

  @BeforeEach
  public void  setUp() {
    webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();

    Mono<User> user = Mono.just(generateUserStaticValuesForIT());

    BDDMockito.when(iUserRepository.findByUserName(TEST)).thenReturn(user);

    BDDMockito.when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);

    BDDMockito.when(iUserRepository.save(any(User.class))).thenReturn(user);

    BDDMockito.when(iUserRepository.findAll()).thenReturn(Flux.just(staticUser));

    BDDMockito.when(iUserRepository.deleteByUserId(anyLong())).thenReturn(Mono.just(1));

    BDDMockito.when(iUserRepository.updatePasswordById(anyLong(), anyString())).thenReturn(Mono.just(1));
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities  = ADMIN)
  @DisplayName("createAccount return 201 status code and a confirmation message when succesful")
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
  @DisplayName("createAccount return 403 status code when access is denied")
  public void createAccount_Return403StatusCode_WhenAccesIsDenied(){
    webTestClient.post()
                  .uri("/accounts")
                  .contentType(JSON)
                  .accept(JSON)
                  .body(Mono.just(staticAccountPayload), User.class)
                  .exchange()
                  .expectStatus().isForbidden();
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities  = ADMIN)
  @DisplayName("updateAccount return 200 status code and a confirmation message when succesful")
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
  @DisplayName("updatePassword return 204 status code when successful")
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
  @DisplayName("updatePassword return 404 status code when repository update operation return 0")
  public void updatePassword_Return404StatusCode_WhenRepositoryUpdateOperationReturnZero() {
    BDDMockito.when(iUserRepository.updatePasswordById(anyLong(), anyString())).thenReturn(Mono.just(0));

    webTestClient.put()
                  .uri("/accounts/passwords")
                  .contentType(JSON)
                  .body(Mono.just(staticPasswordUpdateDto), PasswordUpdateDto.class)
                  .exchange()
                  .expectStatus().isNotFound();
  }

  @Test
  @DisplayName("updatePassword return 403 status code when access is denied")
  public void updatePassword_Return403StatusCode_WhenAccessIsDenied() {
    BDDMockito.when(iUserRepository.updatePasswordById(anyLong(), anyString())).thenReturn(Mono.just(0));

    webTestClient.put()
                  .uri("/accounts/passwords")
                  .contentType(JSON)
                  .body(Mono.just(staticPasswordUpdateDto), PasswordUpdateDto.class)
                  .exchange()
                  .expectStatus().isForbidden();
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities  = ADMIN)
  @DisplayName("deleteByUserId return 204 status code when successful")
  public void deleteByUserId_Return204StatusCode_WhenSuccessful() {
    webTestClient.delete()
                  .uri("/accounts/1")
                  .exchange()
                  .expectStatus().isNoContent();
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities  = ADMIN)
  @DisplayName("deleteByUserId return 404 status code when repository delete operation return 0")
  public void deleteByUserId_Return404StatusCode_WhenRepositoryDeleteOperationReturnZero() {
    BDDMockito.when(iUserRepository.deleteByUserId(anyLong())).thenReturn(Mono.just(0));

    webTestClient.delete()
                  .uri("/accounts/1")
                  .exchange()
                  .expectStatus().isNotFound();
  }

  @Test
  @DisplayName("getAllAccounts return 403 status code and when access is denied")
  public void deleteByUserId_Return403StatusCode_WhenAccessIsDenied() {
    webTestClient.delete()
                  .uri("/accounts/1")
                  .exchange()
                  .expectStatus().isForbidden();
  }

  @Test
  @WithMockUser(username = TEST, password = TEST, authorities  = ADMIN)
  @DisplayName("getAllAccounts return 200 status code and flux user when successful")
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
  @DisplayName("getAllAccounts return 404 status code and when flux of user is empty")
  public void getAllAccounts_Return404StatusCode_WhenFluxUserIsEmpty() {
    BDDMockito.when(iUserRepository.findAll()).thenReturn(Flux.empty());

    webTestClient.get()
                  .uri("/accounts")
                  .accept(JSON)
                  .exchange()
                  .expectHeader().contentType(JSON)
                  .expectStatus().isNotFound();
  }

  @Test
  @DisplayName("getAllAccounts return 403 status code and when access is denied")
  public void getAllAccounts_Return403StatusCode_WhenAccessIsDenied() {
    BDDMockito.when(iUserRepository.findAll()).thenReturn(Flux.empty());

    webTestClient.get()
                  .uri("/accounts")
                  .accept(JSON)
                  .exchange()
                  .expectStatus().isForbidden();
  }
}
