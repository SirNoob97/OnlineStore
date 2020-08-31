package com.sirnoob.productservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.sirnoob.productservice.entity.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface IProductRespository extends ReactiveCrudRepository<Product, Long> {

	Mono<Product> findByProductNumber(Integer productNumber);

	
	Flux<Product> findByMainCategoryCategoryId(Long categoryId);

}
