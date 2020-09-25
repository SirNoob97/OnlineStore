package com.sirnoob.productservice.service;

import java.util.Set;

import com.sirnoob.productservice.entity.MainCategory;

import org.springframework.data.domain.Pageable;

public interface IMainCategoryService {

  public MainCategory createMainCategory(MainCategory mainCategory);

  public MainCategory getMainCategoryById(Long mainCategoryId);

  public MainCategory getMainCategoryByName(String mainCategoryName);

  public Set<String> getAllMainCategory(Pageable pageable);

  public void updateMainCategoryName(Long mainCategoryId, String mainCategoryName);

  public void deleteMainCategory(Long mainCategoryId);
}
