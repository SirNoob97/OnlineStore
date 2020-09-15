package com.sirnoob.productservice.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.sirnoob.productservice.dto.SubCategoryResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;
import com.sirnoob.productservice.mapper.ISubCategoryMapper;
import com.sirnoob.productservice.repository.ISubCategoryRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SubCategoryServiceImpl implements ISubCategoryService {

  private final ISubCategoryMapper iSubCategoryMapper;
  private final ISubCategoryRepository iSubCategoryRepository;

  @Override
  public String createSubCategory(SubCategory subCategory) {
    return iSubCategoryRepository.save(subCategory).getSubCategoryName();
  }

  @Transactional
  @Override
  public int updateSubCategoryName(Long subCategoryId, String subCategoryName) {
    return iSubCategoryRepository.updateSubCategoryName(subCategoryName, subCategoryId);
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
    return iSubCategoryRepository.findById(subCategoryId).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sub Category NOT FOUND with id " + subCategoryId));
  }


  @Override
  public SubCategoryResponse getSubCategoryResponseByName(String subCategoryName) {
    return iSubCategoryMapper.mapSubCategoryToSubCategoryResponse(getSubCategoryByName(subCategoryName));
  }

  @Override
  public SubCategory getSubCategoryByName(String subCategoryName) {
    return iSubCategoryRepository.findBySubCategoryName(subCategoryName).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sub Category " + subCategoryName + " Not Found"));
  }

  @Override
  public Set<String> getAllSubCategories(int page) {
    return iSubCategoryRepository.findAll(PageRequest.of(page, 10)).map(SubCategory::getSubCategoryName).toSet();
  }

  @Override
  public Set<SubCategory> getSubcategoriesByName(String[] subCategoriesNames) {

    Set<SubCategory> subCategories = new HashSet<>();

    for (String sc : subCategoriesNames) {
      subCategories.add(getSubCategoryByName(sc));
    }

    return subCategories;
  }

  @Override
  public Set<SubCategory> getSubCategoryByMainCategory(MainCategory mainCategory) {
    return iSubCategoryRepository.findByMainCategory(mainCategory).stream().collect(Collectors.toSet());
  }

}
