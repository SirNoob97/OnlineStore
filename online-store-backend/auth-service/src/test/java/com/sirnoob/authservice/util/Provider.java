package com.sirnoob.authservice.util;

import java.time.Instant;
import java.util.UUID;

import com.sirnoob.authservice.domain.Token;
import com.sirnoob.authservice.domain.Role;
import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.dto.AccountPayload;
import com.sirnoob.authservice.dto.LoginRequest;
import com.sirnoob.authservice.dto.SignUpRequest;

import org.springframework.http.MediaType;

public class Provider{

  public static final String JWTTEST = "eyJ0eXAiOiJKV1QiLCJhY3QiOiJ0ZXN0IiwiYWxnIjoiUlMyNTYifQ.eyJyb2xlIjpbIkFETUlOIl0sInN1YiI6InRlc3QiLCJpc3MiOiJ0ZXN0IGlzc3VlciIsImlhdCI6MTYxMzA2ODUzNn0.oNWydXvmbeKuTN_Pb7lA08JD6o8wnCY2hrtUMifDjxRxp70OONcGUL9cUexymkAt_PCVEGjNsFu-WrRqptzmfpHJiWWwz7nz5LOqOfacRF23swtgNdrUkVvMuZI5hmxAz1RRPEZb_LUpOB5DlN7am3znqd3Gs6PxSFmE5jQB6mxZ1hdpw3pfWX5rVqZBpHqMoRKXChoAckT3Lt6q8asDZi5wlMjw48z58BxY3Rt9PRZQCJE3EhDure7t0Vk_MjDeleLnzX2D03G5LrBYgmPLw2zFUBBGA5hbTiMvipx9tECYDC4yPikNSVawJr20Mn6se91pQANX5O5_avwpWYk2kw";

  public static final MediaType JSON = MediaType.APPLICATION_JSON;

  public static final Long EXPIRATION_TIME = 1200000L;

  public static final String PASSWORD = "PASSWORD";

  public static final String DEFAULT = "DEFAULT";

  public static final String NEW_PASSWORD = "NEW PASSWORD";

  public static final String TEST = "test";

  public static final String TEST_EMAIL = "test@email.com";

  public static final String EMPLOYEE = "EMPLOYEE";

  public static final String ADMIN = "ADMIN";

  public static final String NEW_USER = "new user";

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

  public static User generateUserStaticValuesForIT(){
    return User.builder().userId(1L)
                          .userName(TEST)
                          .password(TEST)
                          .email(TEST_EMAIL)
                          .role(Role.ADMIN)
                          .build();
  }

  public static AccountPayload generateAccountPayloadStaticValues(){
    return AccountPayload.builder().userName(TEST)
                          .password(TEST)
                          .email(TEST_EMAIL)
                          .role(EMPLOYEE)
                          .build();
  }

  public static Token generateTokenEntity(){
    return Token.builder()
                        .accessToken(JWTTEST)
                        .refreshToken(JWTTEST)
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
    return new LoginRequest(TEST, TEST);
  }


  public static Instant getJwtExpirationTime(){
    return Instant.now().plusMillis(EXPIRATION_TIME);
  }

  private static String getRandomString(){
    return UUID.randomUUID().toString();
  }
}
