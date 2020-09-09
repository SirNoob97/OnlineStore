package com.sirnoob.productservice.mapper;

import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.entity.SubCategory;

import org.springframework.stereotype.Component;

@Component
public class SubCategoryMapperImpl implements ISubCategoryMapper {

	@Override
	public SubCategoryResponse mapSuCategoryToSubCategoryResponse(SubCategory subCategory) {

		return SubCategoryResponse.builder()
      .subCategoryId(subCategory.getSubCategoryId())
      .subCategoryName(subCategory.getSubCategoryName())
      .build();
	}

}
