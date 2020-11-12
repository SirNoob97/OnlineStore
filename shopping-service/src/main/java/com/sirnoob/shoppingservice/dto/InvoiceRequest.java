package com.sirnoob.shoppingservice.dto;

import java.util.Set;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.sirnoob.shoppingservice.model.Customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InvoiceRequest {

  private Long invoiceId;

  @PositiveOrZero(message = "The Invoice Number must be positive!!")
  @Min(value = 100000L,message = "The Bar Code must be 6 digits!!")
  @Digits(integer = 6, fraction = 0, message = "The Bar Code must be {integer} digits!!")
  @NotNull(message = "The Invoice Number is Mandatory!!")
  private Long invoiceNumber;

  @NotNull(message = "The Invoice Must Have Products!!")
  private Set<ProductDto> products;

  @NotNull(message = "The Customer Is Required To Generate The Invoice!!")
  private Customer customer;
}
