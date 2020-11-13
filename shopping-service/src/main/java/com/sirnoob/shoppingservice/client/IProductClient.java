package com.sirnoob.shoppingservice.client;

import com.sirnoob.shoppingservice.model.Product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", fallback = ProductHystrixFallback.class)
public interface IProductClient{

  @GetMapping("/products/invoices")
  public ResponseEntity<Product> getProductForInvoice(@RequestParam(required = true) Long productBarCode, @RequestParam(required = true) String productName);

  @PutMapping("/products/{productBarCode}/stock")
  public ResponseEntity<Void> updateProductStock(@PathVariable Long productBarCode, @RequestParam(required = true) Integer quantity);
}
