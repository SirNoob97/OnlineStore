package com.sirnoob.productservice.service;

import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.exception.ResourceNotFoundException;
import com.sirnoob.productservice.repository.MainCategoryRepository;
import com.sirnoob.productservice.util.CollectionValidator;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MainCategoryServiceImpl implements MainCategoryService {

  private final ProductService productService;
  private final SubCategoryService subCategoryService;
  private final MainCategoryRepository mainCategoryRepository;

  private static final String MAIN_CATEGORY_NOT_FOUND = "Main Category Not Found";
  private static final String NO_MAIN_CATEGORIES_FOUND = "No Main Categories Found";

  @Transactional
  @Override
  public MainCategory create(MainCategory mainCategory) {
    return mainCategoryRepository.save(mainCategory);
  }

  @Transactional
  @Override
  public void updateName(Long mainCategoryId, String mainCategoryName) {
    if (mainCategoryRepository.updateName(mainCategoryName, mainCategoryId) < 1)
      throw new ResourceNotFoundException(MAIN_CATEGORY_NOT_FOUND);
  }

  @Transactional
  @Override
  public void deleteById(Long mainCategoryId) {
    MainCategory mainCategory = getById(mainCategoryId);

    subCategoryService.getMainCategory(mainCategoryId)
      .forEach(sc -> subCategoryService.deleteById(sc.getSubCategoryId()));

    productService.getByMainCategory(mainCategoryId).forEach(prs -> {
      if (prs.getSubCategories() != null && !prs.getSubCategories().isEmpty())
        prs.getSubCategories().clear();
    });

    mainCategoryRepository.delete(mainCategory);
  }

  @Override
  public MainCategory getById(Long mainCategoryId) {
    return mainCategoryRepository.findById(mainCategoryId)
      .orElseThrow(() -> new ResourceNotFoundException(MAIN_CATEGORY_NOT_FOUND));
  }

  @Override
  public MainCategory getByName(String mainCategoryName) {
    return mainCategoryRepository.findByMainCategoryName(mainCategoryName)
      .orElseThrow(() -> new ResourceNotFoundException(MAIN_CATEGORY_NOT_FOUND));
  }

  @Override
  public Set<String> getAll(Pageable pageable) {
    Set<String> mainCategories = mainCategoryRepository.findAll(pageable).stream().map(MainCategory::getMainCategoryName)
      .collect(Collectors.toSet());
    return CollectionValidator.throwExceptionIfSetIsEmpty(mainCategories, NO_MAIN_CATEGORIES_FOUND);
  }
}
