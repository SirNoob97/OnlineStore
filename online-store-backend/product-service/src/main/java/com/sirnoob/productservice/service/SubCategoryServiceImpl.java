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
import com.sirnoob.productservice.mapper.SubCategoryMapper;
import com.sirnoob.productservice.repository.SubCategoryRepository;
import com.sirnoob.productservice.util.CollectionValidator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SubCategoryServiceImpl implements SubCategoryService {

  private final SubCategoryMapper subCategoryMapper;
  private final SubCategoryRepository subCategoryRepository;

  private static final String SUB_CATEGORY_NOT_FOUND = "Sub Category Not Found";
  private static final String NO_SUB_CATEGORIES_FOUND = "No Sub Categories Found";

  @Transactional
  @Override
  public SubCategoryResponse create(String subCategoryName, MainCategory mainCategory) {
    SubCategory subCategory = subCategoryRepository
      .save(subCategoryMapper.subCategoryRequestToSubCategory(subCategoryName, mainCategory));
    return subCategoryMapper.subCategoryToSubCategoryResponse(subCategory);
  }

  @Transactional
  @Override
  public void updateName(Long subCategoryId, String subCategoryName) {
    if (subCategoryRepository.updateName(subCategoryName, subCategoryId) < 1)
      throw new ResourceNotFoundException(SUB_CATEGORY_NOT_FOUND);
  }

  @Transactional
  @Override
  public void deleteById(Long subCategoryId) {
    SubCategory subCategory = getById(subCategoryId);

    if (subCategory.getProducts() != null && !subCategory.getProducts().isEmpty()) {
      subCategory.getProducts().stream()
                               .filter(p -> p.getSubCategories() != null && !p.getSubCategories().isEmpty())
                               .forEach(prd -> prd.getSubCategories().remove(subCategory));
    }
    subCategoryRepository.delete(subCategory);
  }

  @Override
  public SubCategory getById(Long subCategoryId) {
    return subCategoryRepository.findById(subCategoryId)
      .orElseThrow(() -> new ResourceNotFoundException(SUB_CATEGORY_NOT_FOUND));
  }

  @Override
  public SubCategoryResponse getSubCategoryResponseByName(String subCategoryName) {
    return subCategoryMapper.subCategoryToSubCategoryResponse(getByName(subCategoryName));
  }

  @Override
  public SubCategory getByName(String subCategoryName) {
    return subCategoryRepository.findBySubCategoryName(subCategoryName)
      .orElseThrow(() -> new ResourceNotFoundException(SUB_CATEGORY_NOT_FOUND));
  }

  @Override
  public Set<String> getAll(Pageable pageable) {
    Set<String> subCategories = subCategoryRepository.findAll(pageable).map(SubCategory::getSubCategoryName).toSet();
    return CollectionValidator.throwExceptionIfSetIsEmpty(subCategories, NO_SUB_CATEGORIES_FOUND);
  }

  @Override
  public Set<SubCategory> getSetByName(String[] subCategoriesNames) {
    return Stream.of(subCategoriesNames).map(this::getByName).collect(Collectors.toSet());
  }

  @Override
  public List<SubCategory> getMainCategory(Long mainCategoryId) {
    return subCategoryRepository.findByMainCategoryMainCategoryId(mainCategoryId);
  }

}
