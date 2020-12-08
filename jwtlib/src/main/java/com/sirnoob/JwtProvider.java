package com.sirnoob;

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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtProvider {

  private static final String PRODUCT_SERVICE = "product-service";
  private static final String JKS = "JKS";
  private static final String ONLINE_STRORE = "online-store";
  private static final String ONLINE_STRORE_JKS = "/oneline-store-keystore.jks";

  private KeyStore keyStore;

  @CustomPostConstruct
  public void init() {
    try {
      keyStore = KeyStore.getInstance(JKS);
      InputStream resourceAStream = getClass().getResourceAsStream(ONLINE_STRORE_JKS);
      keyStore.load(resourceAStream, ONLINE_STRORE.toCharArray());
    } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
      throw new RuntimeException("Exception occured while loading keystore");
    }
  }



  public String generateToken() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", List.of(PRODUCT_SERVICE));
    return Jwts.builder().setClaims(claims).setSubject(PRODUCT_SERVICE).setIssuedAt(new Date())
        .signWith(getPrivateKey(), SignatureAlgorithm.RS256).compact();
  }

  public String getUsernameFromJwt(String token) {
    return getClaims(token).getSubject();
  }

  public boolean validateToken(String token) {
    return true;
  }

  public Claims getClaims(String token){
    return Jwts.parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(token).getBody();
  }



  private PrivateKey getPrivateKey() {
    try {
      return (PrivateKey) keyStore.getKey(ONLINE_STRORE, ONLINE_STRORE.toCharArray());
    } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
      throw new JwtProviderException("Exception occured while retreiving private key from keystore");
    }
  }

  private PublicKey getPublicKey() {
    try {
      return keyStore.getCertificate(ONLINE_STRORE).getPublicKey();
    } catch (KeyStoreException e) {
      throw new JwtProviderException("Exception occured while retreiving public key from keystore");
    }
  }
}
