package com.sirnoob.productservice.service;

import java.util.List;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;

public interface IProductService {

  public ProductResponse createProduct(ProductRequest productRequest);

  public ProductInvoiceResponse getProductById(Long productId);

  public ProductResponse getProductByBarCode(Long productBarCode);

  public ProductResponse updateProduct(ProductRequest productRequest);

  public ProductResponse updateProductStock(Long productBarCode, Integer quantity);

  public List<ProductResponse> getAllProducts();

  public List<ProductResponse> getProductsByMainCategoryId(Long mainCategoryId);

  public List<ProductResponse> getProductsBySubCategoryId(Long subCategoryId);
}
