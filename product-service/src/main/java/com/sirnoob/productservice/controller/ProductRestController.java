package com.sirnoob.productservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.service.IProductService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/products")
public class ProductRestController {

	private final IProductService iProductService;
	
	@GetMapping
	public ResponseEntity<Flux<Product>> listAll(){
		return ResponseEntity.ok().body(iProductService.listAll());
	}
}
