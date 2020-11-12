package com.sirnoob.shoppingservice.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Builder;

@Builder
@Embeddable
public class Customer {

  @Column(name = "user_name", unique = true, nullable = false, length = 60)
  private String userName;

  @Column(name = "email", unique = true, nullable = false, length = 60)
  private String email;

  public Customer (){}

  public Customer (String userName, String email){
    this.userName = userName;
    this.email = email;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
