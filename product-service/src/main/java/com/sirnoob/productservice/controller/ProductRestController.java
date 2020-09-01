package com.sirnoob.productservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sirnoob.productservice.dto.template.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.template.ProductRequest;
import com.sirnoob.productservice.dto.template.ProductResponse;
import com.sirnoob.productservice.service.IProductService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/products")
public class ProductRestController {

	private final IProductService iProductService;
	
	@PostMapping
	public ResponseEntity<Mono<ProductResponse>> createProduct(@RequestBody ProductRequest productRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(iProductService.createProduct(productRequest));
	}
	
	@GetMapping
	public ResponseEntity<Flux<ProductResponse>> listAll(){
		return ResponseEntity.ok().body(iProductService.listAll());
	}
	@GetMapping("/findbyid/{id}")
	public ResponseEntity<Mono<ProductInvoiceResponse>> getProduct(@PathVariable("id") Long id) {
		return ResponseEntity.ok().body(iProductService.getProductInvoiceResponseById(id));
	}

	@GetMapping("/findbyname/")
	public ResponseEntity<Flux<ProductResponse>> findByName(@RequestParam(name = "product_name", required = true) String productName) {
		return ResponseEntity.ok().body(iProductService.getProductByName(productName));
	}
	
	@GetMapping("/findbynumber/{product_number}")
	public ResponseEntity<Mono<ProductResponse>> findByProductNumber(@PathVariable("product_number") Integer poductNumber){
		return ResponseEntity.ok().body(iProductService.getProductByProductNumber(poductNumber));
	}
	
	@GetMapping("/findbycategory/{id}")
	public ResponseEntity<Flux<ProductResponse>> findByCategory(@PathVariable("id") Long id){
		return ResponseEntity.ok().body(iProductService.getProductByMainCategory(id));
	}
}
