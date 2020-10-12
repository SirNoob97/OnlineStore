package com.sirnoob.authservice.dto;

import com.sirnoob.authservice.domain.Role;

public class AccountView {

  private String name;
  private String email;
  private Role role;

  public AccountView(String name, String email, Role role) {
    this.name = name;
    this.email = email;
    this.role = role;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public Role getRole() {
    return role;
  }
}
