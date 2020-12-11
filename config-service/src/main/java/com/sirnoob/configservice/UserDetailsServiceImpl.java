package com.sirnoob.configservice;

import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

  private final JwtDecryptor jwtDecryptor;

  public UserDetailsServiceImpl(JwtDecryptor jwtDecryptor){
    this.jwtDecryptor = jwtDecryptor;
  }

  @Override
  public UserDetails loadUserByUsername(String jwt) throws UsernameNotFoundException {
    final String REGISTRY_SERVICE = "registry-service";
    final String AUTH_SERVICE = "auth-service";
    final String PRODUCT_SERVICE = "product-service";
    final String SHOPPING_SERVICE = "shopping-service";

    String username = jwtDecryptor.getUsernameFromJwt(jwt);
    Set<String> allowedUsers = Set.of(REGISTRY_SERVICE, AUTH_SERVICE, PRODUCT_SERVICE, SHOPPING_SERVICE);

    return allowedUsers.contains(username) ? new User(username, "", Set.of(new SimpleGrantedAuthority(username)))
        : null;
  }
}
