package com.sirnoob.authservice.handler;

import java.util.Iterator;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.sirnoob.authservice.dto.AuthResponse;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.RefreshTokenRequest;
import com.sirnoob.authservice.dto.SignUpRequest;
import com.sirnoob.authservice.exception.WrongPasswordException;
import com.sirnoob.authservice.service.IAuthService;
import com.sirnoob.authservice.service.IRefreshTokenService;

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

  private final IRefreshTokenService iRefreshTokenService;
  private final IAuthService iAuthService;
  private final Validator validator;

  private static final MediaType JSON = MediaType.APPLICATION_JSON;

  public Mono<ServerResponse> signup(ServerRequest serverRequest){
    Mono<SignUpRequest> body = serverRequest.bodyToMono(SignUpRequest.class);
    Mono<AuthResponse> authResponse = body.flatMap(iAuthService::signup);

    return authResponse.flatMap(data -> ServerResponse.status(HttpStatus.CREATED).contentType(JSON).bodyValue(data));
  }

  public Mono<ServerResponse> login(ServerRequest serverRequest){
    Mono<LoginRequest> body = serverRequest.bodyToMono(LoginRequest.class);

    String message = validation(body).block();

    if (message == null || message.isEmpty()) {
      Mono<AuthResponse> authResponse = body.flatMap(iAuthService::login);

      return authResponse.flatMap(data -> ServerResponse.ok().contentType(JSON).bodyValue(data))
          .onErrorResume(error -> ServerResponse.notFound().build());
    } else {
      return ServerResponse.badRequest().contentType(JSON).bodyValue(message);
    }
  }

  public Mono<ServerResponse> refreshToken(ServerRequest serverRequest){
    Mono<RefreshTokenRequest> body = serverRequest.bodyToMono(RefreshTokenRequest.class);
    Mono<AuthResponse> authResponse = body.flatMap(iAuthService::refreshToken);

    return authResponse.flatMap(data -> ServerResponse.ok().contentType(JSON).bodyValue(data))
                        .onErrorResume(error -> ServerResponse.notFound().build());
  }

  public Mono<ServerResponse> logout(ServerRequest serverRequest){
    Mono<RefreshTokenRequest> body = serverRequest.bodyToMono(RefreshTokenRequest.class);

    return ServerResponse.noContent().build(body.flatMap(token -> iRefreshTokenService.deleteRefreshToken(token.getToken())));
  }

  private <T> Mono<String> validation(Mono<T> t) {
    return t.flatMap(m -> {
     Iterator<ConstraintViolation<T>> it = validator.validate(m).iterator();
      String message = "";
      while (it.hasNext()) {
        ConstraintViolation<T> constraintViolation = it.next();
         message = constraintViolation.getMessage();
      }
      return Mono.just(message);
    });
  }
}
