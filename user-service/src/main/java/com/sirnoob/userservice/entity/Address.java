package com.sirnoob.userservice.entity;


import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "addresses")
public class Address {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "address_id")
  private Long addressId;

  @NotEmpty(message = "The Address is required.")
  @Size(min = 10, max = 60, message = "The Address must be 10 to 60 characters long.")
  @Column(nullable = false, length = 60)
  private String address;

  @NotNull(message = "The Zip Code is required.")
  @Digits(integer = 5, fraction = 0, message = "The Zip Code must be 5 digits.")
  @Column(name = "zip_code", nullable = false)
  private Integer zipCode;

  public Address() {
  }

  public Address(Long addressId, String address, Integer zipCode) {
    this.addressId = addressId;
    this.address = address;
    this.zipCode = zipCode;
  }

  public Long getAddressId() {
    return addressId;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Integer getZipCode() {
    return zipCode;
  }

  public void setZipCode(Integer zipCode) {
    this.zipCode = zipCode;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Address address1 = (Address) o;
    return addressId.equals(address1.addressId) &&
            address.equals(address1.address) &&
            zipCode.equals(address1.zipCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(addressId, address, zipCode);
  }

  public static class Builder {
    private Long addressId;
    private String address;
    private Integer zipCode;

    private Builder(){}

    public Builder addressId(Long addressId){
      this.addressId = addressId;
      return this;
    }

    public Builder address(String address){
      this.address = address;
      return this;
    }

    public Builder zipCode(Integer zipCode){
      this.zipCode = zipCode;
      return this;
    }

    public Address build(){
      Address address = new Address();
      address.addressId = this.addressId;
      address.address = this.address;
      address.zipCode = this.zipCode;

      return address;
    }
  }
}
