package com.sirnoob.authservice.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class RefreshTokenRequest {

  @NotEmpty(message = "The Token is Required!!")
  @Size(max = 60, message = "The Maximum Characters Allowed For The Token Is {max}!!")
  private String token;

  @NotEmpty(message = "The UserName is Required!!")
  @Size(max = 60, message = "The Maximum Characters Allowed For The Username Is {max}!!")
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
