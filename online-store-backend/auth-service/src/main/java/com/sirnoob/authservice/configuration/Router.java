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

import static org.springframework.cloud.gateway.support.RouteMetadataUtils.CONNECT_TIMEOUT_ATTR;
import static org.springframework.cloud.gateway.support.RouteMetadataUtils.RESPONSE_TIMEOUT_ATTR;

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
  public ResponseStatusExceptionHandler handler() {
    return new ResponseStatusExceptionHandler();
  }

  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
    return routeLocatorBuilder.routes()
           .route("products", route -> route.host("product-service")
                                .and()
                                .path("/products/**", "/main-categories/**", "/sub-categories/**", "/product-service/**")
                                .customize(c -> c.metadata(CONNECT_TIMEOUT_ATTR, 1000).metadata(RESPONSE_TIMEOUT_ATTR, 1000))
                                .uri("lb://product-service")
               )
           .route("invoices", route -> route.host("shopping-service")
                                .and()
                                .path("/invoices/**", "/shopping-service/**")
                                .customize(c -> c.metadata(CONNECT_TIMEOUT_ATTR, 10000).metadata(RESPONSE_TIMEOUT_ATTR, 50000))
                                .uri("lb://shopping-service"))
           .build();
  }

  @Bean
  public RouterFunction<ServerResponse> authEndPoints(AuthHandler authHandler) {
    return RouterFunctions.route(POST("/auth/signup").and(contentType(JSON)), authHandler::signup)
        .andRoute(POST("/auth/login").and(contentType(JSON)), authHandler::login)
        .andRoute(GET("/auth/users").and(accept(JSON)), authHandler::getCurrentUser)
        .andRoute(POST("/auth/logout"), authHandler::logout)
        .andRoute(POST("/auth/refresh-token"), authHandler::refreshToken);
  }

  @Bean
  public RouterFunction<ServerResponse> accountEndPoints(AccountHandler accountHandler) {
    return RouterFunctions
        .route(POST("/accounts").and(accept(JSON)).and(contentType(JSON)), accountHandler::createAccount)
        .andRoute(PUT("/accounts").and(accept(JSON)).and(contentType(JSON)), accountHandler::updateAccount)
        .andRoute(PUT("/accounts/passwords").and(contentType(JSON)), accountHandler::updatePassword)
        .andRoute(DELETE("/accounts/{userId}"), accountHandler::deleteAccountById)
        .andRoute(GET("/accounts").and(accept(JSON)), accountHandler::getAllAccounts);
  }

}
