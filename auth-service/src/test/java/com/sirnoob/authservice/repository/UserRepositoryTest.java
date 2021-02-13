package com.sirnoob.authservice.repository;

import static com.sirnoob.authservice.util.Provider.generateUserRandomValues;
import static com.sirnoob.authservice.util.Provider.PASSWORD;
import static com.sirnoob.authservice.util.Provider.NEW_PASSWORD;
import static com.sirnoob.authservice.util.Provider.DEFAULT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.sirnoob.authservice.domain.Role;
import com.sirnoob.authservice.domain.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.r2dbc.core.DatabaseClient;

import reactor.test.StepVerifier;

@DataR2dbcTest
class UserRepositoryTest {

  @Autowired
  private IUserRepository iUserRepository;

  @Autowired
  private DatabaseClient databaseClient;


  private static final User staticUser = generateUserRandomValues(Role.EMPLOYEE);

  @BeforeEach
  public void init(){
    final String dropSql = "DROP TABLE IF EXISTS users";

    final String initSql = "CREATE TABLE IF NOT EXISTS users (" +
              "id identity NOT NULL," +
              "user_name character varying(60) NOT NULL UNIQUE," +
              "password character varying(60) NOT NULL," +
              "email character varying(60) NOT NULL UNIQUE," +
              "role character varying(10) NOT NULL," +
              "check (role in ('CUSTOMER', 'EMPLOYEE', 'ADMIN')))";

    StepVerifier.create(databaseClient.execute(dropSql).then())
                .expectSubscription()
                .verifyComplete();

    StepVerifier.create(databaseClient.execute(initSql).fetch().rowsUpdated())
                .expectNextCount(1)
                .verifyComplete();

    StepVerifier.create(databaseClient.insert()
                                      .into(User.class)
                                      .using(staticUser)
                                      .then())
                .verifyComplete();
  }

  @Test
  public void save_SavePersistANewUser_WhenSuccessful() {
    StepVerifier.create(iUserRepository.save(generateUserRandomValues(Role.EMPLOYEE)))
                .assertNext(user -> {
                  assertThat(user).isNotNull();
                  assertThat(user.getUserId()).isNotNull();
                })
                .verifyComplete();
  }

  @Test
  public void save_ThrowIllegalArgumentException_WhenUserIsNull(){
    assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> iUserRepository.save(null).subscribe());
  }

  @Test
  public void save_ThrowIllegalArgumentException_WhenUserIsEmpty(){
    StepVerifier.create(iUserRepository.save(User.builder().build()))
                .expectError(DataIntegrityViolationException.class)
                .verify();
  }

  @Test
  public void save_ThrowDataIntegrityViolationException_WhenThereIsAlreadyAUserSavedWithThatName(){
    User user = User.builder().userName(staticUser.getUsername()).password(PASSWORD).email("fake@email.com").role(Role.ADMIN).build();

    StepVerifier.create(iUserRepository.save(user))
                .expectError(DataIntegrityViolationException.class)
                .verify();
  }

  @Test
  public void save_ThrowDataIntegrityViolationException_WhenThereIsAlreadyAUserSavedWithThatEmail(){
    User user = User.builder().userName("name").password(PASSWORD).email(staticUser.getEmail()).role(Role.ADMIN).build();

    StepVerifier.create(iUserRepository.save(user))
                .expectError(DataIntegrityViolationException.class)
                .verify();
  }

  @Test
  public void findAll_ReturnAUsersFlux_WhenSuccesful() {
    StepVerifier.create(iUserRepository.findAll())
                .expectNextCount(1)
                .expectComplete()
                .verify();
  }

  @Test
  public void findAll_ReturnAFluxEmpty_WhenSuccesful_() {
    StepVerifier.create(iUserRepository.deleteAll())
                .expectSubscription()
                .verifyComplete();

    StepVerifier.create(iUserRepository.findAll())
                .expectSubscription()
                .expectNextCount(0)
                .expectComplete()
                .verify();
  }

  @Test
  public void findByUserName_ReturnAUser_WhenSuccesfful(){
    String name = staticUser.getUsername();
    StepVerifier.create(iUserRepository.findByUserName(name))
                .expectNextMatches(user -> user.getUsername().equals(name))
                .expectComplete()
                .verify();
  }

  @Test
  public void findByUserName_DoesNotThrowException_WhenNameParameterIsNull(){
    StepVerifier.create(iUserRepository.findByUserName(null))
                .expectSubscription()
                .expectComplete()
                .verify();
  }

  @Test
  public void findByUserName_ReturnADefaultUser_WhenTheFetchOperationReturnAnEmptyEntity(){
    String name = staticUser.getUsername();
    User defaultUser = User.builder().userId(0L).userName(DEFAULT).email(DEFAULT).role(Role.CUSTOMER).build();

    StepVerifier.create(iUserRepository.deleteAll())
                .expectSubscription()
                .verifyComplete();

    StepVerifier.create(iUserRepository.findByUserName(name).defaultIfEmpty(defaultUser))
                .expectSubscription()
                .assertNext(defaultU -> {
                    assertThat(defaultU).isEqualTo(defaultUser);
                    assertThat(defaultU.getUserId()).isEqualTo(0L);
                    assertThat(defaultU.getUsername()).isEqualTo(defaultUser.getUsername());
                })
                .verifyComplete();
  }

  @Test
  public void deleteByUserId_DeleteRemoveAnUserLog_WhenSuccessful(){
    Long userId = iUserRepository.save(generateUserRandomValues(Role.EMPLOYEE)).map(User::getUserId).block();

    StepVerifier.create(iUserRepository.deleteByUserId(userId))
                .expectNextMatches(num -> num.equals(1))
                .expectComplete()
                .verify();
  }

  @Test
  public void deleteByUserId_ReturnZeroAndDoesNotThrowException_WhenUserIdParameterIsNull(){
    StepVerifier.create(iUserRepository.deleteByUserId(null))
                .expectSubscription()
                .assertNext(num -> assertThat(num).isGreaterThan(-1))
                .verifyComplete();
  }

  @Test
  public void deleteByUserId_ReturnZero_WhenThereIsNoUsersInTheRegistry(){
    StepVerifier.create(iUserRepository.deleteAll())
                .expectSubscription()
                .verifyComplete();

    StepVerifier.create(iUserRepository.deleteByUserId(1L))
                .expectSubscription()
                .expectNextMatches(num -> num.equals(0))
                .verifyComplete();
  }

  @Test
  public void updatePasswordById_ReturnAIntegerEqualsToOneAndUpdateThePasswordOfAUserLog_WhenSuccessful(){
    User userDB = iUserRepository.findByUserName(staticUser.getUsername()).block();
    Long id = userDB.getUserId();
    String oldPassword = userDB.getPassword();

    StepVerifier.create(iUserRepository.updatePasswordById(id, NEW_PASSWORD))
                .expectNextMatches(num -> num.equals(1))
                .expectComplete()
                .verify();

    StepVerifier.create(iUserRepository.findByUserName(userDB.getUsername()))
                .expectNextMatches(user -> user.getPassword().equals(NEW_PASSWORD) && !user.getPassword().equals(oldPassword))
                .expectComplete()
                .verify();
  }

  @Test
  public void updatePasswordById_ReturnZeroAndDoesNotThrowException_WhenParametersAreNull(){
    StepVerifier.create(iUserRepository.updatePasswordById(null, null))
                .expectNextMatches(num -> num.equals(0))
                .expectComplete()
                .verify();
  }

  @Test
  public void updatePasswordById_ReturnZero_WhenThereIsNoUsersInTheRegistry(){
    StepVerifier.create(iUserRepository.deleteAll())
                .expectSubscription()
                .verifyComplete();

    StepVerifier.create(iUserRepository.updatePasswordById(1L, PASSWORD))
                .expectSubscription()
                .expectNextMatches(num -> num.equals(0))
                .verifyComplete();
  }
}
