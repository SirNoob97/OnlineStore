package com.sirnoob.authservice.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class LoginRequest {

  @NotEmpty(message = "The UserNames is Required!!")
  @Size(max = 60, message = "The Maximum Characters Allowed For The Username Is {max}!!")
  private String userName;

  @NotEmpty(message = "The Password is Required!!")
  @Size(max = 60, message = "The Maximum Characters Allowed For The Password Is {max}!!")
  private String password;
}
