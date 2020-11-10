package com.sirnoob.shoppingservice.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class Customer {

  @Column(name = "user_name", unique = true, nullable = false, length = 60)
  private String userName;

  @Column(name = "email", unique = true, nullable = false, length = 60)
  private String email;
}
