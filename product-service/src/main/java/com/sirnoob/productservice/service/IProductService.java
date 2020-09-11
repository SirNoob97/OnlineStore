package com.sirnoob.productservice.service;

import java.util.List;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.entity.Product;

public interface IProductService {

  public ProductResponse createProduct(ProductRequest productRequest);

  public ProductInvoiceResponse getProductForInvoice(Long productBarCode);

  public Product getProductByBarCode(Long productBarCode);

  public ProductListView getProductByName(String productName);

  public List<ProductListView> listByName(String productName, int page);

  public ProductResponse updateProduct(ProductRequest productRequest);

  public void updateProductStock(Long productBarCode, Integer quantity);

  public List<ProductListView> getAllProducts(int page);

  public List<ProductListView> getProductsByMainCategory(String mainCategoryName, int page);

  public List<ProductListView> getProductsBySubCategory(String[] subCategoryName);
}
