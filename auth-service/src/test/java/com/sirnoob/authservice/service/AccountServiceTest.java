package com.sirnoob.authservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.PasswordUpdateDto;
import com.sirnoob.authservice.exception.UserNotFoundException;
import com.sirnoob.authservice.repository.IUserRepository;
import static com.sirnoob.authservice.util.UserGenerator.generateUserStaticValues;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
public class AccountServiceTest {

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private IUserRepository iUserRepository;

  private IAccountService iAccountService;

  private static final String PASSWORD = "password";
  private static final User user = generateUserStaticValues();

  @BeforeEach
  public void setUp() {
    iAccountService = new AccountServiceImpl(passwordEncoder, iUserRepository);

    Mono<User> monoUser = Mono.just(user);

    Flux<User> fluxUser = Flux.just(user);

    BDDMockito.when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);

    BDDMockito.when(iUserRepository.save(any(User.class))).thenReturn(monoUser);

    BDDMockito.when(iUserRepository.updatePasswordById(anyLong(), anyString())).thenReturn(Mono.just(1));

    BDDMockito.when(iUserRepository.deleteByUserId(anyLong())).thenReturn(Mono.just(1));

    BDDMockito.when(iUserRepository.findAll()).thenReturn(fluxUser);
  }

  @Test
  @DisplayName("persistAccount save/update and return a user account when successful")
  public void persistAccount_SaveUpdateAndReturnAConfirmationMessage_WhenSuccessful() {
    StepVerifier.create(iAccountService.persistAccount(generateUserStaticValues()))
                .expectSubscription()
                .expectNext("User " + user.getUsername() + " was successfully persisted!")
                .verifyComplete();
  }

  @Test
  @DisplayName("updatePassword update password and return a mono void when query operation returns an integer greater than 0")
  public void updatePassword_UpdatePasswordAndReturnAMonoVoid_WheQueryOperationReturnAIntegerGreaterThanZero() {
    StepVerifier.create(iAccountService.updatePassword(new PasswordUpdateDto(1L, PASSWORD)))
                .expectSubscription()
                .verifyComplete();
  }

  @Test
  @DisplayName("updatePassword return a mono error UserNotFoundException when the query operation returns an integer less than zero")
  public void updatePassword_ReturnAMonoErrorOfUserNotFoundException_WhenTheQueryOperationReturnsAnIntegerLessThanZero() {
    BDDMockito.when(iUserRepository.updatePasswordById(anyLong(), anyString())).thenReturn(Mono.just(0));

    StepVerifier.create(iAccountService.updatePassword(new PasswordUpdateDto(0L, "")))
                .expectError(UserNotFoundException.class)
                .verify();
  }

  @Test
  @DisplayName("deleteAccount delete an account and return a mono void when query operation returns an integer greater than 0")
  public void deleteAccount_DeleteAnAccountAndReturnAMonoVoid_WheQueryOperationReturnAIntegerGreaterThanZero() {
    StepVerifier.create(iAccountService.deleteAccount(1L))
                .expectSubscription()
                .verifyComplete();
  }

  @Test
  @DisplayName("deleteAccount return a mono error UserNotFoundException when the query operation returns an integer less than zero")
  public void deleteAccount_ReturnAMonoErrorOfUserNotFoundException_WhenTheQueryOperationReturnsAnIntegerLessThanZero() {
    BDDMockito.when(iUserRepository.deleteByUserId(anyLong())).thenReturn(Mono.just(0));

    StepVerifier.create(iAccountService.deleteAccount(0L))
                .expectError(UserNotFoundException.class)
                .verify();
  }

  @Test
  @DisplayName("getAllAccounts return a users flux when successful")
  public void getAllAccounts_ReturnAAccountViewsFlux_WhenSuccessful() {
    AccountView account = new AccountView(user.getUserId(), user.getUsername(), user.getEmail(), user.getRole());

    StepVerifier.create(iAccountService.getAllAccounts())
                .expectSubscription()
                .expectNext(account)
                .expectComplete()
                .verify();
  }

  @Test
  @DisplayName("getAllAccounts return a flux error UserNotFoundException when there is no users in the registry")
  public void getAllAccounts_ReturnAFluxErrorUserNotFoundException_WhenThereIsNoUsersInTheRegistry() {
    BDDMockito.when(iUserRepository.findAll()).thenReturn(Flux.empty());

    StepVerifier.create(iAccountService.getAllAccounts())
                .expectError(UserNotFoundException.class)
                .verify();
  }
}