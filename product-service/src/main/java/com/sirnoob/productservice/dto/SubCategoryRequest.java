package com.sirnoob.productservice.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;

@Getter
public class SubCategoryRequest {

  @NotEmpty(message = "The Sub Category Name is required")
  @Size(max = 50, message = "The Sub Category must be a maximum of {max} characters")
  private String subCategoryName;

  @NotNull(message = "The Sub Category must belong to a Main Category")
  private String mainCategoryName;
}
