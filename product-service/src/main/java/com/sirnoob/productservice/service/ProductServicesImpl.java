package com.sirnoob.productservice.service;

import java.time.Instant;

import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.sirnoob.productservice.dto.mapper.IProductMapper;
import com.sirnoob.productservice.dto.template.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.template.ProductRequest;
import com.sirnoob.productservice.dto.template.ProductResponse;
import com.sirnoob.productservice.entity.Product;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProductServicesImpl implements IProductService {

	private final IProductMapper iProductMapper;
	private final TransactionalOperator transactionalOperator;
	private final DatabaseClient databaseClient;

	@Override
	public Mono<ProductResponse> createProduct(ProductRequest productRequest) {
		final String query = " INSERT INTO products (product_number, product_name, product_description, product_stock, product_price, create_at, category_id) "
				+ "VALUES (:productNumber, :productName, :productDescription, :productStock, :productPrice, :createAt, :categoryId)";

		final String queryFetch = "SELECT * FROM products JOIN main_categories ON products.category_id = main_categories.category_id "
				+ "WHERE products.product_number = :productNumber";

		return transactionalOperator.execute(tx -> {
			Mono<Void> insert = databaseClient.execute(query).bind("productNumber", productRequest.getProductNumber())
					.bind("productName", productRequest.getProductName())
					.bind("productDescription", productRequest.getProductDescription())
					.bind("productStock", productRequest.getProductStock())
					.bind("productPrice", productRequest.getProductPrice()).bind("createAt", Instant.now())
					.bind("categoryId", productRequest.getCategoryId()).then();

			Mono<ProductResponse> select = databaseClient.execute(queryFetch)
					.bind("productNumber", productRequest.getProductNumber()).map(iProductMapper::mapToProductResponse)
					.one();

			return insert.then(select);
		}).next();
	}

	@Override
	public Mono<Product> updateProduct(Product product) {
		return null;
	}

	@Override
	public Mono<Product> updateStock(Long id, Integer quantity) {
		return null;
	}

	@Override
	public Mono<Product> deleteProduct(Long productId) {
		return null;
	}

	@Override
	public Mono<ProductInvoiceResponse> getProductInvoiceResponseById(Long productId) {
		final String query = "SELECT * FROM PRODUCTS INNER JOIN main_categories ON products.category_id = main_categories.category_id "
				+ "WHERE products.product_id = :productId";
		return databaseClient.execute(query).bind("productId", productId).map(iProductMapper::mapToInvoiceResponse)
				.first();
	}

	@Override
	public Flux<ProductResponse> listAll() {
		final String query = "SELECT * FROM products INNER JOIN main_categories ON products.category_id = main_categories.category_id";
		return databaseClient.execute(query).map(iProductMapper::mapToProductResponse).all();
	}

	@Override
	public Flux<ProductResponse> findByMainCategory(Long categoryId) {
		final String query = "SELECT * FROM products INNER JOIN main_categories ON main_categories.category_id = products.category_id "
				+ "WHERE products.category_id = :categoryId";
		return databaseClient.execute(query).bind("categoryId", categoryId).map(iProductMapper::mapToProductResponse)
				.all();
	}

	@Override
	public Mono<ProductResponse> getProductByProductNumber(Integer productNumber) {
		final String query = "SELECT * FROM products JOIN main_categories ON products.category_id = main_categories.category_id "
				+ "WHERE products.product_number = :productNumber";
		return databaseClient.execute(query).bind("productNumber", productNumber)
				.map(iProductMapper::mapToProductResponse).one();
	}

}
