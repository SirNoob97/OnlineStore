package com.sirnoob.authservice.repository;

import com.sirnoob.authservice.domain.Token;

import static com.sirnoob.authservice.util.Provider.generateTokenEntity;
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
  @DisplayName("save save/persist and return a new refresh token where successful")
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
  @DisplayName("save throw IllegalArgumentException when refresh token is null")
  public void save_ThrowIllegalArgumentException_WhenRefreshTokenIsNull() {
    assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> iTokenRepository.save(null).subscribe());
  }

  @Test
  @DisplayName("save throw DataIntegrityViolationException when refresh token is empty")
  public void save_ThrowDataIntegrityViolationException_WhenRefreshTokenIsEmpty(){
    StepVerifier.create(iTokenRepository.save(Token.builder().build()))
                .expectError(DataIntegrityViolationException.class)
                .verify();
  }

  @Test
  @DisplayName("save throw DataIntegrityViolationException when there is already a refresh token saved with that token")
  public void save_ThrowDataIntegrityViolationException_WhenThereIsAlreadyARefreshTokenSavedWithThatToken(){
    Token newToken = Token.builder()
                    .accessToken(staticToken.getAccessToken())
                    .refreshToken(staticToken.getRefreshToken()).build();

    StepVerifier.create(iTokenRepository.save(newToken))
                .expectError(DataIntegrityViolationException.class)
                .verify();
  }

  @Test
  @DisplayName("findByToken return a refresh token log when successful")
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
  @DisplayName("findByToken does not throw exception when token parameter is null")
  public void findByToken_DoesNotThrowException_WhenTokenParameterIsNull(){
    StepVerifier.create(iTokenRepository.findByRefreshToken(null))
                .expectSubscription()
                .expectComplete()
                .verify();
  }

  @Test
  @DisplayName("findByToken return a mono void when there is no refresh tokens in the registry")
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
  @DisplayName("deleteByToken return an integer equals to 1 and delete/remove an refresh token log when successful")
  public void deleteByToken_DeleteRemoveAnRefreshTokenLog_WhenSuccessful(){
    String token = staticToken.getRefreshToken();

    StepVerifier.create(iTokenRepository.deleteByRefreshToken(token))
                .expectNextMatches(num -> num.equals(1))
                .expectComplete()
                .verify();
  }

  @Test
  @DisplayName("deleteByToken does not throw exception when token parameter is null")
  public void deleteByToken_ReturnZeroAndDoesNotThrowException_WhenTokenParameterIsNull(){
    StepVerifier.create(iTokenRepository.deleteByRefreshToken(null))
                .expectSubscription()
                .assertNext(num -> assertThat(num).isGreaterThan(-1))
                .verifyComplete();
  }

  @Test
  @DisplayName("deleteByToken return 0 when there is no refresh tokens in the registry")
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
