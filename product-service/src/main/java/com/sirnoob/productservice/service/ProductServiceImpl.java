package com.sirnoob.productservice.service;

import java.util.List;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.repository.IMainCategoryRepository;
import com.sirnoob.productservice.repository.IProductRepository;
import com.sirnoob.productservice.repository.ISubCategoryRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements IProductService {

  private final IMainCategoryRepository iMainCategoryRepository;
  private final ISubCategoryRepository iSubCategoryRepository;
  private final IProductRepository iProductRepository;


  @Override
  public ProductResponse createProduct(ProductRequest productRequest) {
    
    MainCategory mainCategory = iMainCategoryRepository.findById(productRequest.getMainCategoryId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    return null;
  }

  @Override
  public ProductInvoiceResponse getProductById(Long productId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ProductResponse getProductByBarCode(Long productBarCode) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ProductResponse updateProduct(ProductRequest productRequest) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ProductResponse updateProductStock(Long productBarCode, Integer quantity) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<ProductResponse> getAllProducts() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<ProductResponse> getProductsByMainCategoryId(Long mainCategoryId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<ProductResponse> getProductsBySubCategoryId(Long subCategoryId) {
    // TODO Auto-generated method stub
    return null;
  }

}
