package com.sirnoob.authservice.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountPayload {

  private Long userId;

  @NotEmpty(message = "The UserName is Required!!")
  @Size(max = 60, message = "The Maximum Characters Allowed For The Username Is {max}!!")
  private String userName;

  @NotEmpty(message = "The Password is Required!!")
  @Size(max = 60, message = "The Maximum Characters Allowed For The Password Is {max}!!")
  private String password;

  @NotEmpty(message = "The Email is Required!!")
  @Size(max = 60, message = "The Maximum Characters Allowed For The Email Is {max}!!")
  @Email(regexp = "[^@ '\\t\\r\\n]+@[^@ '\\t\\r\\n]+\\.[^@ '\\t\\r\\n]+", message = "Invalid Email Format!!")
  private String email;

  @NotNull(message = "The User Must Have A Role!!")
  private String role;
}
