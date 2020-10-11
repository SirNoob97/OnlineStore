package com.sirnoob.authservice.configuration;

import com.sirnoob.authservice.handler.AuthHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class Router {
  
  private static final MediaType JSON = MediaType.APPLICATION_JSON;

  @Bean
  public RouterFunction<ServerResponse> authEndPoints (AuthHandler authHandler){
    return RouterFunctions.route(RequestPredicates.POST("/auth/signup").and(RequestPredicates.accept(JSON)), authHandler::signup)
                          .andRoute(RequestPredicates.POST("/auth/login").and(RequestPredicates.accept(JSON)), authHandler::login);
  }
}
