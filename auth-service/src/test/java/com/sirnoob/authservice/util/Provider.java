package com.sirnoob.authservice.util;

import java.util.UUID;

import com.sirnoob.authservice.domain.RefreshToken;
import com.sirnoob.authservice.domain.Role;
import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.RefreshTokenRequest;
import com.sirnoob.authservice.dto.SignUpRequest;

public class Provider{

  public static final String PASSWORD = "PASSWORD";

  public static final String DEFAULT = "DEFAULT";

  public static final String NEW_PASSWORD = "NEW PASSWORD";

  public static final String TEST = "TEST";

  public static final String TEST_EMAIL = "TEST@TEST.com";

  public static final String TOKEN = "TOKEN";

  public static User generateUserRandomValues(Role role){
    return User.builder().userName(getRandomString())
                          .password(getRandomString())
                          .email(getRandomString())
                          .role(role)
                          .build();
  }

  public static User generateUserForSignUpTest(){
    return User.builder().userName(TEST)
                          .password(PASSWORD)
                          .email(getRandomString())
                          .build();
  }

  public static User generateUserStaticValues(){
    return User.builder().userName(TEST)
                          .password(TEST)
                          .email(TEST_EMAIL)
                          .role(Role.EMPLOYEE)
                          .build();
  }

  public static RefreshToken generateRefreshToken(){
    return RefreshToken.builder()
                        .token(getRandomString())
                        .build();
  }

  public static RefreshToken getDefaultRefreshToken(){
    return RefreshToken.builder()
                        .id(0L)
                        .token(DEFAULT)
                        .build();
  }

  public static SignUpRequest generateSignUpRequest(){
    return SignUpRequest.builder()
                        .userName(TEST)
                        .password(PASSWORD)
                        .email(TEST_EMAIL)
                        .build();
  }

  public static LoginRequest generateLoginRequest() {
    return new LoginRequest(TEST, PASSWORD);
  }

  public static RefreshTokenRequest generateRefreshTokenRequest(){
    return new RefreshTokenRequest(TOKEN, TEST);
  }

  private static String getRandomString(){
    return UUID.randomUUID().toString();
  }
}
