package com.sirnoob.authservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AccountPayload;
import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.PasswordUpdateDto;
import com.sirnoob.authservice.mapper.UserMapper;
import com.sirnoob.authservice.repository.UserRepository;
import static com.sirnoob.authservice.util.Provider.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class AccountServiceTest {

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  private AccountService accountService;

  private static final User user = generateUserStaticValues();
  private static final AccountView accountView = new AccountView(1L, TEST, TEST_EMAIL, EMPLOYEE);

  @BeforeEach
  public void setUp() {
    accountService = new AccountServiceImpl(passwordEncoder, userRepository, userMapper);

    Mono<User> monoUser = Mono.just(user);

    Flux<User> fluxUser = Flux.just(user);

    BDDMockito.when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);

    BDDMockito.when(userMapper.accountPayloadToUser(any(AccountPayload.class))).thenReturn(user);

    BDDMockito.when(userRepository.save(any(User.class))).thenReturn(monoUser);

    BDDMockito.when(userRepository.updatePasswordById(anyLong(), anyString())).thenReturn(Mono.just(1));

    BDDMockito.when(userRepository.deleteByUserId(anyLong())).thenReturn(Mono.just(1));

    BDDMockito.when(userMapper.userToAccountView(any(User.class))).thenReturn(accountView);

    BDDMockito.when(userRepository.findAll()).thenReturn(fluxUser);
  }

  @Test
  public void persistAccount_ReturnAConfirmationMessage_WhenSuccessful() {
    StepVerifier.create(accountService.persist(generateAccountPayloadStaticValues()))
                .expectSubscription()
                .expectNext("User was successfully persisted!")
                .verifyComplete();
  }

  @Test
  public void updatePassword_ReturnAMonoVoid_WheQueryOperationReturnAIntegerGreaterThanZero() {
    StepVerifier.create(accountService.updatePassword(new PasswordUpdateDto(1L, PASSWORD)))
                .expectSubscription()
                .verifyComplete();
  }

  @Test
  public void updatePassword_ReturnAMonoErrorResponseStatusException_WhenTheQueryOperationReturnsAnIntegerLessThanZero() {
    BDDMockito.when(userRepository.updatePasswordById(anyLong(), anyString())).thenReturn(Mono.just(0));

    StepVerifier.create(accountService.updatePassword(new PasswordUpdateDto(-1L, "")))
                .expectError(ResponseStatusException.class)
                .verify();
  }

  @Test
  public void deleteAccount_ReturnAMonoVoid_WheQueryOperationReturnAIntegerGreaterThanZero() {
    StepVerifier.create(accountService.delete(1L))
                .expectSubscription()
                .verifyComplete();
  }

  @Test
  public void deleteAccount_ReturnAMonoErrorResponseStatusException_WhenTheQueryOperationReturnsAnIntegerLessThanZero() {
    BDDMockito.when(userRepository.deleteByUserId(anyLong())).thenReturn(Mono.just(0));

    StepVerifier.create(accountService.delete(-1L))
                .expectError(ResponseStatusException.class)
                .verify();
  }

  @Test
  public void getAllAccounts_ReturnAAccountViewsFlux_WhenSuccessful() {
    StepVerifier.create(accountService.getAll())
                .expectSubscription()
                .expectNextCount(1)
                .expectComplete()
                .verify();
  }

  @Test
  public void getAllAccounts_ReturnAFluxErrorResponseStatusException_WhenThereIsNoUsersInTheRegistry() {
    BDDMockito.when(userRepository.findAll()).thenReturn(Flux.empty());

    StepVerifier.create(accountService.getAll())
                .expectError(ResponseStatusException.class)
                .verify();
  }
}
