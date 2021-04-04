package com.sirnoob.shoppingservice.client;

import com.sirnoob.shoppingservice.model.Product;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductFallback implements ProductClient {

  @Override
  public ResponseEntity<Product> getInfo(Long productBarCode, String productName) {
    Product product = Product.builder().productName("none").productPrice(0.0).build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(product);
  }

  @Override
  public ResponseEntity<Void> updateStock(Long productBarCode, Integer quantity) {
    return ResponseEntity.notFound().build();
  }
}
