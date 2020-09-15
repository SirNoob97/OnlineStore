package com.sirnoob.productservice.service;

import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.repository.IMainCategoryRepository;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MainCategoryServiceImpl implements IMainCategoryService {

  private final IProductService iProductService;
  private final ISubCategoryService iSubCategoryService;
  private final IMainCategoryRepository iMainCategoryRepository;


  public MainCategoryServiceImpl(@Lazy IProductService iProductService, ISubCategoryService iSubCategoryService,
      IMainCategoryRepository iMainCategoryRepository) {
    this.iProductService = iProductService;
    this.iSubCategoryService = iSubCategoryService;
    this.iMainCategoryRepository = iMainCategoryRepository;
  }

  @Override
  public String createMainCategory(MainCategory mainCategory) {
    return iMainCategoryRepository.save(mainCategory).getMainCategoryName();
  }

  @Transactional
  @Override
  public int updateMainCategoryName(Long mainCategoryId, String mainCategoryName) {
    return iMainCategoryRepository.updateMainCategoryName(mainCategoryName, mainCategoryId);
  }

  @Transactional
  @Override
  public void deleteMainCategory(Long mainCategoryId) {
    MainCategory mainCategory = getMainCategoryById(mainCategoryId);
    iSubCategoryService.getSubCategoryByMainCategory(mainCategory).forEach(sc -> sc.getProducts().clear());
    iProductService.getProductByMainCategory(mainCategory).forEach(prs -> prs.getSubCategories().clear());
    iMainCategoryRepository.delete(mainCategory);
  }

  @Override
  public MainCategory getMainCategoryById(Long mainCategoryId) {
  return iMainCategoryRepository.findById(mainCategoryId).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Main Category NOT FOUND with id " + mainCategoryId));
  }

  @Override
  public MainCategory getMainCategoryByName(String mainCategoryName) {
    return iMainCategoryRepository.findByMainCategoryName(mainCategoryName).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Main Category " + mainCategoryName + " Not Found"));
  }

  @Override
  public Set<String> getAllMainCategory(int page) {
    return iMainCategoryRepository.findAll(PageRequest.of(page, 10)).stream().map(MainCategory::getMainCategoryName)
        .collect(Collectors.toSet());
  }
}
