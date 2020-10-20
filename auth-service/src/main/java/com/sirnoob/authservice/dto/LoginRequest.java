package com.sirnoob.authservice.dto;

public class LoginRequest {

  private String userName;
  private String password;

  public LoginRequest(String userName, String password) {
    this.userName = userName;
    this.password = password;
  }

  public String getUserName() {
    return userName;
  }

  public String getPassword() {
    return password;
  }

}
