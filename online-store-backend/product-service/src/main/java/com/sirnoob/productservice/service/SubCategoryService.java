package com.sirnoob.productservice.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.SubCategory;

public interface SubCategoryService {

  public SubCategoryResponse create(String subCategoryName, MainCategory mainCategory);

  public SubCategoryResponse getSubCategoryResponseByName(String subCategoryName);

  public SubCategory getByName(String subCategoryName);

  public SubCategory getById(Long subCategoryId);

  public Set<String> getAll(Pageable pageable);

  public Set<SubCategory> getSetByName(String[] subCategoriesNames);

  public List<SubCategory> getMainCategory (Long mainCategoryId);

  public void updateName(Long subCategoryId, String subCategoryName);

  public void deleteById(Long subCategoryId);
}
