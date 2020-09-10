package com.sirnoob.productservice.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.sirnoob.productservice.dto.ProductInvoiceResponse;
import com.sirnoob.productservice.dto.ProductRequest;
import com.sirnoob.productservice.dto.ProductResponse;
import com.sirnoob.productservice.entity.MainCategory;
import com.sirnoob.productservice.entity.Product;
import com.sirnoob.productservice.entity.SubCategory;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
public class ProductMapperImpl implements IProductMapper {


	@Override
	public Product mapProductRequestToProduct(ProductRequest productRequest, MainCategory mainCategory, Set<SubCategory> subCategories) {

    return Product.builder()
      .productBarCode(productRequest.getProductBarCode()) 
      .productName(productRequest.getProductName())
      .productDescription(productRequest.getProductDescription())
      .productStock(productRequest.getProductStock())
      .productPrice(productRequest.getProductPrice())
      .mainCategory(mainCategory)
      .subCategories(subCategories)
      .build();

	}

	@Override
	public ProductResponse mapProductToProductResponse(Product product) {
		
    return ProductResponse.builder()
      .productId(product.getProductId())
      .productBarCode(product.getProductBarCode())
      .productName(product.getProductName())
      .productDescription(product.getProductDescription())
      .productStock(product.getProductStock())
      .productPrice(product.getProductPrice())
      .createAt(product.getCreateAt())
      .productStatus(product.getProductStatus())
      .mainCategoryName(product.getMainCategory().getMainCategoryName())
      .subCategories(product.getSubCategories().stream().map(SubCategory::getSubCategoryName).collect(Collectors.toSet()))
      .build();
	}

	@Override
	public ProductInvoiceResponse mapProductToProductInvoiceResponse(Product product) {

		return ProductInvoiceResponse.builder()
      .productBarCode(product.getProductBarCode())
      .productName(product.getProductName())
      .productPrice(product.getProductPrice())
      .mainCategoryName(product.getMainCategory().getMainCategoryName())
      .build();
	}
  
}