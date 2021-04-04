package com.sirnoob.authservice.handler;

import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AccountPayload;
import com.sirnoob.authservice.dto.PasswordUpdateDto;
import com.sirnoob.authservice.service.AccountService;
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
public class AccountHandler {

  private static final MediaType JSON = MediaType.APPLICATION_JSON;

  private final AccountService accountService;
  private final ConstraintValidator validator;

  public Mono<ServerResponse> create (ServerRequest serverRequest){
    return persistUser(serverRequest).flatMap(data -> ServerResponse.status(HttpStatus.CREATED).contentType(JSON).bodyValue(data));
  }

  public Mono<ServerResponse> update(ServerRequest serverRequest){
    return persistUser(serverRequest).flatMap(data -> ServerResponse.ok().contentType(JSON).bodyValue(data));
  }

  public Mono<ServerResponse> updatePassword(ServerRequest serverRequest){
    return serverRequest.bodyToMono(PasswordUpdateDto.class)
           .doOnNext(validator::validate)
           .flatMap(dto -> accountService.updatePassword(dto))
           .then(ServerResponse.noContent().build());
  }

  public Mono<ServerResponse> deleteById(ServerRequest serverRequest) {
    Long userId = Long.valueOf(serverRequest.pathVariable("userId"));

    return accountService.delete(userId).then(ServerResponse.noContent().build());
  }

  public Mono<ServerResponse> getAll(ServerRequest serverRequest){
    return ServerResponse.ok().contentType(JSON).body(accountService.getAll(), User.class);
  }



  private Mono<String> persistUser(ServerRequest serverRequest){
    return serverRequest.bodyToMono(AccountPayload.class)
           .doOnNext(validator::validate)
           .flatMap(accountService::persist);
  }
}
