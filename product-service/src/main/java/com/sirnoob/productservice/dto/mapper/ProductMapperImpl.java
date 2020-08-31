package com.sirnoob.productservice.dto.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.sirnoob.productservice.dto.template.ProductResponse;
import com.sirnoob.productservice.entity.MainCategory;

import io.r2dbc.spi.Row;

@Component
public class ProductMapperImpl implements IProductMapper{

	public ProductResponse mapToProductResponse(Row row) {
		
		ProductResponse productResponse = ProductResponse.builder().productId(row.get("product_id", Long.class))
				.productNumber(row.get("product_number", Integer.class))
				.productName(row.get("product_name", String.class))
				.productDescription(row.get("product_description", String.class))
				.productPrice(row.get("product_price", Double.class))
				.productStock(row.get("product_stock", Integer.class))
				.createAt(LocalDate.from(row.get("create_at", LocalDate.class)))
				.mainCategory(MainCategory.builder().categoryId(row.get("category_id", Long.class))
						.categoryName(row.get("category_name", String.class)).build())
				.build();

		return productResponse;
	}

}
