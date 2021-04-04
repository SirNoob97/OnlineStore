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

public interface ProductService {

  public Product getById(Long productId);

  public List<Product> getByMainCategory(Long mainCategoryId);

  public ProductResponse create(ProductRequest productRequest, MainCategory mainCategory);

  public ProductResponse update(Long productId, ProductRequest productRequest, MainCategory mainCategory);

  public ProductResponse getProductResponseByBarCodeOrProductName(Long productBarCode, String productName);

  public ProductInvoiceResponse getForInvoiceResponse(Long productBarCode, String productName);

  public ProductView getProductViewByName(String productName);

  public Page<ProductListView> getListViewByName(String productName, Pageable pageable);

  public Page<ProductListView> getListView(Pageable pageable);

  public Page<ProductListView> getListViewByMainCategory(Long mainCategoryId, Pageable pageable);

  public Set<ProductListView> getListViewBySubCategory(String[] subCategoryName);

  public void updateStock(Long productBarCode, Integer quantity);

  public void deleteById(Long productId);
}
