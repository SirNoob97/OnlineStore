package com.sirnoob.productservice.service;

import com.sirnoob.productservice.dto.template.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.template.ProductRequest;
import com.sirnoob.productservice.dto.template.ProductResponse;
import com.sirnoob.productservice.entity.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService {

	public Mono<ProductResponse> createProduct(ProductRequest productRequest);

	public Mono<Product> updateProduct(Product product);
	
	public Mono<Product> updateStock(Long id, Integer quantity);

	public Mono<Void> deleteProduct(Integer productNumber);

	public Mono<ProductInvoiceResponse> getProductInvoiceResponseById(Long productId);
	
	public Flux<ProductResponse> listAll();
	
	public Flux<ProductResponse> getProductByName(String productName);

	public Flux<ProductResponse> getProductByMainCategory(Long categoryId);

	public Mono<ProductResponse> getProductByProductNumber(Integer productNumber);

}
