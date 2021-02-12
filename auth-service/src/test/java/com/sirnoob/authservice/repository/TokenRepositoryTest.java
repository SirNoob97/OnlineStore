package com.sirnoob.authservice.repository;

import static com.sirnoob.authservice.util.Provider.generateTokenEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.sirnoob.authservice.domain.Token;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.r2dbc.core.DatabaseClient;

import reactor.test.StepVerifier;

@DataR2dbcTest
class TokenRepositoryTest {

  @Autowired
  private ITokenRepository iTokenRepository;

  @Autowired
  private DatabaseClient databaseClient;

  private static final Token staticToken = generateTokenEntity();

  @BeforeEach
  private void setUp(){
    final String dropSql = "DROP TABLE IF EXISTS tokens";

    final String init = "CREATE TABLE IF NOT EXISTS tokens (" +
      "id bigint NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT 1)," +
      "refresh_token character varying(1024) NOT NULL," +
      "access_token character varying(1024) NOT NULL," +
      "PRIMARY KEY(id)," +
      "CONSTRAINT unique_rtoken UNIQUE (refresh_token)," +
      "CONSTRAINT unique_atoken UNIQUE (access_token))";

    StepVerifier.create(databaseClient.execute(dropSql).then())
                .expectSubscription()
                .verifyComplete();

    StepVerifier.create(databaseClient.execute(init).fetch().rowsUpdated())
                .expectNextCount(1)
                .verifyComplete();

    StepVerifier.create(databaseClient.insert()
                                      .into(Token.class)
                                      .using(staticToken)
                                      .then())
                .expectSubscription()
                .verifyComplete();
  }

  @Test
  public void save_SavePersistAndReturnANewRefreshToken_WhereSuccessful(){
    Token tokenEntity = generateTokenEntity();
    String refreshToken = tokenEntity.getRefreshToken();

    StepVerifier.create(iTokenRepository.save(tokenEntity))
                .expectSubscription()
                .assertNext(tokenDb -> {
                    assertThat(tokenDb).isNotNull();
                    assertThat(tokenDb.getId()).isNotNull();
                    assertThat(tokenDb.getRefreshToken()).isEqualTo(refreshToken);
                })
                .verifyComplete();
  }

  @Test
  public void save_ThrowIllegalArgumentException_WhenRefreshTokenIsNull() {
    assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> iTokenRepository.save(null).subscribe());
  }

  @Test
  public void save_ThrowDataIntegrityViolationException_WhenRefreshTokenIsEmpty(){
    StepVerifier.create(iTokenRepository.save(Token.builder().build()))
                .expectError(DataIntegrityViolationException.class)
                .verify();
  }

  @Test
  public void save_ThrowDataIntegrityViolationException_WhenThereIsAlreadyARefreshTokenSavedWithThatToken(){
    Token newToken = Token.builder()
                    .accessToken(staticToken.getAccessToken())
                    .refreshToken(staticToken.getRefreshToken()).build();

    StepVerifier.create(iTokenRepository.save(newToken))
                .expectError(DataIntegrityViolationException.class)
                .verify();
  }

  @Test
  public void findByToken_ReturnARefreshTokenLog_WhenSuccessful(){
    String token = staticToken.getRefreshToken();

    StepVerifier.create(iTokenRepository.findByRefreshToken(token))
                .expectSubscription()
                .assertNext(tokenDb -> {
                    assertThat(tokenDb).isNotNull();
                    assertThat(tokenDb.getId()).isNotNull();
                    assertThat(tokenDb.getRefreshToken()).isEqualTo(token);
                })
                .verifyComplete();
  }

  @Test
  public void findByToken_DoesNotThrowException_WhenTokenParameterIsNull(){
    StepVerifier.create(iTokenRepository.findByRefreshToken(null))
                .expectSubscription()
                .expectComplete()
                .verify();
  }

  @Test
  public void findByToken_ReturnAMonoVoid_WhenThereIsNoRefreshTokensInTheRegistry(){
    String token = staticToken.getRefreshToken();
    Token defaultToken = new Token();

    StepVerifier.create(iTokenRepository.deleteAll())
                .expectSubscription()
                .verifyComplete();

    StepVerifier.create(iTokenRepository.findByRefreshToken(token).defaultIfEmpty(defaultToken))
                .expectSubscription()
                .assertNext(defaultT -> {
                    assertThat(defaultT).isEqualTo(defaultToken);
                })
                .verifyComplete();
  }

  @Test
  public void deleteByToken_DeleteRemoveAnRefreshTokenLog_WhenSuccessful(){
    String token = staticToken.getRefreshToken();

    StepVerifier.create(iTokenRepository.deleteByRefreshToken(token))
                .expectNextMatches(num -> num.equals(1))
                .expectComplete()
                .verify();
  }

  @Test
  public void deleteByToken_ReturnZeroAndDoesNotThrowException_WhenTokenParameterIsNull(){
    StepVerifier.create(iTokenRepository.deleteByRefreshToken(null))
                .expectSubscription()
                .assertNext(num -> assertThat(num).isGreaterThan(-1))
                .verifyComplete();
  }

  @Test
  public void deleteByToken_ReturnZero_WhenThereIsNoRefreshTokensInTheRegistry(){
    StepVerifier.create(iTokenRepository.deleteAll())
                .expectSubscription()
                .verifyComplete();

    StepVerifier.create(iTokenRepository.deleteByRefreshToken(staticToken.getRefreshToken()))
                .expectSubscription()
                .expectNextMatches(num -> num.equals(0))
                .verifyComplete();
  }
}
