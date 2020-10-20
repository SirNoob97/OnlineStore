package com.sirnoob.authservice.dto;

public class PasswordUpdateDto {

  private Long userId;
  private String password;

  public PasswordUpdateDto(Long userId, String password){
    this.userId = userId;
    this.password = password;
  }

  public Long getUserId(){
    return this.userId;
  }

  public String getPassword(){
    return this.password;
  }
}
