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
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import io.r2dbc.spi.ConnectionFactory;
import reactor.test.StepVerifier;

@DataR2dbcTest
class TokenRepositoryTest {

  @Autowired
  private TokenRepository tokenRepository;

  @Autowired
  private ConnectionFactory connectionFactory;

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

    var template = new R2dbcEntityTemplate(connectionFactory);
    var dbClient = template.getDatabaseClient();

    StepVerifier.create(dbClient.sql(dropSql).then())
                .expectSubscription()
                .verifyComplete();

    StepVerifier.create(dbClient.sql(init).fetch().rowsUpdated())
                .expectNextCount(1)
                .verifyComplete();

    StepVerifier.create(template.insert(Token.class)
                                .using(staticToken)
                                .then())
                .expectSubscription()
                .verifyComplete();
  }

  @Test
  public void save_SavePersistAndReturnANewToken_WhereSuccessful(){
    Token tokenEntity = generateTokenEntity();
    String refreshToken = tokenEntity.getRefreshToken();

    StepVerifier.create(tokenRepository.deleteAll())
                .expectSubscription()
                .verifyComplete();

    StepVerifier.create(tokenRepository.save(tokenEntity))
                .expectSubscription()
                .assertNext(tokenDb -> {
                    assertThat(tokenDb).isNotNull();
                    assertThat(tokenDb.getId()).isNotNull();
                    assertThat(tokenDb.getRefreshToken()).isEqualTo(refreshToken);
                })
                .verifyComplete();
  }

  @Test
  public void save_ThrowIllegalArgumentException_WhenTokenIsNull() {
    assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> tokenRepository.save(null).subscribe());
  }

  @Test
  public void save_ThrowDataIntegrityViolationException_WhenTokenIsEmpty(){
    StepVerifier.create(tokenRepository.save(Token.builder().build()))
                .expectError(DataIntegrityViolationException.class)
                .verify();
  }

  @Test
  public void save_ThrowDataIntegrityViolationException_WhenThereIsAlreadyATokenSavedWithThoseTokens(){
    Token newToken = Token.builder()
                    .accessToken(staticToken.getAccessToken())
                    .refreshToken(staticToken.getRefreshToken()).build();

    StepVerifier.create(tokenRepository.save(newToken))
                .expectError(DataIntegrityViolationException.class)
                .verify();
  }

  @Test
  public void findByRefreshToken_ReturnATokenLog_WhenSuccessful(){
    String token = staticToken.getRefreshToken();

    StepVerifier.create(tokenRepository.findByRefreshToken(token))
                .expectSubscription()
                .assertNext(tokenDb -> {
                    assertThat(tokenDb).isNotNull();
                    assertThat(tokenDb.getId()).isNotNull();
                    assertThat(tokenDb.getRefreshToken()).isEqualTo(token);
                })
                .verifyComplete();
  }

  @Test
  public void findByRefreshToken_DoesNotThrowException_WhenTokenParameterIsNull(){
    StepVerifier.create(tokenRepository.findByRefreshToken(null))
                .expectSubscription()
                .expectComplete()
                .verify();
  }

  @Test
  public void findByRefreshToken_ReturnAMonoVoid_WhenThereIsNoTokensInTheRegistry(){
    String token = staticToken.getRefreshToken();
    Token defaultToken = new Token();

    StepVerifier.create(tokenRepository.deleteAll())
                .expectSubscription()
                .verifyComplete();

    StepVerifier.create(tokenRepository.findByRefreshToken(token).defaultIfEmpty(defaultToken))
                .expectSubscription()
                .assertNext(defaultT -> {
                    assertThat(defaultT).isEqualTo(defaultToken);
                })
                .verifyComplete();
  }

  @Test
  public void deleteByRefreshToken_DeleteRemoveAnTokenLog_WhenSuccessful(){
    String token = staticToken.getRefreshToken();

    StepVerifier.create(tokenRepository.deleteByRefreshToken(token))
                .expectNextMatches(num -> num.equals(1))
                .expectComplete()
                .verify();
  }

  @Test
  public void deleteByRefreshToken_ReturnZeroAndDoesNotThrowException_WhenTokenParameterIsNull(){
    StepVerifier.create(tokenRepository.deleteByRefreshToken(null))
                .expectSubscription()
                .assertNext(num -> assertThat(num).isGreaterThan(-1))
                .verifyComplete();
  }

  @Test
  public void deleteByRefreshToken_ReturnZero_WhenThereIsNoTokensInTheRegistry(){
    StepVerifier.create(tokenRepository.deleteAll())
                .expectSubscription()
                .verifyComplete();

    StepVerifier.create(tokenRepository.deleteByRefreshToken(staticToken.getRefreshToken()))
                .expectSubscription()
                .expectNextMatches(num -> num.equals(0))
                .verifyComplete();
  }
}
