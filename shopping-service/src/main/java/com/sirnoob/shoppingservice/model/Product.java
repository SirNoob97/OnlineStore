package com.sirnoob.shoppingservice.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Builder;

@Embeddable
@Builder
public class Product {

  @Column(name = "product_name", length = 130, nullable = false)
  private String productName;

  @Column(name = "product_price", nullable = false)
  private Double productPrice;

  public Product() {}

  public Product(String productName, Double productPrice) {
    this.productName = productName;
    this.productPrice = productPrice;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public Double getProductPrice() {
    return productPrice;
  }

  public void setProductPrice(Double productPrice) {
    this.productPrice = productPrice;
  }
}
