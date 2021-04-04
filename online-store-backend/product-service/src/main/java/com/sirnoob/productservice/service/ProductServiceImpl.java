package com.sirnoob.productservice.service;

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
import com.sirnoob.productservice.mapper.ProductMapper;
import com.sirnoob.productservice.repository.ProductRepository;
import com.sirnoob.productservice.util.CollectionValidator;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

  private final SubCategoryService subCategoryService;
  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  private static final String PRODUCT_NOT_FOUND = "Product Not Found";
  private static final String NO_PRODUCTS_FOUND = "No Products Found";

  @Transactional
  @Override
  public ProductResponse create(ProductRequest productRequest, MainCategory mainCategory) {
    Set<SubCategory> subCategories = subCategoryService.getSetByName(productRequest.getSubCategoriesNames());

    Product product = productMapper.productRequestToProduct(productRequest, mainCategory, subCategories);

    product.setProductStatus("CREATED");

    return productMapper.productToProductResponse(productRepository.save(product));
  }

  @Transactional
  @Override
  public ProductResponse update(Long productId, ProductRequest productRequest, MainCategory mainCategory) {

    Product product = getById(productId);

    product.setProductBarCode(productRequest.getProductBarCode());
    product.setProductName(productRequest.getProductName());
    product.setProductDescription(productRequest.getProductDescription());
    product.setProductStock(productRequest.getProductStock());
    product.setProductPrice(productRequest.getProductPrice());
    product.setMainCategory(mainCategory);
    product.setSubCategories(subCategoryService.getSetByName(productRequest.getSubCategoriesNames()));

    return productMapper.productToProductResponse(productRepository.save(product));
  }

  @Transactional
  @Override
  public void updateStock(Long productBarCode, Integer quantity) {
    if (productRepository.updateStockByBarCode(quantity, productBarCode) < 1)
      throw new ResourceNotFoundException(PRODUCT_NOT_FOUND);
  }

  @Transactional
  @Override
  public void deleteById(Long productId) {
    productRepository.delete(getById(productId));
  }

  @Transactional(readOnly = true)
  @Override
  public ProductResponse getProductResponseByBarCodeOrProductName(Long productBarCode, String productName) {
    return productMapper
        .productToProductResponse(productRepository.findByProductBarCodeOrProductName(productBarCode, productName)
            .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND)));
  }

  @Override
  public ProductInvoiceResponse getForInvoiceResponse(Long productBarCode, String productName) {
    return productRepository.findForInvoice(productBarCode, productName)
        .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND));
  }

  @Override
  public Product getById(Long productId) {
    return productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND));
  }

  @Override
  public List<Product> getByMainCategory(Long mainCategoryId) {
     return productRepository.findByMainCategoryMainCategoryId(mainCategoryId);
  }

  @Override
  public ProductView getProductViewByName(String productName) {
    return productMapper.productToProductView(productRepository.findByProductName(productName)
        .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND)));
  }

  @Transactional(readOnly = true)
  @Override
  public Page<ProductListView> getListViewByName(String productName, Pageable pageable) {
    Page<ProductListView> products = productRepository.findByProductNameContainingIgnoreCase(productName, pageable);
    return CollectionValidator.throwExceptionIfPageIsEmpty(products, NO_PRODUCTS_FOUND);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<ProductListView> getListView(Pageable pageable) {
    Page<ProductListView> products = productRepository.getAll(pageable);
    return CollectionValidator.throwExceptionIfPageIsEmpty(products, NO_PRODUCTS_FOUND);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<ProductListView> getListViewByMainCategory(Long mainCategoryId, Pageable pageable) {
    Page<ProductListView> products = productRepository.findByMainCategoryMainCategoryId(mainCategoryId, pageable);
    return CollectionValidator.throwExceptionIfPageIsEmpty(products, NO_PRODUCTS_FOUND);
  }

  @Transactional(readOnly = true)
  @Override
  public Set<ProductListView> getListViewBySubCategory(String[] subCategoriesNames) {
    Set<ProductListView> products = subCategoryService.getSetByName(subCategoriesNames).stream()
        .map(sc -> productRepository.findBySubCategory(sc))
        .flatMap(pdv -> pdv.stream())
        .collect(Collectors.toSet());
    return CollectionValidator.throwExceptionIfSetIsEmpty(products, NO_PRODUCTS_FOUND);
  }
}
