package com.sirnoob.authservice.mapper;

import com.sirnoob.authservice.domain.Role;
import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AccountPayload;
import com.sirnoob.authservice.dto.AccountView;
import com.sirnoob.authservice.dto.SignUpRequest;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UserMapperImpl implements IUserMapper {

  private final PasswordEncoder passwordEncoder;

  @Override
  public User mapAccountPayloadToUser(AccountPayload accountPayload) {
    return User.builder().userId(accountPayload.getUserId())
                          .userName(accountPayload.getUserName())
                          .password(passwordEncoder.encode(accountPayload.getPassword()))
                          .email(accountPayload.getEmail())
                          .role(Role.valueOf(accountPayload.getRole())).build();
  }

  @Override
  public User mapSignUpRequestToUser(SignUpRequest signUpRequest) {
    return User.builder().userName(signUpRequest.getUserName())
                          .password(passwordEncoder.encode(signUpRequest.getPassword()))
                          .email(signUpRequest.getEmail())
                          .role(Role.CUSTOMER).build();
  }

  @Override
  public AccountView maptUserToAccountView(User user) {
    return user.getRole().name().equals("CUSTOMER") ?  new AccountView(user.getUserId(), user.getUsername(), user.getEmail(), "")
                                                    :  new AccountView(user.getUserId(), user.getUsername(), user.getEmail(), user.getRole().name());
  }

}
