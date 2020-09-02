package com.sirnoob.productservice.service;

import java.time.Instant;

import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.mapper.IProductMapper;

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
		final String insertQuery = "INSERT INTO products (product_number, product_name, product_description, product_stock, product_price, product_status, create_at, category_id) "
				+ "VALUES (:productNumber, :productName, :productDescription, :productStock, :productPrice, 'CREATED', :createAt, :categoryId)";

		final String selectQuery = "SELECT * FROM products JOIN main_categories ON products.category_id = main_categories.category_id "
				+ "WHERE products.product_number = :productNumber";

		return transactionalOperator.execute(transaction -> {
			Mono<Void> insert = databaseClient.execute(insertQuery)
					.bind("productNumber", productRequest.getProductNumber())
					.bind("productName", productRequest.getProductName())
					.bind("productDescription", productRequest.getProductDescription())
					.bind("productStock", productRequest.getProductStock())
					.bind("productPrice", productRequest.getProductPrice()).bind("createAt", Instant.now())
					.bind("categoryId", productRequest.getCategoryId()).then();

			Mono<ProductResponse> select = databaseClient.execute(selectQuery)
					.bind("productNumber", productRequest.getProductNumber()).map(iProductMapper::mapToProductResponse)
					.one();

			return insert.then(select);
		}).next();
	}

	@Override
	public Mono<ProductResponse> updateProduct(ProductRequest productRequest) {
		final String updateQuery = "UPDATE products SET product_number = :productNumber, product_name = :productName, product_description = :productDescription, product_stock = :productStock, "
				+ "product_price = :productPrice, product_status = 'UPDATED', category_id = :categoryId WHERE products.product_number = :productNumber";
		final String selectQuery = "SELECT * FROM products JOIN main_categories ON products.category_id = main_categories.category_id "
				+ "WHERE products.product_number = :productNumber";

		return transactionalOperator.execute(transaction -> {
			Mono<Void> update = databaseClient.execute(updateQuery)
					.bind("productNumber", productRequest.getProductNumber())
					.bind("productName", productRequest.getProductName())
					.bind("productDescription", productRequest.getProductDescription())
					.bind("productStock", productRequest.getProductStock())
					.bind("productPrice", productRequest.getProductPrice())
					.bind("categoryId", productRequest.getCategoryId()).then();

			Mono<ProductResponse> select = databaseClient.execute(selectQuery)
					.bind("productNumber", productRequest.getProductNumber()).map(iProductMapper::mapToProductResponse)
					.one();

			return update.then(select);
		}).next();
	}

	@Override
	public Mono<ProductResponse> updateStock(Integer productNumber, Integer quantity) {
		final String updateQuery = "UPDATE products SET product_stock = :quantity, product_status = 'UPDATED' WHERE product_number = :productNumber";
		final String selectQuery = "SELECT * FROM products JOIN main_categories ON products.category_id = main_categories.category_id "
				+ "WHERE products.product_number = :productNumber";

		return transactionalOperator.execute(transaction -> {
			Mono<Void> updateStock = databaseClient.execute(updateQuery).bind("productNumber", productNumber)
					.bind("quantity", quantity).then();

			Mono<ProductResponse> select = databaseClient.execute(selectQuery).bind("productNumber", productNumber)
					.map(iProductMapper::mapToProductResponse).one();

			return updateStock.then(select);
		}).next();
	}

	@Override
	public Mono<ProductResponse> deleteProduct(Integer productNumber) {
		final String query = "WITH q AS (DELETE FROM products WHERE products.product_number = :productNumber RETURNING *) "
				+ "SELECT * FROM products INNER JOIN main_categories ON main_categories.category_id = products.category_id WHERE products.product_number = "
				+ "(SELECT product_number FROM q)";
		return databaseClient.execute(query).bind("productNumber", productNumber).map(productDB -> {
			ProductResponse product = iProductMapper.mapToProductResponse(productDB);
			product.setProductStatus("DELETED");
			return product;
		}).first();
	}

	@Override
	public Mono<ProductInvoiceResponse> getProductInvoiceResponseById(Long productId) {
		final String query = "SELECT * FROM products INNER JOIN main_categories ON products.category_id = main_categories.category_id "
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
	public Flux<ProductResponse> getProductByName(String productName) {
		final String query = "SELECT * FROM products INNER JOIN main_categories ON products.category_id = main_categories.category_id WHERE product_name ~* :productName";
		return databaseClient.execute(query).bind("productName", productName).map(iProductMapper::mapToProductResponse)
				.all();
	}

	@Override
	public Flux<ProductResponse> getProductByMainCategory(Long categoryId) {
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
