package com.sirnoob.productservice.mapper;

import com.sirnoob.productservice.dto.MainCategory;

import io.r2dbc.spi.Row;

public interface IMainCategoryMapper {

	public MainCategory mapToMainCategory(Row row);
}
