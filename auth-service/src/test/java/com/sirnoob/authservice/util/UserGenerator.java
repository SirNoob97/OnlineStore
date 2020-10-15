package com.sirnoob.authservice.util;

import java.util.UUID;

import com.sirnoob.authservice.domain.Role;
import com.sirnoob.authservice.domain.User;

public class UserGenerator{
  
  public static User generateUserRandomValues(Role role){
    return User.builder().userName(getRandomString())
                          .password(getRandomString())
                          .email(getRandomString())
                          .role(role)
                          .build();
                          
  }

  public static User generateUserStaticValues(){
    return User.builder().userName("TEST")
                          .password("TEST")
                          .email("TEST@TEST.com")
                          .role(Role.EMPLOYEE)
                          .build();
  }

  private static String getRandomString(){
    return UUID.randomUUID().toString();
  }
}
