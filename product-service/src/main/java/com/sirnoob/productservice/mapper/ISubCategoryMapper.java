package com.sirnoob.productservice.mapper;

import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.SubCategory;

public interface ISubCategoryMapper {

  public SubCategoryResponse mapSubCategoryToSubCategoryResponse(SubCategory subCategory);

  public SubCategory mapSubCategoryRequestToSubCategory(String subCategoryName, MainCategory mainCategory);
}
