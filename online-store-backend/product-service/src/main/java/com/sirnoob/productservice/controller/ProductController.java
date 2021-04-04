package com.sirnoob.productservice.controller;

import java.util.Set;

import javax.validation.Valid;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.dto.ProductView;
import com.sirnoob.productservice.service.MainCategoryService;
import com.sirnoob.productservice.service.ProductService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

  private static final MediaType JSON = MediaType.APPLICATION_JSON;

  private final MainCategoryService mainCategoryService;
  private final ProductService productService;

  @PostMapping
  public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest productRequest){
    return ResponseEntity.status(HttpStatus.CREATED).contentType(JSON).body(productService.create(productRequest,
        mainCategoryService.getByName(productRequest.getMainCategoryName())));
  }

  @PutMapping("/{productId}")
  public ResponseEntity<ProductResponse> update(@PathVariable Long productId, @Valid @RequestBody ProductRequest productRequest){
    return ResponseEntity.ok().contentType(JSON).body(productService.update(productId, productRequest,
          mainCategoryService.getByName(productRequest.getMainCategoryName())));
  }

  @PutMapping("/{productBarCode}/stock")
  public ResponseEntity<Void> updateStock(@PathVariable Long productBarCode, @RequestParam(required = true) Integer quantity){
    productService.updateStock(productBarCode, quantity);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/responses")
  public ResponseEntity<ProductResponse> getProductResponse(@RequestParam(defaultValue = "0", required = false) Long productBarCode,
                                                            @RequestParam(defaultValue = " ", required = false) String productName){
    return ResponseEntity.ok().contentType(JSON).body(productService.getProductResponseByBarCodeOrProductName(productBarCode, productName));
  }

  @GetMapping("/invoices")
  public ResponseEntity<ProductInvoiceResponse> forInvoice(@RequestParam(required = true) Long productBarCode, @RequestParam(required = true) String productName){
    return ResponseEntity.ok().contentType(JSON).body(productService.getForInvoiceResponse(productBarCode, productName));
  }

  @DeleteMapping("/{productId}")
  public ResponseEntity<Void> deleteById(@PathVariable Long productId){
    productService.deleteById(productId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<Page<ProductListView>> getAll(Pageable pageable){
    return ResponseEntity.ok().contentType(JSON).body(productService.getListView(pageable));
  }

  @GetMapping("/main-categories/{mainCategoryId}")
  public ResponseEntity<Page<ProductListView>> getByMainCategoryId(@PathVariable Long mainCategoryId, Pageable pageable){
    return ResponseEntity.ok().contentType(JSON).body(productService.getListViewByMainCategory(mainCategoryId, pageable));
  }

  @GetMapping("/sub-categories")
  public ResponseEntity<Set<ProductListView>> getBySubCategories(@RequestBody String[] subCategories){
    return ResponseEntity.ok().contentType(JSON).body(productService.getListViewBySubCategory(subCategories));
  }

  @GetMapping("/names")
  public ResponseEntity<ProductView> getByName(@RequestParam(required = true) String productName){
    return ResponseEntity.ok().contentType(JSON).body(productService.getProductViewByName(productName));
  }

  @GetMapping("/names/search")
  public ResponseEntity<Page<ProductListView>> getByNameCoincidences(@RequestParam(required = true) String productName, Pageable pageable){
    return ResponseEntity.ok().contentType(JSON).body(productService.getListViewByName(productName, pageable));
  }
}
