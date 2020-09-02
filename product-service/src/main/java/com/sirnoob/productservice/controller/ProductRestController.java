package com.sirnoob.productservice.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
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

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.service.IProductService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/products")
public class ProductRestController {

	private final IProductService iProductService;
	
	@PostMapping("/createproduct")
	public ResponseEntity<Mono<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest productRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(iProductService.createProduct(productRequest));
	}
	
	@DeleteMapping("/deleteproduct/{productNumber}")
	public ResponseEntity<Mono<ProductResponse>> deleteByProductNumber(@PathVariable Integer productNumber) {
		return ResponseEntity.ok().body(iProductService.deleteProduct(productNumber));
	}
	
	@PutMapping("/updateproduct")
	public ResponseEntity<Mono<ProductResponse>> updateProduct(@Valid @RequestBody ProductRequest productRequest){
		return ResponseEntity.ok().body(iProductService.updateProduct(productRequest));
	}
	
	@PutMapping("/updateproductstock/{productNumber}")
	public ResponseEntity<Mono<ProductResponse>> updateProductStock(@PathVariable Integer productNumber, @RequestParam(name = "quantity", required = true) Integer quantity) {
		return ResponseEntity.ok().body(iProductService.updateStock(productNumber, quantity));
	}
	
	@GetMapping
	public ResponseEntity<Flux<ProductResponse>> listAll(){
		return ResponseEntity.ok().body(iProductService.listAll());
	}
	@GetMapping("/findbyid/{id}")
	public ResponseEntity<Mono<ProductInvoiceResponse>> getProduct(@PathVariable Long id) {
		return ResponseEntity.ok().body(iProductService.getProductInvoiceResponseById(id));
	}

	@GetMapping("/findbyname/")
	public ResponseEntity<Flux<ProductResponse>> findByName(@RequestParam(name = "product_name", required = true) String productName) {
		return ResponseEntity.ok().body(iProductService.getProductByName(productName));
	}
	
	@GetMapping("/findbynumber/{productNumber}")
	public ResponseEntity<Mono<ProductResponse>> findByProductNumber(@PathVariable Integer productNumber){
		return ResponseEntity.ok().body(iProductService.getProductByProductNumber(productNumber));
	}
	
	@GetMapping("/findbycategory/{id}")
	public ResponseEntity<Flux<ProductResponse>> findByCategory(@PathVariable Long id){
		return ResponseEntity.ok().body(iProductService.getProductByMainCategory(id));
	}
}
