package com.sirnoob.productservice.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.SubCategory;
import com.sirnoob.productservice.exception.ResourceNotFoundException;
import com.sirnoob.productservice.mapper.ISubCategoryMapper;
import com.sirnoob.productservice.repository.ISubCategoryRepository;
import com.sirnoob.productservice.util.CollectionValidator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SubCategoryServiceImpl implements ISubCategoryService {

  private final ISubCategoryMapper iSubCategoryMapper;
  private final ISubCategoryRepository iSubCategoryRepository;

  private static final String SUB_CATEGORY_NOT_FOUND = "Sub Category Not Found";
  private static final String NO_SUB_CATEGORIES_FOUND = "No Sub Categories Found";

  @Transactional
  @Override
  public SubCategoryResponse createSubCategory(String subCategoryName, MainCategory mainCategory) {
    SubCategory subCategory = iSubCategoryRepository
      .save(iSubCategoryMapper.mapSubCategoryRequestToSubCategory(subCategoryName, mainCategory));
    return iSubCategoryMapper.mapSubCategoryToSubCategoryResponse(subCategory);
  }

  @Transactional
  @Override
  public void updateSubCategoryName(Long subCategoryId, String subCategoryName) {
    if (iSubCategoryRepository.updateSubCategoryName(subCategoryName, subCategoryId) < 1)
      throw new ResourceNotFoundException(SUB_CATEGORY_NOT_FOUND);
  }

  @Transactional
  @Override
  public void deleteSubCategory(Long subCategoryId) {
    SubCategory subCategory = getSubCategoryById(subCategoryId);

    if (subCategory.getProducts() != null && !subCategory.getProducts().isEmpty()) {
      subCategory.getProducts().stream()
      .filter(p -> p.getSubCategories() != null && !p.getSubCategories().isEmpty())
      .forEach(prd -> prd.getSubCategories().remove(subCategory));
//      for (Product p : subCategory.getProducts()) {
//        if (p.getSubCategories() != null && !p.getSubCategories().isEmpty())
//          p.getSubCategories().remove(subCategory);
//      }
    }
    iSubCategoryRepository.delete(subCategory);
  }

  @Override
  public SubCategory getSubCategoryById(Long subCategoryId) {
    return iSubCategoryRepository.findById(subCategoryId)
      .orElseThrow(() -> new ResourceNotFoundException(SUB_CATEGORY_NOT_FOUND));
  }

  @Override
  public SubCategoryResponse getSubCategoryResponseByName(String subCategoryName) {
    return iSubCategoryMapper.mapSubCategoryToSubCategoryResponse(getSubCategoryByName(subCategoryName));
  }

  @Override
  public SubCategory getSubCategoryByName(String subCategoryName) {
    return iSubCategoryRepository.findBySubCategoryName(subCategoryName)
      .orElseThrow(() -> new ResourceNotFoundException(SUB_CATEGORY_NOT_FOUND));
  }

  @Override
  public Set<String> getAllSubCategories(Pageable pageable) {
    Set<String> subCategories = iSubCategoryRepository.findAll(pageable).map(SubCategory::getSubCategoryName).toSet();
    return CollectionValidator.throwExceptionIfSetIsEmpty(subCategories, NO_SUB_CATEGORIES_FOUND);
  }

  @Override
  public Set<SubCategory> getSubcategoriesByName(String[] subCategoriesNames) {
    return Stream.of(subCategoriesNames).map(this::getSubCategoryByName).collect(Collectors.toSet());
  }

  @Override
  public List<SubCategory> getSubCategoryByMainCategory(Long mainCategoryId) {
    return iSubCategoryRepository.findByMainCategoryMainCategoryId(mainCategoryId);
  }

}
