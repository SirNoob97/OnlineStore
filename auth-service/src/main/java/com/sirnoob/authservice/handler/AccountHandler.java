package com.sirnoob.authservice.handler;

import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.PasswordUpdateDto;
import com.sirnoob.authservice.service.IAccountService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class AccountHandler {

  private final IAccountService iAccountService;

  private static final MediaType JSON = MediaType.APPLICATION_JSON;

  public Mono<ServerResponse> createAccount (ServerRequest serverRequest){
    Mono<User> body = serverRequest.bodyToMono(User.class);
    Mono<String> message = body.flatMap(iAccountService::persistAccount);

    return message.flatMap(data -> ServerResponse.status(HttpStatus.CREATED).contentType(JSON).bodyValue(data));
  }

  public Mono<ServerResponse> updateAccount(ServerRequest serverRequest){
    Mono<User> body = serverRequest.bodyToMono(User.class);
    Mono<String> message = body.flatMap(iAccountService::persistAccount);

    return message.flatMap(data -> ServerResponse.ok().contentType(JSON).bodyValue(data));
  }

  public Mono<ServerResponse> updatePassword(ServerRequest serverRequest){
    Mono<PasswordUpdateDto> body = serverRequest.bodyToMono(PasswordUpdateDto.class);

    return ServerResponse.noContent().build(body.flatMap(iAccountService::updatePassword));
  }

  public Mono<ServerResponse> deleteAccountById(ServerRequest serverRequest) {
    Long userId = Long.valueOf(serverRequest.pathVariable("userId"));

    return ServerResponse.noContent().build(iAccountService.deleteAccount(userId));
  }

  public Mono<ServerResponse> getAllAccounts(ServerRequest serverRequest){
    return ServerResponse.ok().contentType(JSON).body(iAccountService.getAllAccounts(), User.class);
  }
}
