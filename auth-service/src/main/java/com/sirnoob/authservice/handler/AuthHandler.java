package com.sirnoob.authservice.handler;

import com.sirnoob.authservice.domain.Token;
import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.SignUpRequest;
import com.sirnoob.authservice.service.IAuthService;
import com.sirnoob.authservice.service.ITokenService;
import com.sirnoob.authservice.validator.ConstraintValidator;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class AuthHandler {

  private static final MediaType JSON = MediaType.APPLICATION_JSON;
  private static final String CLEAR_SITE_DATA = "Clear-Site-Data";
  private static final String CLEAR_SITE_DATA_VALUES = "\"cache\", \"cookies\", \"storage\", \"executionContexts\"";

  private final ITokenService iTokenService;
  private final IAuthService iAuthService;
  private final ConstraintValidator constraintValidator;

  public Mono<ServerResponse> signup(ServerRequest serverRequest){
    return serverRequest.bodyToMono(SignUpRequest.class)
           .doOnNext(constraintValidator::validateRequest)
           .flatMap(iAuthService::signup)
           .flatMap(data -> buildServerResponse(HttpStatus.CREATED, data));
  }

  public Mono<ServerResponse> login(ServerRequest serverRequest){
    return serverRequest.bodyToMono(LoginRequest.class)
           .doOnNext(constraintValidator::validateRequest)
           .flatMap(iAuthService::login)
           .flatMap(data -> buildServerResponse(HttpStatus.OK, data));
  }

  public Mono<ServerResponse> getCurrentUser(ServerRequest serverRequest){
    return  ServerResponse.ok().contentType(JSON).body(iAuthService.getCurrentUser(), AccountView.class);
  }

  public Mono<ServerResponse> refreshToken(ServerRequest serverRequest){
    return getTokens(serverRequest.cookies())
           .flatMap(iAuthService::refreshToken)
           .flatMap(data -> buildServerResponse(HttpStatus.OK, data));
  }

  public Mono<ServerResponse> logout(ServerRequest serverRequest){
    return getTokens(serverRequest.cookies())
           .map(Token::getRefreshToken)
           .flatMap(token -> iTokenService.deleteToken(token))
           .then(ServerResponse.noContent().header(CLEAR_SITE_DATA, CLEAR_SITE_DATA_VALUES).build());
  }


  private Mono<ServerResponse> buildServerResponse(HttpStatus status, Token data){
    return ServerResponse.status(status)
          .cookie(buildCookie("JWT", data.getAccessToken()))
          .cookie(buildCookie("RT", data.getRefreshToken()))
          .build();
  }

  private Mono<Token> getTokens(MultiValueMap<String, HttpCookie> cookies){
    return Mono.just(Token.builder().refreshToken(cookies.getFirst("RT").getValue()).accessToken(cookies.getFirst("JWT").getValue()).build());
  }

  private ResponseCookie buildCookie(String name, String value) {
    return ResponseCookie.from(name, value).httpOnly(true).secure(true).sameSite("Strict").build();
  }
}
