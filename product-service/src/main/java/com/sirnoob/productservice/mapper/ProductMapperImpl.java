package com.sirnoob.productservice.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductResponse;

import io.r2dbc.spi.Row;

@Component
public class ProductMapperImpl implements IProductMapper {

	@Override
	public ProductInvoiceResponse mapToInvoiceResponse(Row row) {

		return ProductInvoiceResponse.builder().productBarCode(row.get("product_bar_code", Long.class))
				.productName(row.get("product_name", String.class))
				.productDescription(row.get("product_description", String.class))
				.productPrice(row.get("product_price", Double.class))
				.mainCategory(row.get("category_name", String.class)).build();
	}

	@Override
	public ProductResponse mapToProductResponse(Row row) {

		return ProductResponse.builder().productId(row.get("product_id", Long.class))
				.productBarCode(row.get("product_bar_code", Long.class))
				.productName(row.get("product_name", String.class))
				.productDescription(row.get("product_description", String.class))
				.productPrice(row.get("product_price", Double.class))
				.productStock(row.get("product_stock", Integer.class))
				.createAt(LocalDate.from(row.get("create_at", LocalDate.class)))
				.productStatus(row.get("product_status", String.class))
				.mainCategory(row.get("category_name", String.class)).build();
	}

}
