package com.sirnoob.shoppingservice.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Embeddable
public class Product {

  @Column(name = "product_name", unique = true, nullable = false, length = 130)
  private String productName;

  @Column(name = "product_price", nullable = false)
  private Double productPrice;
}
