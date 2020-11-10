package com.sirnoob.shoppingservice.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class Product {
  
  @Column(name = "product_name", unique = true, nullable = false, length = 130)
  private String productName;

  @Column(name = "product_price", unique = true, nullable = false)
  private Double productPrice;
}
