package com.sirnoob.configservice;

import java.io.IOException;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtDecryptor jwtDecryptor;

  Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

  public JwtAuthFilter(JwtDecryptor jwtDecryptor) {
    this.jwtDecryptor = jwtDecryptor;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String jwt = getJwtFromRequest(request);

    if (StringUtils.hasText(jwt)) {
      UserDetails userDetails = checkClient(jwt);

      var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }

  private String getJwtFromRequest(HttpServletRequest request) {
    final String AUTHORIZATION = "Authorization";
    final String BEARER = "Bearer ";

    String bearerToken = request.getHeader(AUTHORIZATION);

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
      return bearerToken.substring(BEARER.length());
    }

    log.error("invalid Token!!");
    return "";
  }

  public UserDetails checkClient(String jwt) throws UsernameNotFoundException {
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
