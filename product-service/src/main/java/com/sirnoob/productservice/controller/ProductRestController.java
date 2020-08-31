package com.sirnoob.productservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	
	@GetMapping
	public ResponseEntity<Flux<ProductResponse>> listAll(){
		return ResponseEntity.ok().body(iProductService.listAll());
	}
	
	@GetMapping("/{product_number}")
	public ResponseEntity<Mono<ProductResponse>> findByProductNumber(@PathVariable("product_number") Integer poductNumber){
		return ResponseEntity.ok().body(iProductService.getProductByProductNumber(poductNumber));
	}
	
	@GetMapping("/category/{id}")
	public ResponseEntity<Flux<ProductResponse>> findByCategory(@PathVariable("id") Long id){
		return ResponseEntity.ok().body(iProductService.findByMainCategory(id));
	}
}
