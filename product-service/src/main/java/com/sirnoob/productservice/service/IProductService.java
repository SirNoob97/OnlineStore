package com.sirnoob.productservice.service;

import com.sirnoob.productservice.dto.template.ProductResponse;
import com.sirnoob.productservice.entity.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService {

	public Mono<Product> getProduct(Long productId);

	public Mono<Product> createProduct(Product product);

	public Mono<Product> updateProduct(Product product);

	public Mono<Product> deleteProduct(Long productId);

	public Flux<ProductResponse> listAll();

	public Flux<ProductResponse> findByMainCategory(Long categoryId);

	public Mono<ProductResponse> getProductByProductNumber(Integer productNumber);

}
