package com.sirnoob.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductInvoiceResponse {

  private Long productBarCode;
  private String productName;
  private String productDescription;
  private Double productPrice;
  private String mainCategoryName;
}
