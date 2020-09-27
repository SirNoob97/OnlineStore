package com.sirnoob.productservice.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ProductView {

  private Long productBarCode;
  private String productName;
  private String productDescription;
  private Double productPrice;
  private String mainCategoryName;
  private Set<String> subcategories;
}