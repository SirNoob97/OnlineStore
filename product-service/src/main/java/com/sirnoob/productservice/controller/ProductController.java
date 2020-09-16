package com.sirnoob.productservice.controller;

import java.util.Set;

import javax.validation.Valid;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.dto.ProductView;
import com.sirnoob.productservice.service.IProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

  private final IProductService iProductService;

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest){
    return ResponseEntity.ok().body(iProductService.createProduct(productRequest));
  }

  @PutMapping("/{productId}")
  public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductRequest productRequest){
    return ResponseEntity.ok().body(iProductService.updateProduct(productId, productRequest));
  }

  @PutMapping("/{productBarCode}/stock")
  public ResponseEntity<Void> updateProductStock(@PathVariable Long productBarCode, @RequestParam(required = true) Integer quantity){
    iProductService.updateProductStock(productBarCode, quantity);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/responses")
  public ResponseEntity<ProductResponse> getProductResponse(@RequestParam(defaultValue = "0", required = false) Long productBarCode,
                                                            @RequestParam(defaultValue = " ", required = false) String productName){
    return ResponseEntity.ok().body(iProductService.getProductResponseByBarCodeOrProductName(productBarCode, productName));
  }

  @GetMapping("/invoices")
  public ResponseEntity<ProductInvoiceResponse> getProductForInvoice(@RequestParam(required = true) Long productBarCode, @RequestParam(required = true) String productName){
    return ResponseEntity.ok().body(iProductService.getProductForInvoiceResponse(productBarCode, productName));
  }

  @DeleteMapping("/{productId}")
  public ResponseEntity<Void> deleteProductById(@PathVariable Long productId){
    iProductService.deleteProductById(productId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<Set<ProductListView>> listAllProducts(@RequestParam(defaultValue = "0") int page){
    return ResponseEntity.ok().body(iProductService.getPageOfProductListView(page));
  }

  @GetMapping("/main-categories/{page}")
  public ResponseEntity<Set<ProductListView>> listProductsByMainCategoryId(@RequestParam(required = true) String mainCategoryName, @PathVariable int page){
    return ResponseEntity.ok().body(iProductService.getProductListViewByMainCategory(mainCategoryName, page));
  }

  @GetMapping("/sub-categories")
  public ResponseEntity<Set<ProductListView>> listProductsBySubCategories(@RequestBody String[] subCategories){
    return ResponseEntity.ok().body(iProductService.getProductListViewBySubCategory(subCategories));
  }

  @GetMapping("/names")
  public ResponseEntity<ProductView> getProductByProductName(@RequestParam(required = true) String productName){
    return ResponseEntity.ok().body(iProductService.findProductViewByName(productName));
  }

  @GetMapping("/names/{page}")
  public ResponseEntity<Set<ProductListView>> listByProductNameCoincidences(@RequestParam(required = true) String productName, @PathVariable int page){
    return ResponseEntity.ok().body(iProductService.getProductListViewByName(productName, page));
  }
}
