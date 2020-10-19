package com.sirnoob.authservice.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;

@Builder
@Table(value = "refresh_tokens")
public class RefreshToken {

  @Id
  private Long id;
  private String token;

  public RefreshToken(){}

  public RefreshToken(Long id, String token) {
    this.id = id;
    this.token = token;
  }

  public Long getId() {
    return id;
  }

  public String getToken() {
    return token;
  }
}
