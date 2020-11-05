package com.sirnoob.gatewayservice;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Configuration;

//@Configuration
public class Router {

  public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
    return routeLocatorBuilder.routes()
                                .route(route -> route.path("/auth/**", "/accounts/**")
                                                      .uri("lb://auth-service")
                                                      .id("auth-service"))
                                .route(route -> route.path("/products/**", "/main-categories/**", "/sub-categories/**")
                                                      .uri("lb://product-service")
                                                      .id("product-service"))
                                .build();
  }
}
