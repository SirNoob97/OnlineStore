package com.sirnoob.userservice.entity;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

//  TODO: mover las etiquetas para validaciones a los modelos dto

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

  public User() {
  }

  public User(Long userId, String userDni, String userName, String firstName, String lastName, String email, Address address) {
    this.userId = userId;
    this.userDni = userDni;
    this.userName = userName;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.address = address;
  }

  public Long getUserId() {
    return userId;
  }

  public String getUserDni() {
    return userDni;
  }

  public void setUserDni(String userDni) {
    this.userDni = userDni;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return userId.equals(user.userId) &&
            firstName.equals(user.firstName) &&
            lastName.equals(user.lastName) &&
            email.equals(user.email) &&
            status.equals(user.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, firstName, lastName, email, status);
  }

  @Override
  public String toString() {
    return "User{" +
            "userId=" + userId +
            ", userDni='" + userDni + '\'' +
            ", userName='" + userName + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", status='" + status + '\'' +
            ", address=" + address +
            '}';
  }

  public static class Builder {

    private Long userId;
    private String userDni;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String status;
    private Address address;

    private Builder() {
    }

    public Builder userId(Long userId) {
      this.userId = userId;
      return this;
    }

    public Builder userDni(String userDni) {
      this.userDni = userDni;
      return this;
    }

    public Builder userName(String userName) {
      this.userName = userName;
      return this;
    }

    public Builder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Builder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public Builder status(String status) {
      this.status = status;
      return this;
    }

    public Builder address(Address address) {
      this.address = address;
      return this;
    }

    public User build() {
      User user = new User();
      user.userId = this.userId;
      user.userDni = this.userDni;
      user.userName = this.userName;
      user.firstName = this.firstName;
      user.lastName = this.lastName;
      user.email = this.email;
      user.status = this.status;
      user.address = this.address;

      return user;
    }
  }
}
