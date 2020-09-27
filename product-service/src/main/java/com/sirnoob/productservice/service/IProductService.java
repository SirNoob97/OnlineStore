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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {

  public Product getProductById(Long productId);

  public List<Product> getProductByMainCategory(Long mainCategoryId);

  public ProductResponse createProduct(ProductRequest productRequest, MainCategory mainCategory);

  public ProductResponse updateProduct(Long productId, ProductRequest productRequest, MainCategory mainCategory);

  public ProductResponse getProductResponseByBarCodeOrProductName(Long productBarCode, String productName);

  public ProductInvoiceResponse getProductForInvoiceResponse(Long productBarCode, String productName);

  public ProductView findProductViewByName(String productName);

  public Page<ProductListView> getProductListViewByName(String productName, Pageable pageable);

  public Page<ProductListView> getPageOfProductListView(Pageable pageable);

  public Page<ProductListView> getProductListViewByMainCategory(Long mainCategoryId, Pageable pageable);

  public Set<ProductListView> getProductListViewBySubCategory(String[] subCategoryName);

  public void updateProductStock(Long productBarCode, Integer quantity);

  public void deleteProductById(Long productId);
}
