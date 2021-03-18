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
import java.util.List;

import javax.annotation.PostConstruct;

import com.sirnoob.authservice.domain.Token;
import com.sirnoob.authservice.domain.User;
import com.sirnoob.authservice.exception.JwtProviderException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import reactor.core.publisher.Mono;

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
      throw new JwtProviderException("Exception occured while loading keystore");
    }
  }

  public String generateAccessToken(User user, String issuer) {
    return Jwts.builder()
          .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
          .claim("role", List.of(user.getRole()))
          .setSubject(user.getUsername())
          .setIssuer(issuer)
          .setIssuedAt(new Date())
          .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
          .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
          .compact();
  }

  public String generateRefreshToken(String userName, String issuer) {
    return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setHeaderParam("act", "ref")
            .setSubject(userName)
            .setIssuer(issuer)
            .setIssuedAt(new Date())
            .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
            .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
            .compact();
  }

  public String getUsernameFromJwt(String token) {
    return getClaims(token).getSubject();
  }

  public Mono<String> validateToken(Token token, String dbAccessToken) {
    var expiration = checkExpiration(token.getRefreshToken()) && checkExpiration(token.getAccessToken());
    var headers = checkHeaders(token.getAccessToken());
    var issuer = checkIssuer(token, dbAccessToken);

    return expiration && headers && issuer ? Mono.just(token.getAccessToken()) : Mono.empty();
  }

  public boolean checkExpiration(String token) {
    return getClaims(token).getExpiration().after(new Date());
  }

  public boolean checkHeaders(String token) {
    var headers = getHeaders(token);
    if (headers.get("alg") != null && !headers.get("alg").toString().equals(SignatureAlgorithm.RS256.toString())){
      return false;
    }

    if (headers.getType() != null && !headers.getType().equals(Header.JWT_TYPE)) {
      System.out.println(headers.getType());
      return false;
    }

    return true;
  }

  private boolean checkIssuer(Token token, String dbAccessToken) {
    var issuerRef = getClaims(token.getRefreshToken()).getIssuer();
    var issuerAcc = getClaims(token.getAccessToken()).getIssuer();
    var issuerDbAcc = getClaims(dbAccessToken).getIssuer();
	  return issuerRef.equals(issuerDbAcc) && issuerAcc.equals(issuerDbAcc);
  }

  private Header getHeaders(String token) {
    return Jwts.parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(token).getHeader();
  }

  public Claims getClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(getPublicKey()).build().parseClaimsJws(token).getBody();
  }

  private PrivateKey getPrivateKey() {
    try {
      return (PrivateKey) keyStore.getKey("auth-service", "auth-service".toCharArray());
    } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
      throw new JwtProviderException("Exception occured while retreiving private key from keystore");
    }
  }

  private PublicKey getPublicKey() {
    try {
      return keyStore.getCertificate("auth-service").getPublicKey();
    } catch (KeyStoreException e) {
      throw new JwtProviderException("Exception occured while retreiving public key from keystore");
    }
  }

  public Instant getJwtExpirationTime() {
    return Instant.now().plusMillis(jwtExpirationInMillis);
  }
}
