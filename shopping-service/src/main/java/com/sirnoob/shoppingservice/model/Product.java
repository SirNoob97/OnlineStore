package com.sirnoob.shoppingservice.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Builder;

@Builder
@Embeddable
public class Product {

  @Column(name = "product_name", unique = true, nullable = false, length = 130)
  private String productName;

  @Column(name = "product_price", nullable = false)
  private Double productPrice;

  public Product() {
  }

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
