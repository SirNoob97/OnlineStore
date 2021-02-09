package com.sirnoob.authservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static com.sirnoob.authservice.util.Provider.generateRefreshToken;
import com.sirnoob.authservice.domain.Token;
import com.sirnoob.authservice.repository.ITokenRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class RefreshTokenServiceTest {

    @Mock
    private ITokenRepository iRefreshTokenRepository;

    private ITokenService iRefreshTokenService;

    private static Token staticRefreshToken = generateRefreshToken();

    @BeforeEach
    public void setUp (){
      iRefreshTokenService = new TokenServiceImpl(iRefreshTokenRepository);

      Mono<Token> refreshToken = Mono.just(staticRefreshToken);

      BDDMockito.when(iRefreshTokenRepository.save(any(Token.class))).thenReturn(refreshToken);

      BDDMockito.when(iRefreshTokenRepository.findByRefreshToken(anyString())).thenReturn(refreshToken);

      BDDMockito.when(iRefreshTokenRepository.deleteByRefreshToken(anyString())).thenReturn(Mono.just(1));
    }

    @Test
    @DisplayName("generateRefreshToken return a string when successful")
    public void generateRefreshToken_ResturnAString_WhenSuccessful(){
      StepVerifier.create(iRefreshTokenService.generateRefreshToken())
                  .expectNext(staticRefreshToken.getToken())
                  .verifyComplete();
    }

    @Test
    @DisplayName("validateRefreshToken return a string when successful")
    public void validateRefreshToken_ReturnAString_WhenSuccessful(){
      String token = staticRefreshToken.getToken();

      StepVerifier.create(iRefreshTokenService.validateRefreshToken(token))
                  .expectNext(token)
                  .verifyComplete();
    }

    @Test
    @DisplayName("validateRefreshToken throw ResponseStatusException when the token was not found")
    public void validateRefreshToken_ReturnAMonoErrorResponseStatusException_WhenTheRepositoryReturnsAnMonoEmpty(){
      BDDMockito.when(iRefreshTokenRepository.findByRefreshToken(anyString())).thenReturn(Mono.empty());

      StepVerifier.create(iRefreshTokenService.validateRefreshToken(""))
                  .expectError(ResponseStatusException.class)
                  .verify();
    }

    @Test
    @DisplayName("deleteRefreshToken return a mono void when successful")
    public void deleteRefreshToken_ReturnAMonoVoid_WhenSuccessful(){
      StepVerifier.create(iRefreshTokenService.deleteToken(staticRefreshToken.getToken()))
                  .expectSubscription()
                  .verifyComplete();
    }

    @Test
    @DisplayName("deleteRefreshToken throw ResponseStatusException when the query operation returns 0")
    public void deleteRefreshToken_ReturnAMonoErrorResponseStatusException_WhenTheQueryOperationReturnsZero() {
      BDDMockito.when(iRefreshTokenRepository.deleteByRefreshToken(anyString())).thenReturn(Mono.just(0));

      StepVerifier.create(iRefreshTokenService.deleteToken(""))
                  .expectSubscription()
                  .expectError(ResponseStatusException.class)
                  .verify();
    }
}
