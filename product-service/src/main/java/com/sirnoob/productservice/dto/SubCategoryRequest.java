package com.sirnoob.productservice.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SubCategoryRequest {

  @NotEmpty(message = "The Sub Category Name is required")
  @Size(max = 50, message = "The Sub Category must be a maximum of {max} characters")
  private String subCategoryName;

  @NotNull(message = "The Sub Category must belong to a Main Category")
  private String mainCategoryName;

  public SubCategoryRequest(){}

  public SubCategoryRequest(String subCategoryName, String mainCategoryName) {
    this.subCategoryName = subCategoryName;
    this.mainCategoryName = mainCategoryName;
  }

  public String getSubCategoryName() {
    return subCategoryName;
  }

  public void setSubCategoryName(String subCategoryName) {
    this.subCategoryName = subCategoryName;
  }

  public String getMainCategoryName() {
    return mainCategoryName;
  }

  public void setMainCategoryName(String mainCategoryName) {
    this.mainCategoryName = mainCategoryName;
  }

}
