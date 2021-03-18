package com.sirnoob.authservice.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public class PasswordUpdateDto {

  @NotNull(message = "The User Id is Required!!")
  @Positive(message = "The User Id Must Be Positive!!")
  private Long userId;

  @NotEmpty(message = "The Password is Required!!")
  @Size(max = 60, message = "The Maximum Characters Allowed For The Password Is {max}!!")
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
