package com.sirnoob.authservice.handler;

import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.RefreshTokenRequest;
import com.sirnoob.authservice.dto.SignUpRequest;
import com.sirnoob.authservice.service.IAuthService;
import com.sirnoob.authservice.service.IRefreshTokenService;
import com.sirnoob.authservice.validator.ConstraintValidator;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class AuthHandler {

  private static final MediaType JSON = MediaType.APPLICATION_JSON;

  private final IRefreshTokenService iRefreshTokenService;
  private final IAuthService iAuthService;
  private final ConstraintValidator constraintValidator;

  public Mono<ServerResponse> signup(ServerRequest serverRequest){
    return serverRequest.bodyToMono(SignUpRequest.class)
                        .doOnNext(constraintValidator::validateRequest)
                        .flatMap(iAuthService::signup)
                        .flatMap(data -> getServerResponse(HttpStatus.CREATED, data));
  }

  public Mono<ServerResponse> login(ServerRequest serverRequest){
    return serverRequest.bodyToMono(LoginRequest.class)
                        .doOnNext(constraintValidator::validateRequest)
                        .flatMap(iAuthService::login)
                        .flatMap(data -> getServerResponse(HttpStatus.OK, data));
  }

  public Mono<ServerResponse> refreshToken(ServerRequest serverRequest){
    return getRefreshTokenRequestAndValidate(serverRequest)
                        .flatMap(iAuthService::refreshToken)
                        .flatMap(data -> getServerResponse(HttpStatus.OK, data));
  }

  public Mono<ServerResponse> logout(ServerRequest serverRequest){
    return getRefreshTokenRequestAndValidate(serverRequest)
                        .map(RefreshTokenRequest::getToken)
                        .flatMap(token -> ServerResponse.noContent().build(iRefreshTokenService.deleteRefreshToken(token)));
  }


  private <T> Mono<ServerResponse> getServerResponse(HttpStatus status, T data){
    return ServerResponse.status(status).contentType(JSON).bodyValue(data);
  }

  private Mono<RefreshTokenRequest> getRefreshTokenRequestAndValidate(ServerRequest serverRequest){
    return serverRequest.bodyToMono(RefreshTokenRequest.class).doOnNext(constraintValidator::validateRequest);
  }
}
