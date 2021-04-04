package com.sirnoob.shoppingservice.config;

import com.sirnoob.shoppingservice.client.ProductClient;
import com.sirnoob.shoppingservice.client.ProductFallback;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import feign.Feign;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FeignConfig {
  private final CircuitBreakerRegistry circuitBreakerRegistry;
  private final ProductFallback productFallback;

  @Bean
  @Scope("prototype")
  public Feign.Builder feignBuilder() {
    var invoicesCircuitBreaker = circuitBreakerRegistry.circuitBreaker(ProductClient.INVOICES);
    var stockCircuitBreaker = circuitBreakerRegistry.circuitBreaker(ProductClient.STOCK);    
    var decorator = FeignDecorators.builder()
                                    .withCircuitBreaker(invoicesCircuitBreaker)
                                    .withCircuitBreaker(stockCircuitBreaker)
                                    .withFallback(productFallback)
                                    .build();
    return Resilience4jFeign.builder(decorator);
  }
}
