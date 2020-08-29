package com.sirnoob.productservice.service;

import org.springframework.stereotype.Service;

import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.repository.IProductRespository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProductServicesImpl implements IProductService{

	private final IProductRespository iProductRespository;

	@Override
	public Mono<Product> getProduct(Long productId) {
		return iProductRespository.findById(productId);
	}

	@Override
	public Mono<Product> createProduct(Product product) {
		return null;
	}

	@Override
	public Mono<Product> updateProduct(Product product) {
		return null;
	}

	@Override
	public Mono<Product> deleteProduct(Long productId) {
		return null;
	}

	@Override
	public Flux<Product> listAll() {
		return iProductRespository.findAll();
	}

	@Override
	public Flux<Product> findByCategoryId(Long categoryId) {
		return iProductRespository.findByCategoryCategoryId(categoryId);
	}

	@Override
	public Flux<Product> findBySubCategoryId(Long categoryId) {
		return iProductRespository.findBySubCategoriesSubCategoryId(categoryId);
	}

	@Override
	public Flux<Product> findByProductNumber(Integer productNumber) {
		return iProductRespository.findByProductNumber(productNumber);
	}

}
