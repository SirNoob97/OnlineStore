package com.sirnoob.authservice.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

  private String userName;
  private String authToken;
  private String refreshToken;
  private Instant expiresAt;
}
