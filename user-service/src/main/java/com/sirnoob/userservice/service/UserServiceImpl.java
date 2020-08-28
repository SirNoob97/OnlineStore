package com.sirnoob.userservice.service;

import com.sirnoob.userservice.entity.Address;
import com.sirnoob.userservice.entity.User;
import com.sirnoob.userservice.exceptions.AddressNotFoundException;
import com.sirnoob.userservice.exceptions.UserAlreadyExistsException;
import com.sirnoob.userservice.exceptions.UserNotFoundException;
import com.sirnoob.userservice.repository.IAddresRepository;
import com.sirnoob.userservice.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

  private static final String CREATED = "CREATED";
  private static final String DELETED = "DELETED";

  private IUserRepository iUserRepository;

  private UserServiceImpl(){}

  @Autowired
  public UserServiceImpl(IUserRepository iUserRepository) {
    this.iUserRepository = iUserRepository;
  }

  @Override
  public User createUser(User user) {
    if(user.getUserName() == null)
      user.setUserName(user.getFirstName());

    user.setStatus(CREATED);

    return iUserRepository.save(user);
  }

  @Override
  public User updateUser(User user) {
//    User userDB = iUserRepository.findByUserDni(user.getUserDni()).orElse(null);
//    if(userDB != null && userDB.getUserId() != user.getUserId())
//      throw new UserAlreadyExistsException("There is already a user with that DNI.");
//
//    userDB = iUserRepository.findByEmail(user.getEmail()).orElse(null);
//    if(userDB != null && userDB.getUserId() != user.getUserId())
//      throw new UserAlreadyExistsException("There is already a user with that Email.");

//    return iUserRepository.save(user);
    User userDB = getUser(user.getUserId());

    userDB.setUserDni(user.getUserDni());
    userDB.setUserName(user.getUserName());
    userDB.setFirstName(userDB.getFirstName());
    userDB.setLastName(user.getLastName());
    userDB.setEmail(user.getEmail());
    userDB.setAddress(user.getAddress());

    return iUserRepository.save(userDB);
  }

  @Override
  public User deleteUser(Long userId){
    User user = getUser(userId);
    user.setStatus(DELETED);

    iUserRepository.deleteById(userId);

    return user;
  }

  @Override
  public User getUser(Long id) {
    return iUserRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with ID " + id));
  }

  @Override
  public List<User> findUsersByAddress(Long addressId) {
    return iUserRepository.findByAddressAddressId(addressId);
  }

  @Override
  public List<User> findAllUsers() {
    return iUserRepository.findAll();
  }
}