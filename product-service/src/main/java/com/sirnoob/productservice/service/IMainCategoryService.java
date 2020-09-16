package com.sirnoob.productservice.service;

import java.util.Set;

import com.sirnoob.productservice.entity.MainCategory;

public interface IMainCategoryService {

  public String createMainCategory(MainCategory mainCategory);

  public MainCategory getMainCategoryById(Long mainCategoryId);

  public MainCategory getMainCategoryByName(String mainCategoryName);

  public Set<String> getAllMainCategory(int page);

  public void updateMainCategoryName(Long mainCategoryId, String mainCategoryName);
  
  public void deleteMainCategory(Long mainCategoryId);
}
