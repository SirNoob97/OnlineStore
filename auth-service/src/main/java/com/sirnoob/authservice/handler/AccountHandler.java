package com.sirnoob.authservice.handler;

import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AccountPayload;
import com.sirnoob.authservice.dto.PasswordUpdateDto;
import com.sirnoob.authservice.service.IAccountService;
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

  private final IAccountService iAccountService;
  private final ConstraintValidator validator;

  public Mono<ServerResponse> createAccount (ServerRequest serverRequest){
    return persistUser(serverRequest).flatMap(data -> ServerResponse.status(HttpStatus.CREATED).contentType(JSON).bodyValue(data));
  }

  public Mono<ServerResponse> updateAccount(ServerRequest serverRequest){
    return persistUser(serverRequest).flatMap(data -> ServerResponse.ok().contentType(JSON).bodyValue(data));
  }

  public Mono<ServerResponse> updatePassword(ServerRequest serverRequest){
    return serverRequest.bodyToMono(PasswordUpdateDto.class)
                        .doOnNext(validator::validateRequest)
                        .flatMap(dto -> ServerResponse.noContent().build(iAccountService.updatePassword(dto)));
  }

  public Mono<ServerResponse> deleteAccountById(ServerRequest serverRequest) {
    Long userId = Long.valueOf(serverRequest.pathVariable("userId"));

    //iAccountService.deleteAccount(userId).subscribe();

    //return ServerResponse.noContent().build();
    return ServerResponse.noContent().build(iAccountService.deleteAccount(userId));
  }

  public Mono<ServerResponse> getAllAccounts(ServerRequest serverRequest){
    return ServerResponse.ok().contentType(JSON).body(iAccountService.getAllAccounts(), User.class);
  }



  private Mono<String> persistUser(ServerRequest serverRequest){
    return serverRequest.bodyToMono(AccountPayload.class)
                        .doOnNext(validator::validateRequest)
                        .flatMap(iAccountService::persistAccount);
  }
}
