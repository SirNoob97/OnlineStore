package com.sirnoob.productservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.dto.ProductView;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;
import com.sirnoob.productservice.exception.ResourceNotFoundException;
import com.sirnoob.productservice.mapper.IProductMapper;
import com.sirnoob.productservice.repository.IProductRepository;
import com.sirnoob.productservice.util.CollectionValidator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements IProductService {

  private final IMainCategoryService iMainCategoryService;
  private final ISubCategoryService iSubCategoryService;
  private final IProductRepository iProductRepository;
  private final IProductMapper iProductMapper;

  private static final String PRODUCT_NOT_FOUND = "Product Not Found";
  private static final String NO_PRODUCTS_FOUND = "No Products Found";

  @Transactional
  @Override
  public ProductResponse createProduct(ProductRequest productRequest) {

    MainCategory mainCategory = iMainCategoryService.getMainCategoryByName(productRequest.getMainCategoryName());

    Set<SubCategory> subCategories = iSubCategoryService.getSubcategoriesByName(productRequest.getSubCategoriesNames());

    Product product = iProductMapper.mapProductRequestToProduct(productRequest, mainCategory, subCategories);

    product.setProductStatus("CREATED");
    product.setCreateAt(LocalDate.now());

    return iProductMapper.mapProductToProductResponse(iProductRepository.save(product));
  }

  @Transactional
  @Override
  public ProductResponse updateProduct(Long productId, ProductRequest productRequest) {

    Product product = getProductById(productId);

    product.setProductBarCode(productRequest.getProductBarCode());
    product.setProductName(productRequest.getProductName());
    product.setProductDescription(productRequest.getProductDescription());
    product.setProductStock(productRequest.getProductStock());
    product.setProductPrice(productRequest.getProductPrice());
    product.setMainCategory(iMainCategoryService.getMainCategoryByName(productRequest.getMainCategoryName()));
    product.setSubCategories(iSubCategoryService.getSubcategoriesByName(productRequest.getSubCategoriesNames()));

    return iProductMapper.mapProductToProductResponse(iProductRepository.save(product));
  }

  @Transactional
  @Override
  public void updateProductStock(Long productBarCode, Integer quantity) {
    if (iProductRepository.updateProductStockByProductBarCode(quantity, productBarCode) < 1)
      throw new ResourceNotFoundException(PRODUCT_NOT_FOUND);
  }

  @Transactional
  @Override
  public void deleteProductById(Long productId) {
    iProductRepository.delete(getProductById(productId));
  }

  @Override
  public ProductResponse getProductResponseByBarCodeOrProductName(Long productBarCode, String productName) {
    return iProductMapper
        .mapProductToProductResponse(iProductRepository.findByProductBarCodeOrProductName(productBarCode, productName)
            .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND)));
  }

  @Override
  public ProductInvoiceResponse getProductForInvoiceResponse(Long productBarCode, String productName) {
    return iProductRepository.findProductForInvoice(productBarCode, productName)
        .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND));
  }

  @Override
  public Product getProductById(Long productId) {
    return iProductRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND));
  }

  @Override
  public List<Product> getProductByMainCategory(Long mainCategoryId) {
    return iProductRepository.findByMainCategoryMainCategoryId(mainCategoryId);
  }

  @Override
  public ProductView findProductViewByName(String productName) {
    return iProductMapper.mapProductToProductView(iProductRepository.findByProductName(productName)
        .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND)));
  }

  @Override
  public Page<ProductListView> getProductListViewByName(String productName, Pageable pageable) {
    Page<ProductListView> products = iProductRepository.findByProductNameContainingIgnoreCase(productName, pageable);
    return CollectionValidator.throwExceptionIfPageIsEmpty(products, NO_PRODUCTS_FOUND);
  }

  @Override
  public Page<ProductListView> getPageOfProductListView(Pageable pageable) {
    Page<ProductListView> products = iProductRepository.getAll(pageable);
    return CollectionValidator.throwExceptionIfPageIsEmpty(products, NO_PRODUCTS_FOUND);
  }

  @Override
  public Page<ProductListView> getProductListViewByMainCategory(Long mainCategoryId, Pageable pageable) {
    Page<ProductListView> products = iProductRepository.findByMainCategoryMainCategoryId(mainCategoryId, pageable);
    return CollectionValidator.throwExceptionIfPageIsEmpty(products, NO_PRODUCTS_FOUND);
  }

  @Override
  public Set<ProductListView> getProductListViewBySubCategory(String[] subCategoriesNames) {
    Set<ProductListView> products = iSubCategoryService.getSubcategoriesByName(subCategoriesNames).stream()
        .map(sc -> iProductRepository.findBySubCategory(sc))
        .flatMap(pdv -> pdv.stream())
        .collect(Collectors.toSet());
    return CollectionValidator.throwExceptionIfSetIsEmpty(products, NO_PRODUCTS_FOUND);
  }
}
