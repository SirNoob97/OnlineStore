package com.sirnoob.shoppingservice.client;

import com.sirnoob.shoppingservice.config.FeignConfig;
import com.sirnoob.shoppingservice.model.Product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;



@FeignClient(name = "product-service", configuration = FeignConfig.class, fallback = ProductFallback.class)
public interface IProductClient{
  public final String INVOICES = "invoices";
  public final String STOCK = "stock";


  @CircuitBreaker(name = INVOICES)
  @GetMapping("/products/invoices")
  public ResponseEntity<Product> getProductForInvoice(@RequestParam(required = true) Long productBarCode,
                                                       @RequestParam(required = true) String productName);

  @CircuitBreaker(name = STOCK)
  @PutMapping("/products/{productBarCode}/stock")
  public ResponseEntity<Void> updateProductStock(@PathVariable Long productBarCode, @RequestParam(required = true) Integer quantity);
}
