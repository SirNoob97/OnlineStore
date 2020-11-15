package com.sirnoob.authservice.configuration;

import com.sirnoob.authservice.handler.AccountHandler;
import com.sirnoob.authservice.handler.AuthHandler;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

@Configuration
public class Router {

  private static final MediaType JSON = MediaType.APPLICATION_JSON;

  @Bean
  public ResponseStatusExceptionHandler handler (){
    return new ResponseStatusExceptionHandler();
  }

  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
    return routeLocatorBuilder.routes()
                              .route(route -> route.path("/products/**", "/main-categories/**", "/sub-categories/**")
                                                    .uri("lb://product-service")
                                                    .id("product-service"))
                              .route(route -> route.path("/invoices/**")
                                                    .uri("lb://shopping-service")
                                                    .id("shopping-service"))
                              .build();
  }

  @Bean
  public RouterFunction<ServerResponse> authEndPoints (AuthHandler authHandler){
    return RouterFunctions.route(POST("/auth/signup").and(accept(JSON)).and(contentType(JSON)), authHandler::signup)
                          .andRoute(POST("/auth/login").and(accept(JSON)).and(contentType(JSON)), authHandler::login)
                          .andRoute(GET("/auth/users").and(accept(JSON)), authHandler::getCurrentUser)
                          .andRoute(POST("/auth/logout").and(accept(JSON)), authHandler::logout)
                          .andRoute(POST("/auth/refresh-token").and(accept(JSON)).and(contentType(JSON)), authHandler::refreshToken);
  }

  @Bean
  public RouterFunction<ServerResponse> accountEndPoints (AccountHandler accountHandler){
    return RouterFunctions.route(POST("/accounts").and(accept(JSON)).and(contentType(JSON)), accountHandler::createAccount)
                          .andRoute(PUT("/accounts").and(accept(JSON)).and(contentType(JSON)), accountHandler::updateAccount)
                          .andRoute(PUT("/accounts/passwords").and(contentType(JSON)), accountHandler::updatePassword)
                          .andRoute(DELETE("/accounts/{userId}"), accountHandler::deleteAccountById)
                          .andRoute(GET("/accounts").and(accept(JSON)), accountHandler::getAllAccounts);
  }

}
