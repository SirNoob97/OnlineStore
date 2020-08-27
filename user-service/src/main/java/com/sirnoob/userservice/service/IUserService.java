package com.sirnoob.userservice.service;

import com.sirnoob.userservice.entity.User;

import java.util.List;

public interface IUserService {
  public User createUser(User user);
  public User updateUser(User user);
  public User deleteUser(User user);

  public User getUser(Long id);
  public List<User> findUsersByAddress(Long addressId);
  public List<User> findAllUsers();
}
