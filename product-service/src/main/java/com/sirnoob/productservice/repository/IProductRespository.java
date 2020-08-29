package com.sirnoob.productservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.sirnoob.productservice.entity.Product;

import reactor.core.publisher.Flux;

@Repository
public interface IProductRespository extends ReactiveCrudRepository<Product, Long>{

	Flux<Product> findByProductNumber(Integer productNumberId);
	
	Flux<Product> findByCategoryCategoryId(Long categoryId);
	
	Flux<Product> findBySubCategoriesSubCategoryId(Long subCategoriesId);
	
	
}
