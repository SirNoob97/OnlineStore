package com.sirnoob.productservice.configuration;

import java.io.IOException;
import java.util.Arrays;

import com.sirnoob.productservice.security.JwtProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CustomConfigServerRequest extends ConfigServicePropertySourceLocator{

  private final JwtProvider jwtProvider;
  private final Environment environment;

  Logger log = LoggerFactory.getLogger(CustomConfigServerRequest.class);

  public CustomConfigServerRequest (ConfigClientProperties clientProperties, Environment environment, JwtProvider jwtProvider){
    super(clientProperties);
    this.environment = environment;
    this.jwtProvider = jwtProvider;
  }

  @Bean
  public ConfigClientProperties clientProperties() {
    ConfigClientProperties client = new ConfigClientProperties(this.environment);
    client.setEnabled(false);
    return client;
  }

  @Bean
  public ConfigServicePropertySourceLocator configServicePropertySourceLocator() {
    ConfigClientProperties clientProperties = clientProperties();
    var sourceLocator = new ConfigServicePropertySourceLocator(clientProperties);
    sourceLocator.setRestTemplate(getCustomRestTemplate(clientProperties));
    return sourceLocator;
  }

  private RestTemplate getCustomRestTemplate(ConfigClientProperties clientProperties) {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setReadTimeout(60000);

log.info(jwtProvider.generateToken());

    RestTemplate template = new RestTemplate(requestFactory);
    template.setInterceptors(Arrays.<ClientHttpRequestInterceptor>asList(new AuthorizationToken(jwtProvider.generateToken())));

    return template;
  }

  private static class AuthorizationToken implements ClientHttpRequestInterceptor {

    private final String authenticationToken;

    public AuthorizationToken(String authenticationToken) {
      this.authenticationToken = authenticationToken == null ? "" : authenticationToken;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
      request.getHeaders().add("Authorization", authenticationToken);
      return execution.execute(request, body);
    }
  }
}
