package com.sirnoob.authservice.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;

import javax.annotation.PostConstruct;

import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.exception.CustomException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtProvider {

  private KeyStore keyStore;

  @Value("${jwt.expiration.time}")
  private Long jwtExpirationInMillis;

  @PostConstruct
  public void init() {
    try {
      keyStore = KeyStore.getInstance("JKS");
      InputStream resourceAStream = getClass().getResourceAsStream("/auth-service-keystore.jks");
      keyStore.load(resourceAStream, "auth-service".toCharArray());
    } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
      throw new CustomException("Exception occured while loading keystore");
    }
  }



  public String generateToken(User user) {
    return Jwts.builder().setSubject(user.getUsername()).signWith(getPrivateKey())
        .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis))).compact();
  }

  public String generateTokenWithUserName(String userName) {
    return Jwts.builder().setSubject(userName).setIssuedAt(Date.from(Instant.now())).signWith(getPrivateKey())
        .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis))).compact();
  }

  public String getUsernameFromJwt(String token) {
    return getClaims(token).getSubject();
  }

  public boolean validateToken(String token) {
    return getClaims(token).getExpiration().after(new Date());
  }

  public Claims getClaims(String token){
    return Jwts.parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(token).getBody();
  }



  private PrivateKey getPrivateKey() {
    try {
      return (PrivateKey) keyStore.getKey("auth-service", "auth-service".toCharArray());
    } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
      throw new CustomException("Exception occured while retreiving private key from keystore");
    }
  }

  private PublicKey getPublicKey() {
    try {
      return keyStore.getCertificate("auth-service").getPublicKey();
    } catch (KeyStoreException e) {
      throw new CustomException("Exception occured while retreiving public key from keystore");
    }
  }
}
