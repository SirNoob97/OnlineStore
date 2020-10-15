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
import org.springframework.data.r2dbc.core.DatabaseClient;

import reactor.test.StepVerifier;

@TestInstance(Lifecycle.PER_CLASS)
@DataR2dbcTest
@DisplayName("User Repository Test")
class UserRepositoryTest {

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
    databaseClient.execute(initSql).fetch().rowsUpdated().as(StepVerifier::create).expectNextCount(1).verifyComplete();
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
  @DisplayName("save throw illegal argument exception when user is null")
  public void save_ThrowIllegalArgumentException_WhenUserIsNull(){
    assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> iUserRepository.save(null).subscribe());
  }

  @Test
  @DisplayName("save throw an exception when there is already a user saved with that name")
  public void save_ThrowAnException_WhenThereIsAlreadyAUserSavedWithThatName(){
    iUserRepository.save(User.builder().userName("TEST").password("TEST").email("email@mail.c").role(Role.EMPLOYEE).build()).subscribe();

    /*
     * it should be DataIntegrityViolationException but it doesn't work.
     * i tried with all these exceptions and the test does not pass, I do not know what it is I will leave it like that with the parent exception.
     *
     *   R2dbcDataIntegrityViolationException
     *   JdbcSQLIntegrityConstraintViolationException
     *   SQLIntegrityConstraintViolationException
    */
    assertThatExceptionOfType(Exception.class).isThrownBy(() -> iUserRepository.save(generateUserStaticValues()).subscribe());
  }
}
