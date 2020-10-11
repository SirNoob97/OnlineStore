package com.sirnoob.authservice.handler;

import com.sirnoob.authservice.dto.AuthResponse;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.SignUpRequest;
import com.sirnoob.authservice.exception.CustomException;
import com.sirnoob.authservice.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthHandler {

  private final AuthService authService;

  private static final MediaType JSON = MediaType.APPLICATION_JSON;

  public Mono<ServerResponse> signup(ServerRequest serverRequest){    
    Mono<SignUpRequest> body = serverRequest.bodyToMono(SignUpRequest.class);
    Mono<AuthResponse> authResponse = body.flatMap(authService::signup);

    return authResponse.flatMap(data -> ServerResponse.status(HttpStatus.CREATED).contentType(JSON).bodyValue(data));
                        
//                        .onErrorResume(error -> ServerResponse.badRequest().build());
  }

  public Mono<ServerResponse> login(ServerRequest serverRequest){
    Mono<LoginRequest> body = serverRequest.bodyToMono(LoginRequest.class);
    Mono<AuthResponse> authResponse = body.flatMap(authService::login);

    return authResponse.flatMap(data -> ServerResponse.ok().contentType(JSON).bodyValue(data))
                        .switchIfEmpty(ServerResponse.notFound().build())
                        .onErrorResume(error -> {
                            return error instanceof CustomException ? ServerResponse.badRequest().build() : ServerResponse.status(500).build();
                        });
  }

}
