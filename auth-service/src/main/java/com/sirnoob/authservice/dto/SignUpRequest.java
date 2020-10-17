package com.sirnoob.authservice.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SignUpRequest{

  @NotEmpty(message = "The UserNames is Required!!")
  @Size(max = 60, message = "The Maximum Characters Allowed For The Username Is {max}!!")
  private String userName;

  @NotEmpty(message = "The Password is Required!!")
  @Size(max = 60, message = "The Maximum Characters Allowed For The Password Is {max}!!")
  private String password;

  @NotEmpty(message = "The Email is Required!!")
  @Size(max = 60, message = "The Maximum Characters Allowed For The Email Is {max}!!")
  private String email;
}
