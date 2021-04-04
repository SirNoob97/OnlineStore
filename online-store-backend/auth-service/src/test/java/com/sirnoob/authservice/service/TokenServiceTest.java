package com.sirnoob.authservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static com.sirnoob.authservice.util.Provider.generateTokenEntity;
import com.sirnoob.authservice.domain.Token;
import com.sirnoob.authservice.repository.TokenRepository;

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
    private TokenRepository tokenRepository;

    private TokenService tokenService;

    private static Token staticToken = generateTokenEntity();

    @BeforeEach
    public void setUp (){
      tokenService = new TokenServiceImpl(tokenRepository);

      Mono<Token> token = Mono.just(staticToken);

      BDDMockito.when(tokenRepository.save(any(Token.class))).thenReturn(token);

      BDDMockito.when(tokenRepository.findByRefreshToken(anyString())).thenReturn(token);

      BDDMockito.when(tokenRepository.deleteByRefreshToken(anyString())).thenReturn(Mono.just(1));
    }

    @Test
    public void persistToken_ResturnAString_WhenSuccessful(){
      StepVerifier.create(tokenService.persist(staticToken))
                  .expectNext(staticToken)
                  .verifyComplete();
    }

    @Test
    public void getTokensByRefreshToken_ReturnAString_WhenSuccessful(){
      String token = staticToken.getRefreshToken();

      StepVerifier.create(tokenService.getByRefreshToken(token))
                  .expectNext(staticToken)
                  .verifyComplete();
    }

    @Test
    public void getTokensByRefreshToken_ReturnAMonoErrorResponseStatusException_WhenTheRepositoryReturnsAnMonoEmpty(){
      BDDMockito.when(tokenRepository.findByRefreshToken(anyString())).thenReturn(Mono.empty());

      StepVerifier.create(tokenService.getByRefreshToken(""))
                  .expectError(ResponseStatusException.class)
                  .verify();
    }

    @Test
    public void deleteToken_ReturnAMonoVoid_WhenSuccessful(){
      StepVerifier.create(tokenService.delete(staticToken.getRefreshToken()))
                  .expectSubscription()
                  .verifyComplete();
    }

    @Test
    public void deleteToken_ReturnAMonoErrorResponseStatusException_WhenTheQueryOperationReturnsZero() {
      BDDMockito.when(tokenRepository.deleteByRefreshToken(anyString())).thenReturn(Mono.just(0));

      StepVerifier.create(tokenService.delete(""))
                  .expectSubscription()
                  .expectError(ResponseStatusException.class)
                  .verify();
    }
}
