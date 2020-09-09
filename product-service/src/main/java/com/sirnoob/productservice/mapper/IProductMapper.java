package com.sirnoob.productservice.mapper;

import java.util.Set;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;

public interface IProductMapper {

  public Product mapProductRequestToProduct(ProductRequest productRequest, MainCategory mainCategory,
      Set<SubCategory> subCategories);

  public ProductResponse mapProductToProductResponse(Product product);

  public ProductInvoiceResponse mapProductToProductInvoiceResponse(Product product);
}
