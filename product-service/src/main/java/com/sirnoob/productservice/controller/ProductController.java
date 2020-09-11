package com.sirnoob.productservice.controller;

import java.util.List;

import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.service.IProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

  @GetMapping("/getallproducts/{page}")
  public ResponseEntity<List<ProductListView>> getAllProducts(@PathVariable int page){
    return ResponseEntity.ok().body(iProductService.getAllProducts(page));
  }

  @GetMapping("/findbymaincategory/{page}")
  public ResponseEntity<List<ProductListView>> findProductsByMainCategoryId(@RequestParam(required = true) String mainCategoryName, @PathVariable int page){
    return ResponseEntity.ok().body(iProductService.getProductsByMainCategory(mainCategoryName, page));
  }

  @GetMapping("/findbyproductname/{productName}")
  public ResponseEntity<ProductListView> findProductByProductName(@PathVariable(required = true) String productName){
    return ResponseEntity.ok().body(iProductService.getProductByName(productName));
  }

  @GetMapping("/listbyproductname/{page}")
  public ResponseEntity<List<ProductListView>> listByProductName(@RequestParam(required = true) String productName, @PathVariable int page){
    return ResponseEntity.ok().body(iProductService.listByName(productName, page));
  }
}
