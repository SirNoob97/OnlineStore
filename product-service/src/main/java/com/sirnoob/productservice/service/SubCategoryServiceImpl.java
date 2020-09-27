package com.sirnoob.productservice.service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sirnoob.productservice.dto.SubCategoryRequest;
import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
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
  public SubCategoryResponse createSubCategory(SubCategoryRequest subCategoryRequest, MainCategory mainCategory) {
    SubCategory subCategory = iSubCategoryRepository.save(iSubCategoryMapper.mapSubCategoryRequestToSubCategory(subCategoryRequest, mainCategory));
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

    if(!subCategory.getProducts().isEmpty()){
      for (Product p : subCategory.getProducts()) {
        p.getSubCategories().remove(subCategory);
      }
    }
    iSubCategoryRepository.delete(subCategory);
  }

  @Override
  public SubCategory getSubCategoryById(Long subCategoryId) {
    return iSubCategoryRepository.findById(subCategoryId).orElseThrow(() -> new ResourceNotFoundException(SUB_CATEGORY_NOT_FOUND));
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
    Set<String> subCategories = iSubCategoryRepository.findAll(pageable).map(SubCategory::getSubCategoryName)
      .toSet();
    return CollectionValidator.throwExceptionIfSetIsEmpty(subCategories, NO_SUB_CATEGORIES_FOUND);
  }

  @Override
  public Set<SubCategory> getSubcategoriesByName(String[] subCategoriesNames) {
    Set<SubCategory> subCategories = Stream.of(subCategoriesNames).map(this::getSubCategoryByName).collect(Collectors.toSet());
    return CollectionValidator.throwExceptionIfSetIsEmpty(subCategories, NO_SUB_CATEGORIES_FOUND);
  }

  @Override
  public Set<SubCategory> getSubCategoryByMainCategory(MainCategory mainCategory) {
    Set<SubCategory> subCategories = iSubCategoryRepository.findByMainCategory(mainCategory).stream().collect(Collectors.toSet());
    return CollectionValidator.throwExceptionIfSetIsEmpty(subCategories, NO_SUB_CATEGORIES_FOUND);
  }

}
