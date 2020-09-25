package com.sirnoob.productservice.service;

import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.exception.ResourceNotFoundException;
import com.sirnoob.productservice.repository.IMainCategoryRepository;
import com.sirnoob.productservice.util.CollectionValidator;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MainCategoryServiceImpl implements IMainCategoryService {

	private final IProductService iProductService;
	private final ISubCategoryService iSubCategoryService;
	private final IMainCategoryRepository iMainCategoryRepository;

	private static final String MAIN_CATEGORY_NOT_FOUND = "Main Category Not Found";
	private static final String NO_MAIN_CATEGORIES_FOUND = "No Main Categories Found";

	public MainCategoryServiceImpl(@Lazy IProductService iProductService, @Lazy ISubCategoryService iSubCategoryService,
			IMainCategoryRepository iMainCategoryRepository) {
		this.iProductService = iProductService;
		this.iSubCategoryService = iSubCategoryService;
		this.iMainCategoryRepository = iMainCategoryRepository;
	}

	@Transactional
	@Override
	public MainCategory createMainCategory(MainCategory mainCategory) {
		return iMainCategoryRepository.save(mainCategory);
	}

	@Transactional
	@Override
	public void updateMainCategoryName(Long mainCategoryId, String mainCategoryName) {
		if(iMainCategoryRepository.updateMainCategoryName(mainCategoryName, mainCategoryId) < 1)
			throw new ResourceNotFoundException(MAIN_CATEGORY_NOT_FOUND);
	}

	@Transactional
	@Override
	public void deleteMainCategory(Long mainCategoryId) {
		MainCategory mainCategory = getMainCategoryById(mainCategoryId);
		iSubCategoryService.getSubCategoryByMainCategory(mainCategory).forEach(sc -> iSubCategoryService.deleteSubCategory(sc.getSubCategoryId()));
		iProductService.getProductByMainCategory(mainCategory).forEach(prs -> prs.getSubCategories().clear());
		iMainCategoryRepository.delete(mainCategory);
	}

	@Override
	public MainCategory getMainCategoryById(Long mainCategoryId) {
		return iMainCategoryRepository.findById(mainCategoryId)
				.orElseThrow(() -> new ResourceNotFoundException(MAIN_CATEGORY_NOT_FOUND));
	}

	@Override
	public MainCategory getMainCategoryByName(String mainCategoryName) {
		return iMainCategoryRepository.findByMainCategoryName(mainCategoryName)
				.orElseThrow(() -> new ResourceNotFoundException(MAIN_CATEGORY_NOT_FOUND));
	}

	@Override
	public Set<String> getAllMainCategory(Pageable pageable) {
		Set<String> mainCategories = iMainCategoryRepository.findAll(pageable).stream().map(MainCategory::getMainCategoryName)
				.collect(Collectors.toSet());
		return CollectionValidator.throwExceptionIfSetIsEmpty(mainCategories, NO_MAIN_CATEGORIES_FOUND);
	}
}
