package com.sirnoob.productservice.dto;


import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import lombok.Getter;

@Getter
public class ProductRequest{

  private Long productId;

  @PositiveOrZero(message = "The Bar Code must be positive.")
  @Min(value = 1000000000000L,message = "The Bar Code must be 13 digits.")
  @Digits(integer = 13, fraction = 0, message = "The Bar Code must be {integer} digits.")
  @NotNull(message = "The Bar Code is required.")
  private Long productBarCode;

  @NotNull(message = "The Name is required.")
  @NotEmpty(message = "The Name is required.")
  @Size(max = 30, message = "The Name must be a maximum of 30 characters.")
  private String productName;
  private String productDescription;
  
  @Positive(message = "The Product Stock must be greater than zero.")
  @NotNull(message = "The Product Stock is required.")
  @Digits(integer = 4, fraction = 0, message = "The Stock must be an integer and its maximum is 9999.")
  private Integer productStock;
  
  @DecimalMin(value = "0.0", message = "The Product Price must be positive.")
  @NotNull(message = "The Product Price is required.")
  @Digits(integer = 6, fraction = 2, message = "The Price can only have 2 decimal places and its maximum is 999999.99")
  private Double productPrice;

  @NotNull(message = "The Product must belong to a Main Category.")
  private String mainCategoryName;

  @NotNull(message = "The Product must belong to a Sub Category.")
  @Size(min = 1, message = "The Product must belong at least {min} Sub Category.")
  private String[] subCategoriesNames;
}
