package com.sirnoob.authservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;

@Builder
@Table(value = "tokens")
public class Token {

  @Id
  private Long id;
  private String refreshToken;
  private String accessToken;

  public Token(){}

  public Token(Long id, String refreshToken, String accessToken) {
    this.id = id;
    this.refreshToken = refreshToken;
    this.accessToken = accessToken;
  }

  public Long getId() {
    return id;
  }

  public String getRefreshToken() {
    return this.refreshToken;
  }

  public String getAccessToken() {
    return this.accessToken;
  }
}
