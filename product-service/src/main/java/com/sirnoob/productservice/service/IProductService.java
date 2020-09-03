package com.sirnoob.productservice.service;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService {

	public Mono<ProductResponse> createProduct(ProductRequest productRequest);

	public Mono<ProductResponse> deleteProduct(Long productBarCode);

	public Flux<ProductResponse> getProductByMainCategory(Long categoryId);
	
	public Flux<ProductResponse> getProductByName(String productName);

	public Mono<ProductResponse> getProductByproductBarCode(Long productBarCode);

	public Mono<ProductInvoiceResponse> getProductInvoiceResponseById(Long productId);
	
	public Flux<ProductResponse> getAllProducts();
	
	public Mono<ProductResponse> suspendProduct(Long productBarCode);

	public Mono<ProductResponse> updateProduct(ProductRequest productRequest);

	public Mono<ProductResponse> updateStock(Long productBarCode, Integer quantity);

}
