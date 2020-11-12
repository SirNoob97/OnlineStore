package com.sirnoob.shoppingservice.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

public class ProductDto {
 
  @PositiveOrZero(message = "The Bar Code must be positive!!")
  @Min(value = 1000000000000L,message = "The Bar Code must be 13 digits!!")
  @Digits(integer = 13, fraction = 0, message = "The Bar Code must be {integer} digits!!")
  @NotNull(message = "The Product Barcode is Mandatory!!")
  private Long productBarCode;

  @NotEmpty(message = "The Name is Mandatory!!")
  @Size(max = 130, message = "The Name must be a maximum of {max} characters!!")
  private String productName;

  private Integer quantity;

  public ProductDto(){}

  public ProductDto(Long productBarCode, String productName, Integer quantity) {
    this.productBarCode = productBarCode;
    this.productName = productName;
    this.quantity = quantity;
  }

  public Long getProductBarCode() {
    return productBarCode;
  }

  public void setProductBarCode(Long productBarCode) {
    this.productBarCode = productBarCode;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public Integer getQuantity(){
    return this.quantity;
  }

  public void setQuantity(Integer quantity){
    this.quantity = quantity;
  }
}
