package com.sirnoob.productservice.service;

import java.util.Set;

import com.sirnoob.productservice.entity.MainCategory;

public interface IMainCategoryService {

  public String createMainCategory(MainCategory mainCategory);

  public int updateMainCategoryName(Long mainCategoryId, String mainCategoryName);

  public void deleteMainCategry(Long mainCategoryId);

  public MainCategory getMainCategoryByName(String mainCategoryName);

  public Set<String> getAllMainCategory(int page);
}
