package com.sirnoob.productservice.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.sirnoob.productservice.dto.SubCategoryRequest;
import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;
import com.sirnoob.productservice.exception.ResourceNotFoundException;
import com.sirnoob.productservice.mapper.ISubCategoryMapper;
import com.sirnoob.productservice.repository.ISubCategoryRepository;
import com.sirnoob.productservice.util.CollectionValidator;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SubCategoryServiceImpl implements ISubCategoryService {

  private final ISubCategoryMapper iSubCategoryMapper;
  private final ISubCategoryRepository iSubCategoryRepository;
  private final IMainCategoryService iMainCategoryService;

  private static final String SUB_CATEGORY_NOT_FOUND = "Sub Category Not Found";
  private static final String NO_SUB_CATEGORIES_FOUND = "No Sub Categories Found";

  @Transactional
  @Override
  public String createSubCategory(SubCategoryRequest subCategoryRequest) {
    MainCategory mainCategory = iMainCategoryService.getMainCategoryByName(subCategoryRequest.getMainCategoryName());
    return iSubCategoryRepository.save(iSubCategoryMapper.mapSubCategoryRequestToSubCategory(subCategoryRequest, mainCategory)).getSubCategoryName();
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
  public Set<String> getAllSubCategories(int page) {
    Set<String> subCategories = iSubCategoryRepository.findAll(PageRequest.of(page, 10)).map(SubCategory::getSubCategoryName)
      .toSet();
    return CollectionValidator.throwExceptionIfSetIsEmpty(subCategories, NO_SUB_CATEGORIES_FOUND);
  }

  @Override
  public Set<SubCategory> getSubcategoriesByName(String[] subCategoriesNames) {

    Set<SubCategory> subCategories = new HashSet<>();

    for (String sc : subCategoriesNames) {
      subCategories.add(getSubCategoryByName(sc));
    }

    return CollectionValidator.throwExceptionIfSetIsEmpty(subCategories, NO_SUB_CATEGORIES_FOUND);
  }

  @Override
  public Set<SubCategory> getSubCategoryByMainCategory(MainCategory mainCategory) {
    Set<SubCategory> subCategories = iSubCategoryRepository.findByMainCategory(mainCategory).stream().collect(Collectors.toSet());
    return CollectionValidator.throwExceptionIfSetIsEmpty(subCategories, NO_SUB_CATEGORIES_FOUND);
  }

}
