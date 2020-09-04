package com.sirnoob.productservice.service;

import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;

import com.sirnoob.productservice.dto.MainCategory;
import com.sirnoob.productservice.exception.ICustomException;
import com.sirnoob.productservice.mapper.IMainCategoryMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class MainCategoryServiceImpl implements IMainCategoryService {

	private final IMainCategoryMapper iMainCategoryMapper;
	private final DatabaseClient databaseClient;
	private final ICustomException iCustomException;

	@Override
	public Flux<MainCategory> findAllMainCategories() {
		final String query = "SELECT * FROM main_categories";
		return databaseClient.execute(query).map(iMainCategoryMapper::mapToMainCategory).all()
				.switchIfEmpty(iCustomException.fluxElementsCustomNotFoundException("Empty Record."));
	}

	@Override
	public Mono<MainCategory> findMainCategoryByName(String mainCategoryName) {
		final String query = "SELECT * FROM main_categories WHERE main_categories.category_name = :mainCategoryName";
		return databaseClient.execute(query).bind("mainCategoryName", mainCategoryName)
				.map(iMainCategoryMapper::mapToMainCategory).first()
				.switchIfEmpty(iCustomException.monoElementCustomNotFoundException("Category Not Found."));
	}

	@Override
	public Mono<MainCategory> createMainCategory(String mainCategoryName) {
		final String query = "INSERT INTO main_categories (category_name) VALUES (:mainCategoryName) RETURNING *";
		return databaseClient.execute(query).bind("mainCategoryName", mainCategoryName)
				.map(iMainCategoryMapper::mapToMainCategory).first()
				.switchIfEmpty(iCustomException.monoElementCustomNotFoundException("Error Creating Category."));
	}

	@Override
	public Mono<MainCategory> updateMainCategory(MainCategory mainCategory) {
		final String query = "UPDATE main_categories SET category_name = :mainCategoryName WHERE main_categories.category_id = :mainCategoryId RETURNING *";
		return databaseClient.execute(query).bind("mainCategoryId", mainCategory.getCategoryId())
				.bind("mainCategoryName", mainCategory.getCategoryName()).map(iMainCategoryMapper::mapToMainCategory)
				.first().switchIfEmpty(iCustomException.monoElementCustomNotFoundException("Category Update Error."));
	}

	@Override
	public Mono<MainCategory> deleteMainCategory(Long mainCategoryId) {
		final String query = "DELETE FROM main_categories WHERE main_categories.category_id = :mainCategoryId RETURNING *";
		return databaseClient.execute(query).bind("mainCategoryId", mainCategoryId)
				.map(iMainCategoryMapper::mapToMainCategory).first()
				.switchIfEmpty(iCustomException.monoElementCustomNotFoundException("Category Delete Error."));
	}

}
