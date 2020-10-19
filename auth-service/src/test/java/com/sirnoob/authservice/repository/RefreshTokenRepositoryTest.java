package com.sirnoob.authservice.repository;

import java.util.UUID;

import com.sirnoob.authservice.domain.RefreshToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.r2dbc.core.DatabaseClient;

import reactor.test.StepVerifier;

@DataR2dbcTest
@DisplayName("Refresh Token Repository Test")
public class RefreshTokenRepositoryTest {

  @Autowired
  private IRefreshTokenRepository iRefreshTokenRepository;

  @Autowired
  private DatabaseClient databaseClient;

  private static final RefreshToken staticRefreshToken = RefreshToken.builder().token(UUID.randomUUID().toString()).build();

  @BeforeEach
  private void setUp(){
    final String dropSql = "DROP TABLE IF EXISTS refresh_tokens";

    final String init = "CREATE TABLE IF NOT EXISTS refresh_tokens (" +
                              "id bigint NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT 1)," +
                              "token character varying(60) NOT NULL," +
                              "PRIMARY KEY(id)," +
                              "CONSTRAINT unique_token UNIQUE (token))";
    StepVerifier.create(databaseClient.execute(dropSql).then())
                .expectSubscription()
                .verifyComplete();

    StepVerifier.create(databaseClient.execute(init).fetch().rowsUpdated())
                .expectNextCount(1)
                .verifyComplete();

    StepVerifier.create(databaseClient.insert()
                                      .into(RefreshToken.class)
                                      .using(staticRefreshToken)
                                      .then())
                .expectSubscription()
                .verifyComplete();
  }

  @Test
  @DisplayName("save save/persist and return a new refresh token where successful")
  public void save_SavePersistAndReturnANewRefreshToken_WhereSuccessful(){
    final RefreshToken refreshToken = RefreshToken.builder().token(UUID.randomUUID().toString()).build();
    String token = refreshToken.getToken();

    StepVerifier.create(iRefreshTokenRepository.save(refreshToken))
                .expectSubscription()
                .assertNext(refreshTokenDb -> {
                    assertThat(refreshTokenDb).isNotNull();
                    assertThat(refreshTokenDb.getId()).isNotNull();
                    assertThat(refreshTokenDb.getToken()).isEqualTo(token);
                })
                .verifyComplete();
  }

  @Test
  @DisplayName("save throw IllegalArgumentException when refresh token is null")
  public void save_ThrowIllegalArgumentException_WhenRefreshTokenIsNull() {
    assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> iRefreshTokenRepository.save(null).subscribe());
  }

  @Test
  @DisplayName("save throw DataIntegrityViolationException when refresh token is empty")
  public void save_ThrowDataIntegrityViolationException_WhenRefreshTokenIsEmpty(){
    StepVerifier.create(iRefreshTokenRepository.save(RefreshToken.builder().build()))
                .expectError(DataIntegrityViolationException.class)
                .verify();
  }

  @Test
  @DisplayName("save throw DataIntegrityViolationException when there is already a refresh token saved with that token")
  public void save_ThrowDataIntegrityViolationException_WhenThereIsAlreadyARefreshTokenSavedWithThatToken(){
    StepVerifier.create(iRefreshTokenRepository.save(staticRefreshToken))
                .expectError(DataIntegrityViolationException.class)
                .verify();
  }

  @Test
  @DisplayName("findByToken return a refresh token log when successful")
  public void findByToken_ReturnARefreshTokenLog_WhenSuccessful(){
    String token = staticRefreshToken.getToken();

    StepVerifier.create(iRefreshTokenRepository.findByToken(token))
                .expectSubscription()
                .assertNext(refreshTokenDb -> {
                    assertThat(refreshTokenDb).isNotNull();
                    assertThat(refreshTokenDb.getId()).isNotNull();
                    assertThat(refreshTokenDb.getToken()).isEqualTo(token);
                })
                .verifyComplete();
  }

  @Test
  @DisplayName("findByToken does not throw exception when token parameter is null")
  public void findByToken_DoesNotThrowException_WhenTokenParameterIsNull(){
    StepVerifier.create(iRefreshTokenRepository.findByToken(null))
                .expectSubscription()
                .expectComplete()
                .verify();
  }

  @Test
  @DisplayName("findByToken return a mono void when there is no refresh tokens in the registry")
  public void findByToken_ReturnAMonoVoid_WhenThereIsNoRefreshTokensInTheRegistry(){
    String token = staticRefreshToken.getToken();
    RefreshToken defaultToken = RefreshToken.builder().id(0L).token("DEFAULT").build();

    StepVerifier.create(iRefreshTokenRepository.deleteAll())
                .expectSubscription()
                .verifyComplete();

    StepVerifier.create(iRefreshTokenRepository.findByToken(token).defaultIfEmpty(defaultToken))
                .expectSubscription()
                .assertNext(defaultT -> {
                    assertThat(defaultT).isEqualTo(defaultToken);
                    assertThat(defaultT.getToken()).isEqualTo(defaultToken.getToken());
                })
                .verifyComplete();
  }

  @Test
  @DisplayName("deleteByToken return an integer equals to 1 and delete/remove an refresh token log when successful")
  public void deleteByToken_DeleteRemoveAnRefreshTokenLog_WhenSuccessful(){
    String token = staticRefreshToken.getToken();

    StepVerifier.create(iRefreshTokenRepository.deleteByToken(token))
                .expectNextMatches(num -> num.equals(1))
                .expectComplete()
                .verify();
  }

  @Test
  @DisplayName("deleteByToken does not throw exception when token parameter is null")
  public void deleteByToken_ReturnZeroAndDoesNotThrowException_WhenTokenParameterIsNull(){
    StepVerifier.create(iRefreshTokenRepository.deleteByToken(null))
                .expectSubscription()
                .assertNext(num -> assertThat(num).isGreaterThan(-1))
                .verifyComplete();
  }

  @Test
  @DisplayName("deleteByToken return 0 when there is no refresh tokens in the registry")
  public void deleteByToken_ReturnZero_WhenThereIsNoRefreshTokensInTheRegistry(){
    StepVerifier.create(iRefreshTokenRepository.deleteAll())
                .expectSubscription()
                .verifyComplete();

    StepVerifier.create(iRefreshTokenRepository.deleteByToken(staticRefreshToken.getToken()))
                .expectSubscription()
                .expectNextMatches(num -> num.equals(0))
                .verifyComplete();
  }
}
