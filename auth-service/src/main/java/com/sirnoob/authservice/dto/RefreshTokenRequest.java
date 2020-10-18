package com.sirnoob.authservice.dto;

public class RefreshTokenRequest {

  private String token;
  private String userName;

  public RefreshTokenRequest(){}

  public RefreshTokenRequest(String token, String userName) {
    this.token = token;
    this.userName = userName;
  }

  public String getToken() {
    return token;
  }

  public String getUserName() {
    return userName;
  }
}
