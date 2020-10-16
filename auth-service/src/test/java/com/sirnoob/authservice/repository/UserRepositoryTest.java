package com.sirnoob.authservice.repository;

import static com.sirnoob.authservice.util.UserGenerator.generateUserRandomValues;
import static com.sirnoob.authservice.util.UserGenerator.generateUserStaticValues;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.sirnoob.authservice.domain.Role;
import com.sirnoob.authservice.domain.User;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.r2dbc.core.DatabaseClient;

import reactor.test.StepVerifier;

@TestInstance(Lifecycle.PER_CLASS)
@DataR2dbcTest
@DisplayName("User Repository Test")
class UserRepositoryTest {

  private final String TEST = "TEST";

  @Autowired
  private IUserRepository iUserRepository;

  @Autowired
  private DatabaseClient databaseClient;

  @BeforeAll
  public void init(){
    final String initSql = "CREATE TABLE IF NOT EXISTS users (" +
              "id identity NOT NULL," +
              "user_name character varying(60) NOT NULL UNIQUE," +
              "password character varying(60) NOT NULL," +
              "email character varying(60) NOT NULL UNIQUE," +
              "role character varying(10) NOT NULL," +
              "check (role in ('CUSTOMER', 'EMPLOYEE', 'ADMIN')))";

    StepVerifier.create(databaseClient.execute(initSql).fetch().rowsUpdated())
                .expectNextCount(1)
                .verifyComplete();

    StepVerifier.create(databaseClient.insert()
                                      .into(User.class)
                                      .using(generateUserStaticValues())
                                      .then())
                .verifyComplete();
  }

  @Test
  @DisplayName("save save/persist and return a new user when successful")
  public void save_SavePersistANewUser_WhenSuccessful() {
    StepVerifier.create(iUserRepository.save(generateUserRandomValues(Role.EMPLOYEE)))
                .assertNext(user -> {
                  assertThat(user).isNotNull();
                  assertThat(user.getUserId()).isNotNull();
                })
                .verifyComplete();
  }

  @Test
  @DisplayName("save throw IllegalArgumentException when user is null")
  public void save_ThrowIllegalArgumentException_WhenUserIsNull(){
    assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> iUserRepository.save(null).subscribe());
  }

  @Test
  @DisplayName("save throw DataIntegrityViolationException when user is empty")
  public void save_ThrowIllegalArgumentException_WhenUserIsEmpty(){
    StepVerifier.create(iUserRepository.save(User.builder().build()))
                .expectError(DataIntegrityViolationException.class)
                .verify();
  }

  @Test
  @DisplayName("save throw DataIntegrityViolationException when there is already a user saved with that name")
  public void save_ThrowDataIntegrityViolationException_WhenThereIsAlreadyAUserSavedWithThatName(){
    User user = User.builder().userName(TEST).password("password").email("fake@email.com").role(Role.ADMIN).build();

    StepVerifier.create(iUserRepository.save(user))
                .expectError(DataIntegrityViolationException.class)
                .verify();
  }

  @Test
  @DisplayName("save throw DataIntegrityViolationException when there is already a user saved with that email")
  public void save_ThrowDataIntegrityViolationException_WhenThereIsAlreadyAUserSavedWithThatEmail(){
    User user = User.builder().userName("name").password("password").email("TEST@TEST.com").role(Role.ADMIN).build();

    StepVerifier.create(iUserRepository.save(user))
                .expectError(DataIntegrityViolationException.class)
                .verify();
  }

  @Test
  @DisplayName("findByUserName return a user when successful")
  public void findByUserName_ReturnAUser_WhenSuccesfful(){
    StepVerifier.create(iUserRepository.findByUserName(TEST))
                .expectNextMatches(user -> user.getUsername().equals(TEST))
                .expectComplete()
                .verify();
  }

  @Test
  @DisplayName("findByUserName does not throw exception when name parameter is null")
  public void findByUserName_DoesNotThrowException_WhenNameParameterIsNull(){
    StepVerifier.create(iUserRepository.findByUserName(null))
                .expectSubscription()
                .expectComplete()
                .verify();
  }

  @Test
  @DisplayName("deleteByUserId return an integer equals to 1 and delete/remove an user log when successful")
  public void deleteByUserId_DeleteRemoveAnUserLog_WhenSuccessful(){
    Long userId = iUserRepository.save(generateUserRandomValues(Role.EMPLOYEE)).map(User::getUserId).block();

    StepVerifier.create(iUserRepository.deleteByUserId(userId))
                .expectNextMatches(num -> num.equals(1))
                .expectComplete()
                .verify();
  }

  @Test
  @DisplayName("delete does not throw exception when user id parameter is null")
  public void deleteByUserId_ReturnZeroAndDoesNotThrowException_WhenUserIdParameterIsNull(){
    StepVerifier.create(iUserRepository.deleteByUserId(null))
                .expectNextMatches(num -> num.equals(0))
                .expectComplete()
                .verify();
  }

  @Test
  @DisplayName("updatePasswordById return an integer equals to 1 and update the password of a user log when successful")
  public void deleteByUserIdReturnAIntegerEqualsToOneAndUpdateThePasswordOfAUserLog_WhenSuccessful(){
    User userDB = iUserRepository.findByUserName(TEST).block();
    Long id = userDB.getUserId();
    String oldPassword = userDB.getPassword();

    StepVerifier.create(iUserRepository.updatePasswordById(id, "new Password"))
                .expectNextMatches(num -> num.equals(1))
                .expectComplete()
                .verify();

    StepVerifier.create(iUserRepository.findByUserName(TEST))
                .expectNextMatches(user -> user.getPassword().equals("new Password") && !user.getPassword().equals(oldPassword))
                .expectComplete()
                .verify();
  }

  @Test
  @DisplayName("updatePasswordById return 0 and does not throw exception when parameter are null")
  public void updatePasswordById_ReturnZeroAndDoesNotThrowException_WhenParametersAreNull(){
    StepVerifier.create(iUserRepository.updatePasswordById(null, null))
                .expectNextMatches(num -> num.equals(0))
                .expectComplete()
                .verify();
  }
}
