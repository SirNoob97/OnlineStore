package com.sirnoob.authservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table(value = "users")
public class User {

  @Id
  @Column(value = "id")
  private Long userId;

  @Column(value = "user_name")
  private String userName;

  @Column(value = "user_password")
  private String userPassword;
}
