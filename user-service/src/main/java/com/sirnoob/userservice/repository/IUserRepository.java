package com.sirnoob.userservice.repository;

import com.sirnoob.userservice.entity.Address;
import com.sirnoob.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
  public Optional<User> findByUserDni(String dni);
  public Optional<User> findByEmail(String email);
  public List<User> findByAddress(Address address);
}
