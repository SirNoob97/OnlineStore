package com.sirnoob.authservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static com.sirnoob.authservice.util.Provider.generateRefreshToken;
import com.sirnoob.authservice.domain.RefreshToken;
import com.sirnoob.authservice.repository.IRefreshTokenRepository;

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
    private IRefreshTokenRepository iRefreshTokenRepository;

    private IRefreshTokenService iRefreshTokenService;

    private static RefreshToken staticRefreshToken = generateRefreshToken();

    @BeforeEach
    public void setUp (){
      iRefreshTokenService = new RefreshTokenServiceImpl(iRefreshTokenRepository);

      Mono<RefreshToken> refreshToken = Mono.just(staticRefreshToken);

      BDDMockito.when(iRefreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

      BDDMockito.when(iRefreshTokenRepository.findByToken(anyString())).thenReturn(refreshToken);

      BDDMockito.when(iRefreshTokenRepository.deleteByToken(anyString())).thenReturn(Mono.just(1));
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
    @DisplayName("validateRefreshToken throw RefreshTokenNotFoundException when the token was not found")
    public void validateRefreshToken_ReturnAMonoErrorRefreshTokenNotFoundException_WhenTheRepositoryReturnsAnMonoEmpty(){
      BDDMockito.when(iRefreshTokenRepository.findByToken(anyString())).thenReturn(Mono.empty());

      StepVerifier.create(iRefreshTokenService.validateRefreshToken(""))
                  .expectError(ResponseStatusException.class)
                  .verify();
    }

    @Test
    @DisplayName("deleteRefreshToken return a mono void when successful")
    public void deleteRefreshToken_ReturnAMonoVoid_WhenSuccessful(){
      StepVerifier.create(iRefreshTokenService.deleteRefreshToken(staticRefreshToken.getToken()))
                  .expectSubscription()
                  .verifyComplete();
    }

    @Test
    @DisplayName("deleteRefreshToken throw RefreshTokenNotFoundException when the query operation returns 0")
    public void deleteRefreshToken_ReturnAMonoErrorRefreshTokenNotFoundException_WhenTheQueryOperationReturnsZero() {
      BDDMockito.when(iRefreshTokenRepository.deleteByToken(anyString())).thenReturn(Mono.just(0));

      StepVerifier.create(iRefreshTokenService.deleteRefreshToken(""))
                  .expectSubscription()
                  .expectError(ResponseStatusException.class)
                  .verify();
    }
}
