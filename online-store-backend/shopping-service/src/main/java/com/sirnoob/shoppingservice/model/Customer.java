package com.sirnoob.shoppingservice.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Customer {

  @Column(name = "user_name", length = 60, nullable = false)
  private String userName;

  @Column(name = "user_email", length = 60, nullable = false)
  private String userEmail;

  public Customer (){}

  public Customer (String userName, String userEmail){
    this.userName = userName;
    this.userEmail = userEmail;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String email) {
    this.userEmail = email;
  }
}
