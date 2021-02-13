package com.sirnoob.authservice.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;

@Builder
@Table(value = "tokens")
public class Token {

  @Id
  private Long id;

  @NotEmpty(message = "Refresh Token is Required!!")
  @Pattern(regexp = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]+$", message = "Malformed JWT!!")
  private String refreshToken;

  @NotEmpty(message = "Access Token is Required!!")
  @Pattern(regexp = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]+$", message = "Malformed JWT!!")
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
