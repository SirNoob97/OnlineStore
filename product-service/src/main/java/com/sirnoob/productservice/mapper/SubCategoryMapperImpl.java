package com.sirnoob.productservice.mapper;

import java.util.stream.Collectors;

import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;

import org.springframework.stereotype.Component;

@Component
public class SubCategoryMapperImpl implements ISubCategoryMapper {

  @Override
  public SubCategoryResponse mapSubCategoryToSubCategoryResponse(SubCategory subCategory) {
    return SubCategoryResponse.builder()
                              .subCategoryId(subCategory.getSubCategoryId())
                              .subCategoryName(subCategory.getSubCategoryName())
                              .mainCategory(subCategory.getMainCategory().getMainCategoryName())
                              .products(subCategory.getProducts().stream().map(Product::getProductName).collect(Collectors.toSet()))
                              .build();
  }
}
