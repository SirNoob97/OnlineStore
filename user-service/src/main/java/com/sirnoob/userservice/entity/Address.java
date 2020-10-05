package com.sirnoob.userservice.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "address_id")
  private Long addressId;

  @NotEmpty(message = "The Street is required.")
  @Size(min = 10, max = 60, message = "The Street must be 10 to 60 characters long.")
  @Column(nullable = false, length = 60)
  private String street;


  @NotNull(message = "The Zip Code is required.")
  @Digits(integer = 5, fraction = 0, message = "The Zip Code must be 5 digits.")
  @Column(name = "zip_code", nullable = false)
  private Integer zipCode;

  @Column(nullable = false)
  private Integer number;

  @Column(nullable = false)
  private String country;

  @Column(nullable = false)
  private String city;
}
