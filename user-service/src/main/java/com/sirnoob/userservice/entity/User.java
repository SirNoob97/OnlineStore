package com.sirnoob.userservice.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId;

  @NotEmpty(message = "The DNI is required.")
  @Size(min = 9, max = 9, message = "The DNI must be nine characters.")
  @Pattern(regexp = "([A-Z])(\\d{7})([A-Z])", message = "Invalid DNI format.")
  @Column(name = "user_dni", nullable = false, length = 9, unique = true)
  private String userDni;

  @NotEmpty(message = "The USERNAME is required.")
  @Size(min = 3, max = 25, message = "The User Name must be 3 to 25 characters long.")
  @Column(name = "user_name", length = 25)
  private String userName;

  @NotEmpty(message = "The FIRST NAME is required.")
  @Size(min = 3, max = 25, message = "The First Name must be 3 to 25 characters long.")
  @Column(name = "first_name", nullable = false, length = 25)
  private String firstName;

  @Size(min = 3, max = 25, message = "The Last Name must be 3 to 25 characters long.")
  @Column(name = "last_name", length = 25)
  private String lastName;

  @NotEmpty(message = "The Email is required.")
  @Email(message = "Invalid Email Format")
  @Column(name = "user_email", unique = true, nullable = false)
  private String email;

  @Column(updatable = false)
  private String status;

  @NotNull(message = "The Region is required.")
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "address_id")
  private Address address;
}
