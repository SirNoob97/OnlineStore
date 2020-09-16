package com.sirnoob.productservice.service;

import java.util.List;
import java.util.Set;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.dto.ProductView;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;

public interface IProductService {

  public Product getProductById(Long productId);

  public List<Product> getProductByMainCategory(MainCategory mainCategory);

  public ProductResponse createProduct(ProductRequest productRequest);

  public ProductResponse updateProduct(Long productId, ProductRequest productRequest);

  public ProductResponse getProductResponseByBarCodeOrProductName(Long productBarCode, String productName);

  public ProductInvoiceResponse getProductForInvoiceResponse(Long productBarCode, String productName);

  public ProductView findProductViewByName(String productName);

  public Set<ProductListView> getProductListViewByName(String productName, int page);

  public Set<ProductListView> getPageOfProductListView(int page);

  public Set<ProductListView> getProductListViewByMainCategory(String mainCategory, int page);

  public Set<ProductListView> getProductListViewBySubCategory(String[] subCategoryName);

  public void updateProductStock(Long productBarCode, Integer quantity);

  public void deleteProductById(Long productId);
}
