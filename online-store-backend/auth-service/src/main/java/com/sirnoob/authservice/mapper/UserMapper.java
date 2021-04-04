package com.sirnoob.authservice.mapper;

import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AccountPayload;
import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.SignUpRequest;

public interface UserMapper {

  public User accountPayloadToUser(AccountPayload accountPayload);

  public User signUpRequestToUser(SignUpRequest signUpRequest);

  public AccountView userToAccountView(User user);
}
