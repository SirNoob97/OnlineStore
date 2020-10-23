package com.sirnoob.authservice.domain;

import java.util.Collection;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(value = "users")
public class User implements UserDetails{

  private static final long serialVersionUID = 1L;

  @Id
  @Column("id")
  private Long userId;

  @NotEmpty(message = "The UserName is Required!!")
  @Size(max = 60, message = "The Maximum Characters Allowed For The Username Is {max}!!")
  private String userName;

  @NotEmpty(message = "The Password is Required!!")
  @Size(max = 60, message = "The Maximum Characters Allowed For The Password Is {max}!!")
  private String password;

  @NotEmpty(message = "The Email is Required!!")
  @Size(max = 60, message = "The Maximum Characters Allowed For The Email Is {max}!!")
  @Email(regexp = "[^@ '\\t\\r\\n]+@[^@ '\\t\\r\\n]+\\.[^@ '\\t\\r\\n]+", message = "Invalid Email Format!!")
  private String email;

  @NotNull(message = "The User Must Have A Role!!")
  private Role role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  @Override
  public String getUsername() {
    return this.userName;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
