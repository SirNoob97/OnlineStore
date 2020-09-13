package com.sirnoob.productservice.service;

import java.util.List;
import java.util.Set;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductListView;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.dto.ProductView;

public interface IProductService {

  public ProductResponse createProduct(ProductRequest productRequest);

  public ProductResponse updateProduct(Long productId, ProductRequest productRequest);

  public ProductResponse getProductResponseByBarCodeOrProductName(Long productBarCode, String productName);

  public ProductInvoiceResponse getProductForInvoiceResponse(Long productBarCode, String productName);

  public ProductView findProductViewByName(String productName);

  public List<ProductListView> getProductListViewByName(String productName, int page);

  public List<ProductListView> getPageOfProductListView(int page);

  public List<ProductListView> getProductListViewByMainCategory(String mainCategoryName, int page);

  public Set<ProductListView> getProductListViewBySubCategory(String[] subCategoryName);

  public int updateProductStock(Long productBarCode, Integer quantity);

  public void deleteProductById(Long productId);
}
