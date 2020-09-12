package com.sirnoob.productservice.controller;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.dto.ProductView;
import com.sirnoob.productservice.service.IProductService;

import org.springframework.http.ResponseEntity;
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

  @PostMapping("/createproduct")
  public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest){
    return ResponseEntity.ok().body(iProductService.createProduct(productRequest));
  }

  @PutMapping("/updateproductstock/{productBarCode}/")
  public ResponseEntity<Void> updateProductStock(@PathVariable Long productBarCode, @RequestParam(required = true) Integer quantity){
    iProductService.updateProductStock(productBarCode, quantity);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/getproductforinvoice")
  public ResponseEntity<ProductInvoiceResponse> getProductForInvoice(@RequestParam(required = false, defaultValue = "0") Long productBarCode, @RequestParam(required = false, defaultValue = " ") String productName){
    return ResponseEntity.ok().body(iProductService.getProductForInvoiceResponse(productBarCode, productName));
  }



  @GetMapping("/getallproducts/{page}")
  public ResponseEntity<List<ProductListView>> getAllProducts(@PathVariable int page){
    return ResponseEntity.ok().body(iProductService.getPageOfProductListView(page));
  }

  @GetMapping("/findbymaincategory/{page}")
  public ResponseEntity<List<ProductListView>> findProductsByMainCategoryId(@RequestParam(required = true) String mainCategoryName, @PathVariable int page){
    return ResponseEntity.ok().body(iProductService.getProductListViewByMainCategory(mainCategoryName, page));
  }

  @GetMapping("/findbysubcategories")
  public ResponseEntity<Set<ProductListView>> findBySubCategories(@RequestBody String[] subCategories){
    return ResponseEntity.ok().body(iProductService.getProductListViewBySubCategory(subCategories));
  }

  @GetMapping("/findbyproductname/{productName}")
  public ResponseEntity<ProductView> findProductByProductName(@PathVariable(required = true) String productName){
    return ResponseEntity.ok().body(iProductService.findProductViewByName(productName));
  }

  @GetMapping("/listbyproductname/{page}")
  public ResponseEntity<List<ProductListView>> listByProductName(@RequestParam(required = true) String productName, @PathVariable int page){
    return ResponseEntity.ok().body(iProductService.getProductListViewByName(productName, page));
  }
}
