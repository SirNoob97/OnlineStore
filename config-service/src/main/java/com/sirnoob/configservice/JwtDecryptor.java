package com.sirnoob.configservice;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Component
public class JwtDecryptor {

  Logger log = LoggerFactory.getLogger(JwtDecryptor.class);

  private KeyStore keyStore;
  private static final String ONLINE_STORE = "online-store";

  @PostConstruct
  public void init() {
    final String JKS = "JKS";
    final String ONLINE_STORE_JKS = "/online-store-keystore.jks";
    try {
      keyStore = KeyStore.getInstance(JKS);
      InputStream resourceAStream = getClass().getResourceAsStream(ONLINE_STORE_JKS);
      keyStore.load(resourceAStream, ONLINE_STORE.toCharArray());
    } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
      throw new JwtException("Exception occured while loading keystore");
    }
  }

  public String getUsernameFromJwt(String token) {
    return getClaims(token).getSubject();
  }

  public Claims getClaims(String token){
    try {
      return Jwts.parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(token).getBody();
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return null;
  }

  private PublicKey getPublicKey() {
    try {
      return keyStore.getCertificate(ONLINE_STORE).getPublicKey();
    } catch (KeyStoreException e) {
      throw new JwtException("Exception occured while retreiving public key from keystore");
    }
  }
}
