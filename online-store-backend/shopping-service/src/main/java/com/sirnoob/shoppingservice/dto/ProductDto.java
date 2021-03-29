package com.sirnoob.shoppingservice.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductDto {

  @PositiveOrZero(message = "The Bar Code must be positive!!")
  @Min(value = 1000000000000L,message = "The Bar Code must be 13 digits!!")
  @Digits(integer = 13, fraction = 0, message = "The Bar Code must be {integer} digits!!")
  @NotNull(message = "The Product Barcode is Mandatory!!")
  private Long productBarCode;

  @NotEmpty(message = "The Name is Mandatory!!")
  @Size(max = 130, message = "The Name must be a maximum of {max} characters!!")
  private String productName;

  @PositiveOrZero(message = "The Quantity must be positive!!")
  @NotNull(message = "The Quantity is Mandatory!!")
  private Integer quantity;
}
