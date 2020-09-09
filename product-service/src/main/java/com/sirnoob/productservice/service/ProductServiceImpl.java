package com.sirnoob.productservice.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;
import com.sirnoob.productservice.mapper.IProductMapper;
import com.sirnoob.productservice.repository.IMainCategoryRepository;
import com.sirnoob.productservice.repository.IProductRepository;
import com.sirnoob.productservice.repository.ISubCategoryRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements IProductService {

  private final IMainCategoryRepository iMainCategoryRepository;
  private final ISubCategoryRepository iSubCategoryRepository;
  private final IProductRepository iProductRepository;
  private final IProductMapper iProductMapper;

  @Override
  public ProductResponse createProduct(ProductRequest productRequest) {

    MainCategory mainCategory = getMainCategoryById(productRequest.getMainCategoryId());

    Set<SubCategory> subCategories = getSubcategoriesById(productRequest.getSubCategoriesId());
    
    Product product = iProductMapper.mapProductRequestToProduct(productRequest, mainCategory, subCategories);

    product.setProductStatus("CREATED");
    product.setCreateAt(LocalDate.now());

    return iProductMapper.mapProductToProductResponse(iProductRepository.save(product));
  }

  @Override
  public ProductInvoiceResponse getProductForInvoice(Long productBarCode) {
    return iProductMapper.mapProductToProductInvoiceResponse(getProductByBarCode(productBarCode));
  }

  @Override
  public Product getProductByBarCode(Long productBarCode) {
    return iProductRepository.findByProductBarCode(productBarCode).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Not Found with Bar Code " + productBarCode));
  }

  @Override
  public ProductResponse updateProduct(ProductRequest productRequest) {


    Product product = iProductRepository.findById(productRequest.getProductId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Not Found with Id " + productRequest.getProductId()));

    product.setProductBarCode(productRequest.getProductBarCode());
    product.setProductName(productRequest.getProductName());
    product.setProductDescription(productRequest.getProductDescription());
    product.setProductStock(productRequest.getProductStock());
    product.setProductPrice(productRequest.getProductPrice());
    product.setProductStatus("UPDATED");
    product.setMainCategory(getMainCategoryById(productRequest.getMainCategoryId()));
    product.setSubCategories(getSubcategoriesById(productRequest.getSubCategoriesId()));

    return iProductMapper.mapProductToProductResponse(iProductRepository.save(product));
  }

  @Override
  public ProductResponse updateProductStock(Long productBarCode, Integer quantity) {

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



  private MainCategory getMainCategoryById(Long mainCategoryId) {

    return iMainCategoryRepository.findById(mainCategoryId).orElseThrow(
        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Main Category Not Found with Id " + mainCategoryId));
  }

  private Set<SubCategory> getSubcategoriesById(Long[] subCategoriesId) {

    Set<SubCategory> subCategories = new HashSet<>();

    for (Long sc : subCategoriesId) {
      subCategories.add(iSubCategoryRepository.findById(sc).orElseThrow(
          () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sub Category Not Found with Id " + sc)));
    }

    return subCategories;
  }

}
