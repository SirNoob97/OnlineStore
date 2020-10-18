package com.sirnoob.authservice.dto;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthResponse {

  private String userName;
  private String authToken;
  private String refreshToken;
  private Instant expiresAt;
}
