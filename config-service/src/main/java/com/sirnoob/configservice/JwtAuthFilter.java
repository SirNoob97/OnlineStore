package com.sirnoob.configservice;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final UserDetailsServiceImpl userDetailsServiceImpl;

  public JwtAuthFilter(UserDetailsServiceImpl userDetailsServiceImpl) {
    this.userDetailsServiceImpl = userDetailsServiceImpl;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String jwt = getJwtFromRequest(request);

    if (StringUtils.hasText(jwt)) {
      UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(jwt);

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

    return "";
  }

}
