package com.sirnoob.productservice.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;
import com.sirnoob.productservice.exception.ResourceNotFoundException;
import com.sirnoob.productservice.mapper.ISubCategoryMapper;
import com.sirnoob.productservice.repository.ISubCategoryRepository;
import com.sirnoob.productservice.validator.CollectionValidator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SubCategoryServiceImpl implements ISubCategoryService {

  private final ISubCategoryMapper iSubCategoryMapper;
  private final ISubCategoryRepository iSubCategoryRepository;
  
  private static final String SUBCATEGORYNOTFOUND = "Sub Category Not Found";
  private static final String NOSUBCATEGORIESFOUND = "No Sub Categories Found";

  @Transactional
  @Override
  public String createSubCategory(SubCategory subCategory) {
    return iSubCategoryRepository.save(subCategory).getSubCategoryName();
  }

  @Transactional
  @Override
  public void updateSubCategoryName(Long subCategoryId, String subCategoryName) {
    if (iSubCategoryRepository.updateSubCategoryName(subCategoryName, subCategoryId) < 1)
      throw new ResourceNotFoundException(SUBCATEGORYNOTFOUND);
  }

  @Transactional
  @Override
  public void deleteSubCategory(Long subCategoryId) {
    SubCategory subCategory = getSubCategoryById(subCategoryId);
    for (Product p : subCategory.getProducts()) {
      subCategory.getProducts().remove(p);
      p.getSubCategories().remove(subCategory);
    }
    iSubCategoryRepository.delete(subCategory);
  }

  @Override
  public SubCategory getSubCategoryById(Long subCategoryId) {
    return iSubCategoryRepository.findById(subCategoryId).orElseThrow(() -> new ResourceNotFoundException(SUBCATEGORYNOTFOUND));
  }

  @Override
  public SubCategoryResponse getSubCategoryResponseByName(String subCategoryName) {
    return iSubCategoryMapper.mapSubCategoryToSubCategoryResponse(getSubCategoryByName(subCategoryName));
  }

  @Override
  public SubCategory getSubCategoryByName(String subCategoryName) {
    return iSubCategoryRepository.findBySubCategoryName(subCategoryName)
      .orElseThrow(() -> new ResourceNotFoundException(SUBCATEGORYNOTFOUND));
  }

  @Override
  public Set<String> getAllSubCategories(int page) {
    Set<String> subCategories = iSubCategoryRepository.findAll(PageRequest.of(page, 10)).map(SubCategory::getSubCategoryName)
      .toSet();
    return CollectionValidator.throwExceptionIfSetIsEmpty(subCategories, NOSUBCATEGORIESFOUND);
  }

  @Override
  public Set<SubCategory> getSubcategoriesByName(String[] subCategoriesNames) {

    Set<SubCategory> subCategories = new HashSet<>();

    for (String sc : subCategoriesNames) {
      subCategories.add(getSubCategoryByName(sc));
    }

    return CollectionValidator.throwExceptionIfSetIsEmpty(subCategories, NOSUBCATEGORIESFOUND);
  }

  @Override
  public Set<SubCategory> getSubCategoryByMainCategory(MainCategory mainCategory) {
    Set<SubCategory> subCategories = iSubCategoryRepository.findByMainCategory(mainCategory).stream().collect(Collectors.toSet());
    return CollectionValidator.throwExceptionIfSetIsEmpty(subCategories, NOSUBCATEGORIESFOUND);
  }

}
