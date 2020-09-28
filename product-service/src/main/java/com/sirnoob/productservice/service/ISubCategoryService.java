package com.sirnoob.productservice.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.SubCategory;

public interface ISubCategoryService {

  public SubCategoryResponse createSubCategory(String subCategoryName, MainCategory mainCategory);

  public SubCategoryResponse getSubCategoryResponseByName(String subCategoryName);

  public SubCategory getSubCategoryByName(String subCategoryName);

  public SubCategory getSubCategoryById(Long subCategoryId);

  public Set<String> getAllSubCategories(Pageable pageable);

  public Set<SubCategory> getSubcategoriesByName(String[] subCategoriesNames);

  public List<SubCategory> getSubCategoryByMainCategory (Long mainCategoryId);

  public void updateSubCategoryName(Long subCategoryId, String subCategoryName);

  public void deleteSubCategory(Long subCategoryId);
}
