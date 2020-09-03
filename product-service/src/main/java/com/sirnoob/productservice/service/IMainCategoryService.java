package com.sirnoob.productservice.service;

import com.sirnoob.productservice.dto.MainCategory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IMainCategoryService {
	
	Flux<MainCategory> findAllMainCategories();
	
	Mono<MainCategory> findMainCategoryByName(String mainCategoryName);
	
	Mono<MainCategory> createMainCategory(String mainCategoryName);
	
	Mono<MainCategory> updateMainCategory(MainCategory mainCategory);
	
	Mono<MainCategory> deleteMainCategory(Long mainCategoryid);
}
