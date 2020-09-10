package com.sirnoob.productservice.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ProductView {
  
  private String productName;
  private String productDescription;
  private Double productPrice;
  private String mainCategoryName;
  private Set<String> subCategoriesName;
}
