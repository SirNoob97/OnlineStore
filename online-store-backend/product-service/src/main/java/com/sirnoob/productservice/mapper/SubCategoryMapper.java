package com.sirnoob.productservice.mapper;

import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.SubCategory;

public interface SubCategoryMapper {

  public SubCategoryResponse subCategoryToSubCategoryResponse(SubCategory subCategory);

  public SubCategory subCategoryRequestToSubCategory(String subCategoryName, MainCategory mainCategory);
}
