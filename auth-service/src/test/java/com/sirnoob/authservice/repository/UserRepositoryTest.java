package com.sirnoob.authservice.repository;

import com.sirnoob.authservice.domain.Role;
import com.sirnoob.authservice.domain.User;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ContextConfiguration;

import reactor.test.StepVerifier;

@ContextConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@DataR2dbcTest
class UserRepositoryTest {

  @Autowired
  private DatabaseClient databaseClient;

  @Autowired
  private IUserRepository iUserRepository;

  @BeforeAll
  public void init(){
    final String initSql = "CREATE TABLE IF NOT EXISTS users (" +
              "id identity NOT NULL," +
              "user_name character varying(60) NOT NULL," +
              "password character varying(60) NOT NULL," +
              "email character varying(60) NOT NULL," +
              "role character varying(10) NOT NULL," +
              "check (role in ('CUSTOMER', 'EMPLOYEE', 'ADMIN')))";
    databaseClient.execute(initSql).fetch().rowsUpdated().as(StepVerifier::create).expectNextCount(1).verifyComplete();
  }

  @Test
  public void save_savePersistANewUser_WhenSuccessful() {
    iUserRepository.save(User.builder().userName("TEST").password("TEST").email("TEST@email.com").role(Role.EMPLOYEE).build())
                    .as(StepVerifier::create)
                    .expectNextCount(1)
                    .verifyComplete();
  }
}
