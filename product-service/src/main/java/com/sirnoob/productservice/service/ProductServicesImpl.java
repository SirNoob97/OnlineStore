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
		final String insertQuery = "INSERT INTO products ( product_bar_code, product_name, product_description, product_stock, product_price, product_status, create_at, category_id) "
				+ "VALUES (:productBarCode, :productName, :productDescription, :productStock, :productPrice, 'CREATED', :createAt, :categoryId)";

		final String selectQuery = "SELECT * FROM products JOIN main_categories ON products.category_id = main_categories.category_id "
				+ "WHERE products. product_bar_code = :productBarCode";

		return transactionalOperator.execute(transaction -> {
			Mono<Void> insert = databaseClient.execute(insertQuery)
					.bind("productBarCode", productRequest.getProductBarCode())
					.bind("productName", productRequest.getProductName())
					.bind("productDescription", productRequest.getProductDescription())
					.bind("productStock", productRequest.getProductStock())
					.bind("productPrice", productRequest.getProductPrice()).bind("createAt", Instant.now())
					.bind("categoryId", productRequest.getCategoryId()).then();

			Mono<ProductResponse> select = databaseClient.execute(selectQuery)
					.bind("productBarCode", productRequest.getProductBarCode())
					.map(iProductMapper::mapToProductResponse).one();

			return insert.then(select);
		}).next();
	}

	@Override
	public Mono<ProductResponse> updateProduct(ProductRequest productRequest) {
		final String updateQuery = "UPDATE products SET  product_bar_code = :productBarCode, product_name = :productName, product_description = :productDescription, product_stock = :productStock, "
				+ "product_price = :productPrice, product_status = 'UPDATED', category_id = :categoryId WHERE products. product_bar_code = :productBarCode";
		final String selectQuery = "SELECT * FROM products JOIN main_categories ON products.category_id = main_categories.category_id "
				+ "WHERE products. product_bar_code = :productBarCode";

		return transactionalOperator.execute(transaction -> {
			Mono<Void> update = databaseClient.execute(updateQuery)
					.bind("productBarCode", productRequest.getProductBarCode())
					.bind("productName", productRequest.getProductName())
					.bind("productDescription", productRequest.getProductDescription())
					.bind("productStock", productRequest.getProductStock())
					.bind("productPrice", productRequest.getProductPrice())
					.bind("categoryId", productRequest.getCategoryId()).then();

			Mono<ProductResponse> select = databaseClient.execute(selectQuery)
					.bind("productBarCode", productRequest.getProductBarCode())
					.map(iProductMapper::mapToProductResponse).one();

			return update.then(select);
		}).next();
	}

	@Override
	public Mono<ProductResponse> updateStock(Long productBarCode, Integer quantity) {
		final String updateQuery = "UPDATE products SET product_stock = :quantity, product_status = 'UPDATED' WHERE  product_bar_code = :productBarCode";
		final String selectQuery = "SELECT * FROM products JOIN main_categories ON products.category_id = main_categories.category_id "
				+ "WHERE products.product_bar_code = :productBarCode";

		return transactionalOperator.execute(transaction -> {
			Mono<Void> updateStock = databaseClient.execute(updateQuery).bind("productBarCode", productBarCode)
					.bind("quantity", quantity).then();

			Mono<ProductResponse> select = databaseClient.execute(selectQuery).bind("productBarCode", productBarCode)
					.map(iProductMapper::mapToProductResponse).one();

			return updateStock.then(select);
		}).next();
	}

	@Override
	public Mono<ProductResponse> suspendProduct(Long productBarCode) {
		final String updateQuery = "UPDATE products SET product_status = 'SUSPENDED' WHERE  product_bar_code = :productBarCode";
		final String selectQuery = "SELECT * FROM products JOIN main_categories ON products.category_id = main_categories.category_id "
				+ "WHERE products.product_bar_code = :productBarCode";

		return transactionalOperator.execute(transaction -> {
			Mono<Void> updateStock = databaseClient.execute(updateQuery).bind("productBarCode", productBarCode).then();

			Mono<ProductResponse> select = databaseClient.execute(selectQuery).bind("productBarCode", productBarCode)
					.map(iProductMapper::mapToProductResponse).one();

			return updateStock.then(select);
		}).next();
	}

	@Override
	public Mono<ProductResponse> deleteProduct(Long productBarCode) {
		final String query = "WITH q AS (DELETE FROM products WHERE products. product_bar_code = :productBarCode RETURNING *) "
				+ "SELECT * FROM products INNER JOIN main_categories ON main_categories.category_id = products.category_id WHERE products. product_bar_code = "
				+ "(SELECT  product_bar_code FROM q)";
		return databaseClient.execute(query).bind("productBarCode", productBarCode).map(productDB -> {
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
	public Flux<ProductResponse> getAllProducts() {
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
	public Mono<ProductResponse> getProductByproductBarCode(Long productBarCode) {
		final String query = "SELECT * FROM products JOIN main_categories ON products.category_id = main_categories.category_id "
				+ "WHERE products. product_bar_code = :productBarCode";
		return databaseClient.execute(query).bind("productBarCode", productBarCode)
				.map(iProductMapper::mapToProductResponse).one();
	}

}
