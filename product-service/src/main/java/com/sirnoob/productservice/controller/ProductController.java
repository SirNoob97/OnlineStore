package com.sirnoob.productservice.controller;

import com.sirnoob.productservice.service.IProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

  private final IProductService iProductService;


  @PutMapping("/updateproductstock/{productBarCode}/")
  public ResponseEntity<Void> updateProductStock(@PathVariable Long productBarCode, @RequestParam(required = true) Integer quantity){
    iProductService.updateProductStock(productBarCode, quantity);
    return ResponseEntity.noContent().build();
  }
}
