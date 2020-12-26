package com.sirnoob.authservice.handler;

import com.sirnoob.authservice.dto.AccountView;
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
  private static final String CLEAR_SITE_DATA = "Clear-Site-Data";
  private static final String CLEAR_SITE_DATA_VALUES = "\"cache\", \"cookies\", \"storage\", \"executionContexts\"";

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

  public Mono<ServerResponse> getCurrentUser(ServerRequest serverRequest){
    return  ServerResponse.ok().contentType(JSON).body(iAuthService.getCurrentUser(), AccountView.class);
  }

  public Mono<ServerResponse> refreshToken(ServerRequest serverRequest){
    return getRefreshTokenRequestAndValidate(serverRequest)
                        .flatMap(iAuthService::refreshToken)
                        .flatMap(data -> getServerResponse(HttpStatus.OK, data));
  }

  public Mono<ServerResponse> logout(ServerRequest serverRequest){
    return getRefreshTokenRequestAndValidate(serverRequest)
                        .map(RefreshTokenRequest::getToken)
                        .flatMap(token -> iRefreshTokenService.deleteRefreshToken(token))
                        .then(ServerResponse.noContent().header(CLEAR_SITE_DATA, CLEAR_SITE_DATA_VALUES).build());
  }


  private <T> Mono<ServerResponse> getServerResponse(HttpStatus status, T data){
    return ServerResponse.status(status).contentType(JSON).bodyValue(data);
  }

  private Mono<RefreshTokenRequest> getRefreshTokenRequestAndValidate(ServerRequest serverRequest){
    return serverRequest.bodyToMono(RefreshTokenRequest.class).doOnNext(constraintValidator::validateRequest);
  }
}
