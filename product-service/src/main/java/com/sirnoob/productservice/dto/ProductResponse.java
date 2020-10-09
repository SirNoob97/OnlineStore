package com.sirnoob.productservice.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse{

  private Long productId;
  private Long productBarCode;
  private String productName;
  private String productDescription;
  private Integer productStock;
  private Double productPrice;
  private LocalDate createDate;
  private LocalDateTime lastModifiedDate;
  private String productStatus;
  private String mainCategoryName;
  private Set<String> subCategories;
}
