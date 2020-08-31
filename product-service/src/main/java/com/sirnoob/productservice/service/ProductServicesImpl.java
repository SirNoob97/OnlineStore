package com.sirnoob.productservice.service;

import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;

import com.sirnoob.productservice.dto.mapper.IProductMapper;
import com.sirnoob.productservice.dto.template.ProductResponse;
import com.sirnoob.productservice.entity.Product;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProductServicesImpl implements IProductService {

	private final IProductMapper iProductMapper;
	private final DatabaseClient databaseClient;

	@Override
	public Mono<Product> getProduct(Long productId) {
		return null;
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
	public Flux<ProductResponse> listAll() {
		final String query = "SELECT * FROM products INNER JOIN main_categories ON products.category_id = main_categories.category_id";
		return databaseClient.execute(query).map(iProductMapper::mapToProductResponse).all();
	}

	@Override
	public Flux<ProductResponse> findByMainCategory(Long categoryId) {
		final String query = "SELECT * FROM products INNER JOIN main_categories ON main_categories.category_id = products.category_id WHERE products.category_id = :categoryId";
		return databaseClient.execute(query).bind("categoryId", categoryId).map(iProductMapper::mapToProductResponse).all();
	}

	@Override
	public Mono<ProductResponse> getProductByProductNumber(Integer productNumber) {
		final String query = "SELECT * FROM products JOIN main_categories ON products.category_id = main_categories.category_id WHERE products.product_number = :productNumber";
		return databaseClient.execute(query).bind("productNumber", productNumber).map(iProductMapper::mapToProductResponse).one();
	}

}
