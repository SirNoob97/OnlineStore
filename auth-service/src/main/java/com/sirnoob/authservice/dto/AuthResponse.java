package com.sirnoob.authservice.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthResponse {

  private String userName;
  private String authToken;
}
