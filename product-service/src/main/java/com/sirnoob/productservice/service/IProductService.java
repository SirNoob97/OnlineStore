package com.sirnoob.productservice.service;

import com.sirnoob.productservice.entity.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService {

	public Mono<Product> getProduct(Long productId);
	
	public Mono<Product> createProduct(Product product);
	
	public Mono<Product> updateProduct(Product product);
	
	public Mono<Product> deleteProduct(Long productId);
	
	public Flux<Product> listAll();

	public Flux<Product> findByCategoryId(Long categoryId);
	
	public Flux<Product> findBySubCategoryId(Long categoryId);
	
	public Flux<Product> findByProductNumber(Integer productNumber);
}
