package com.sirnoob.productservice.configuration;

import java.util.Arrays;
import java.util.Map;

import com.sirnoob.productservice.security.JwtProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Import({JwtProvider.class})
@Configuration
public class CustomConfigServerRequest {

  @Autowired
  private ConfigClientProperties configClientProperties;
  @Autowired
  private JwtProvider jwtProvider;

  Logger log = LoggerFactory.getLogger(CustomConfigServerRequest.class);

  public ConfigClientProperties configClientProperties() {
    return configClientProperties;
  }

  @Bean
  public ConfigServicePropertySourceLocator configServicePropertySourceLocator() {
    ConfigClientProperties clientProperties = configClientProperties();
    clientProperties.setUsername("product-service");
    var sourceLocator = new ConfigServicePropertySourceLocator(clientProperties);
    sourceLocator.setRestTemplate(getSecureRestTemplate(clientProperties));
    return sourceLocator;
  }

  private RestTemplate getSecureRestTemplate(ConfigClientProperties clientProperties) {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setReadTimeout(clientProperties.getRequestReadTimeout());

    RestTemplate template = new RestTemplate(requestFactory);

    clientProperties.getHeaders().remove(ConfigClientProperties.AUTHORIZATION);

    template.setInterceptors(Arrays.<ClientHttpRequestInterceptor>asList(new ConfigServicePropertySourceLocator.GenericRequestHeaderInterceptor
            (Map.of("authorization", "Bearer " + jwtProvider.generateToken()))));

    return template;
  }
}
