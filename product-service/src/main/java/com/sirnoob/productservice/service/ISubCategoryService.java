package com.sirnoob.productservice.service;

import java.util.Set;

import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.SubCategory;

public interface ISubCategoryService {

  public String createSubCategory(SubCategory subCategory);

  public SubCategoryResponse getSubCategoryResponseByName(String subCategoryName);

  public SubCategory getSubCategoryByName(String subCategoryName);

  public SubCategory getSubCategoryById(Long subCategoryId);

  public Set<String> getAllSubCategories(int page);

  public Set<SubCategory> getSubcategoriesByName(String[] subCategoriesNames);

  public Set<SubCategory> getSubCategoryByMainCategory (MainCategory mainCategory);

  public void updateSubCategoryName(Long subCategoryId, String subCategoryName);

  public void deleteSubCategory(Long subCategoryId);
}
