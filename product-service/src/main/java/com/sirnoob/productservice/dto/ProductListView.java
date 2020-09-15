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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((productDescription == null) ? 0 : productDescription.hashCode());
    result = prime * result + ((productName == null) ? 0 : productName.hashCode());
    result = prime * result + ((productPrice == null) ? 0 : productPrice.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ProductListView other = (ProductListView) obj;
    if (productDescription == null) {
      if (other.productDescription != null)
        return false;
    } else if (!productDescription.equals(other.productDescription))
      return false;
    if (productName == null) {
      if (other.productName != null)
        return false;
    } else if (!productName.equals(other.productName))
      return false;
    if (productPrice == null) {
      if (other.productPrice != null)
        return false;
    } else if (!productPrice.equals(other.productPrice))
      return false;
    return true;
  }

}
