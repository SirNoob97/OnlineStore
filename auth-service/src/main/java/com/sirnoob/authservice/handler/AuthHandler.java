package com.sirnoob.authservice.handler;

import com.sirnoob.authservice.dto.AuthResponse;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.SignUpRequest;
import com.sirnoob.authservice.exception.WrongPasswordException;
import com.sirnoob.authservice.service.IAuthService;

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

  private final IAuthService iAuthService;

  private static final MediaType JSON = MediaType.APPLICATION_JSON;

  public Mono<ServerResponse> signup(ServerRequest serverRequest){
    Mono<SignUpRequest> body = serverRequest.bodyToMono(SignUpRequest.class);
    Mono<AuthResponse> authResponse = body.flatMap(iAuthService::signup);

    return authResponse.flatMap(data -> ServerResponse.status(HttpStatus.CREATED).contentType(JSON).bodyValue(data));
  }

  public Mono<ServerResponse> login(ServerRequest serverRequest){
    Mono<LoginRequest> body = serverRequest.bodyToMono(LoginRequest.class);
    Mono<AuthResponse> authResponse = body.flatMap(iAuthService::login);

    return authResponse.flatMap(data -> ServerResponse.ok().contentType(JSON).bodyValue(data))
                        .switchIfEmpty(ServerResponse.notFound().build())
                        .onErrorResume(error -> {
                            return error instanceof WrongPasswordException ? ServerResponse.badRequest().build() : ServerResponse.status(500).build();
                        });
  }

}
