package com.sirnoob.authservice.dto;

public class AccountView {

  private Long id;
  private String name;
  private String email;
  private String role;

  public AccountView() {}

  public AccountView(Long id, String name, String email, String role) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.role = role;
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getEmail() {
    return this.email;
  }

  public String getRole() {
    return this.role;
  }
}
