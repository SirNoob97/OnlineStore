package com.sirnoob.authservice.mapper;

import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AccountPayload;
import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.SignUpRequest;

public interface IUserMapper {

  public User mapAccountPayloadToUser(AccountPayload accountPayload);

  public User mapSignUpRequestToUser(SignUpRequest signUpRequest);

  public AccountView maptUserToAccountView (User user);
}
