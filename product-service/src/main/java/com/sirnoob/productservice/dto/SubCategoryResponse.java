package com.sirnoob.productservice.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubCategoryResponse {

  private Long subCategoryId;
  private String subCategoryName;
  private String mainCategory;
  private Set<String> products;
}
