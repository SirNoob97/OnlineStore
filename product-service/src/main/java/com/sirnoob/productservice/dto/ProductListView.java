package com.sirnoob.productservice.dto;

public class ProductListView {

  private String productName;
  private String productDescription;
  private Double productPrice;

  public ProductListView(String productName, String productDescription, Double productPrice) {
    this.productName = productName;
    this.productDescription = productDescription;
    this.productPrice = productPrice;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductDescription() {
    return productDescription;
  }

  public void setProductDescription(String productDescription) {
    this.productDescription = productDescription;
  }

  public Double getProductPrice() {
    return productPrice;
  }

  public void setProductPrice(Double productPrice) {
    this.productPrice = productPrice;
  }

}
