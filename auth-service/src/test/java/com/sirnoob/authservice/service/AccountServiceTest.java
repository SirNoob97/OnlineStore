package com.sirnoob.authservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AccountPayload;
import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.PasswordUpdateDto;
import com.sirnoob.authservice.mapper.IUserMapper;
import com.sirnoob.authservice.repository.IUserRepository;
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
  private IUserRepository iUserRepository;

  @Mock
  private IUserMapper iUserMapper;

  private IAccountService iAccountService;

  private static final User user = generateUserStaticValues();
  private static final AccountView accountView = new AccountView(1L, TEST, TEST_EMAIL, EMPLOYEE);

  @BeforeEach
  public void setUp() {
    iAccountService = new AccountServiceImpl(passwordEncoder, iUserRepository, iUserMapper);

    Mono<User> monoUser = Mono.just(user);

    Flux<User> fluxUser = Flux.just(user);

    BDDMockito.when(passwordEncoder.encode(anyString())).thenReturn(PASSWORD);

    BDDMockito.when(iUserMapper.mapAccountPayloadToUser(any(AccountPayload.class))).thenReturn(user);

    BDDMockito.when(iUserRepository.save(any(User.class))).thenReturn(monoUser);

    BDDMockito.when(iUserRepository.updatePasswordById(anyLong(), anyString())).thenReturn(Mono.just(1));

    BDDMockito.when(iUserRepository.deleteByUserId(anyLong())).thenReturn(Mono.just(1));

    BDDMockito.when(iUserMapper.maptUserToAccountView(any(User.class))).thenReturn(accountView);

    BDDMockito.when(iUserRepository.findAll()).thenReturn(fluxUser);
  }

  @Test
  public void persistAccount_ReturnAConfirmationMessage_WhenSuccessful() {
    StepVerifier.create(iAccountService.persistAccount(generateAccountPayloadStaticValues()))
                .expectSubscription()
                .expectNext("User was successfully persisted!")
                .verifyComplete();
  }

  @Test
  public void updatePassword_ReturnAMonoVoid_WheQueryOperationReturnAIntegerGreaterThanZero() {
    StepVerifier.create(iAccountService.updatePassword(new PasswordUpdateDto(1L, PASSWORD)))
                .expectSubscription()
                .verifyComplete();
  }

  @Test
  public void updatePassword_ReturnAMonoErrorResponseStatusException_WhenTheQueryOperationReturnsAnIntegerLessThanZero() {
    BDDMockito.when(iUserRepository.updatePasswordById(anyLong(), anyString())).thenReturn(Mono.just(0));

    StepVerifier.create(iAccountService.updatePassword(new PasswordUpdateDto(-1L, "")))
                .expectError(ResponseStatusException.class)
                .verify();
  }

  @Test
  public void deleteAccount_ReturnAMonoVoid_WheQueryOperationReturnAIntegerGreaterThanZero() {
    StepVerifier.create(iAccountService.deleteAccount(1L))
                .expectSubscription()
                .verifyComplete();
  }

  @Test
  public void deleteAccount_ReturnAMonoErrorResponseStatusException_WhenTheQueryOperationReturnsAnIntegerLessThanZero() {
    BDDMockito.when(iUserRepository.deleteByUserId(anyLong())).thenReturn(Mono.just(0));

    StepVerifier.create(iAccountService.deleteAccount(-1L))
                .expectError(ResponseStatusException.class)
                .verify();
  }

  @Test
  public void getAllAccounts_ReturnAAccountViewsFlux_WhenSuccessful() {
    StepVerifier.create(iAccountService.getAllAccounts())
                .expectSubscription()
                .expectNextCount(1)
                .expectComplete()
                .verify();
  }

  @Test
  public void getAllAccounts_ReturnAFluxErrorResponseStatusException_WhenThereIsNoUsersInTheRegistry() {
    BDDMockito.when(iUserRepository.findAll()).thenReturn(Flux.empty());

    StepVerifier.create(iAccountService.getAllAccounts())
                .expectError(ResponseStatusException.class)
                .verify();
  }
}
