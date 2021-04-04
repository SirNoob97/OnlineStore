package com.sirnoob.productservice.mapper;

import java.util.Set;

import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.dto.ProductView;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;

public interface ProductMapper {

  public Product productRequestToProduct(ProductRequest productRequest, MainCategory mainCategory,
      Set<SubCategory> subCategories);

  public ProductResponse productToProductResponse(Product product);

  public ProductView productToProductView(Product product);

}
