package com.sirnoob.authservice.domain;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;

@Builder
@Table(value = "refresh_tokens")
public class RefreshToken {

  @Id
  private Long id;
  private String token;
  private LocalDate createdDate;

  public RefreshToken(){}

  public RefreshToken(Long id, String token, LocalDate createdDate) {
    this.id = id;
    this.token = token;
    this.createdDate = createdDate;
  }

  public Long getId() {
    return id;
  }

  public String getToken() {
    return token;
  }

  public LocalDate getCreatedDate() {
    return createdDate;
  }
}
