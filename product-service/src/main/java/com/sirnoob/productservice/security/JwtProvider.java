package com.sirnoob.productservice.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.sirnoob.JwtProviderException;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtProvider {

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



  public String generateToken() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", List.of("product-service"));
    return Jwts.builder().setClaims(claims).setSubject("product-service").setIssuedAt(new Date())
        .signWith(getPrivateKey(), SignatureAlgorithm.RS256).compact();
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
      return (PrivateKey) keyStore.getKey(ONLINE_STORE, ONLINE_STORE.toCharArray());
    } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
      throw new JwtProviderException("Exception occured while retreiving private key from keystore");
    }
  }

  private PublicKey getPublicKey() {
    try {
      return keyStore.getCertificate(ONLINE_STORE).getPublicKey();
    } catch (KeyStoreException e) {
      throw new JwtProviderException("Exception occured while retreiving public key from keystore");
    }
  }

  //public Instant getJwtExpirationTime() {
    //return Instant.now().plusMillis(jwtExpirationInMillis);
  //}
}
