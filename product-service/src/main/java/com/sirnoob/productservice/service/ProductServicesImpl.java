package com.sirnoob.productservice.service;

import com.sirnoob.productservice.entity.Category;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;
import com.sirnoob.productservice.repository.IProductRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductServicesImpl implements IProductService {

	private final IProductRespository iProductRespository;
	private final DatabaseClient databaseClient;

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
		final String query = "SELECT * FROM products INNER JOIN categories ON products.category_id = categories.category_id;";

		return databaseClient.execute(query).map(productDB -> {

			Long productId = productDB.get("product_id", Long.class);

			Product product = Product.builder().productId(productId)
					.productNumber(productDB.get("product_number", Integer.class))
					.productName(productDB.get("product_name", String.class))
					.productDescription(productDB.get("product_description", String.class))
					.productPrice(productDB.get("product_price", Double.class))
					.productStock(productDB.get("product_stock", Integer.class))
					.createAt(LocalDate.from(productDB.get("create_at", LocalDate.class)))
					.category(Category.builder().categoryId(productDB.get("category_id", Long.class))
							.categoryName(productDB.get("category_name", String.class)).build())
					.build();
			return product;
		}).all();

	
	}

	@Override
	public Flux<Product> findByCategoryId(Long categoryId) {
		return iProductRespository.findByCategoryCategoryId(categoryId);
	}

	@Override
	public Flux<Product> findBySubCategoryId(Long categoryId) {
		return null;// iProductRespository.findBySubCategorySubCategoryId(categoryId);
	}

	@Override
	public Flux<Product> findByProductNumber(Integer productNumber) {
		return iProductRespository.findByProductNumber(productNumber);
	}

}
