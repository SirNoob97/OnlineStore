package com.sirnoob.productservice.mapper;

import org.springframework.stereotype.Component;

import com.sirnoob.productservice.dto.MainCategory;

import io.r2dbc.spi.Row;

@Component
public class MainCategoryMapperImpl implements IMainCategoryMapper {

	@Override
	public MainCategory mapToMainCategory(Row row) {
		return MainCategory.builder()
				.categoryId(row.get("category_id", Long.class))
				.categoryName(row.get("category_name", String.class)).build();
	}

}
