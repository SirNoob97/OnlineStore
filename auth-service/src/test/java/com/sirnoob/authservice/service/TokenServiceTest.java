package com.sirnoob.authservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static com.sirnoob.authservice.util.Provider.generateTokenEntity;
import com.sirnoob.authservice.domain.Token;
import com.sirnoob.authservice.repository.ITokenRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class TokenServiceTest {

    @Mock
    private ITokenRepository iTokenRepository;

    private ITokenService iTokenService;

    private static Token staticToken = generateTokenEntity();

    @BeforeEach
    public void setUp (){
      iTokenService = new TokenServiceImpl(iTokenRepository);

      Mono<Token> token = Mono.just(staticToken);

      BDDMockito.when(iTokenRepository.save(any(Token.class))).thenReturn(token);

      BDDMockito.when(iTokenRepository.findByRefreshToken(anyString())).thenReturn(token);

      BDDMockito.when(iTokenRepository.deleteByRefreshToken(anyString())).thenReturn(Mono.just(1));
    }

    @Test
    public void generateRefreshToken_ResturnAString_WhenSuccessful(){
      StepVerifier.create(iTokenService.persistToken(staticToken))
                  .expectNext(staticToken)
                  .verifyComplete();
    }

    @Test
    public void validateRefreshToken_ReturnAString_WhenSuccessful(){
      String token = staticToken.getRefreshToken();

      StepVerifier.create(iTokenService.getTokensByRefreshToken(token))
                  .expectNext(staticToken)
                  .verifyComplete();
    }

    @Test
    public void validateRefreshToken_ReturnAMonoErrorResponseStatusException_WhenTheRepositoryReturnsAnMonoEmpty(){
      BDDMockito.when(iTokenRepository.findByRefreshToken(anyString())).thenReturn(Mono.empty());

      StepVerifier.create(iTokenService.getTokensByRefreshToken(""))
                  .expectError(ResponseStatusException.class)
                  .verify();
    }

    @Test
    public void deleteRefreshToken_ReturnAMonoVoid_WhenSuccessful(){
      StepVerifier.create(iTokenService.deleteToken(staticToken.getRefreshToken()))
                  .expectSubscription()
                  .verifyComplete();
    }

    @Test
    public void deleteRefreshToken_ReturnAMonoErrorResponseStatusException_WhenTheQueryOperationReturnsZero() {
      BDDMockito.when(iTokenRepository.deleteByRefreshToken(anyString())).thenReturn(Mono.just(0));

      StepVerifier.create(iTokenService.deleteToken(""))
                  .expectSubscription()
                  .expectError(ResponseStatusException.class)
                  .verify();
    }
}
