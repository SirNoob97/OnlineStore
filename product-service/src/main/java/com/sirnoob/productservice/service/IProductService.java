package com.sirnoob.productservice.service;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService {

	public Mono<ProductResponse> createProduct(ProductRequest productRequest);

	public Mono<ProductResponse> updateProduct(ProductRequest productRequest);
	
	public Mono<ProductResponse> updateStock(Integer productNumber, Integer quantity);

	public Mono<ProductResponse> deleteProduct(Integer productNumber);

	public Mono<ProductInvoiceResponse> getProductInvoiceResponseById(Long productId);
	
	public Flux<ProductResponse> listAll();
	
	public Flux<ProductResponse> getProductByName(String productName);

	public Flux<ProductResponse> getProductByMainCategory(Long categoryId);

	public Mono<ProductResponse> getProductByProductNumber(Integer productNumber);

}
